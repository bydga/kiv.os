package cz.zcu.kiv.os.core;

import cz.zcu.kiv.os.Utilities;
import cz.zcu.kiv.os.core.Process;
import cz.zcu.kiv.os.core.device.*;
import cz.zcu.kiv.os.core.filesystem.FileManager;
import cz.zcu.kiv.os.core.filesystem.FileMode;
import cz.zcu.kiv.os.core.interrupts.KeyboardEvent;
import cz.zcu.kiv.os.core.interrupts.Signals;
import cz.zcu.kiv.os.terminal.SwingTerminal;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Main class of the OS simulation. Wraps all standard inputs/outpus, 
 * terminal and other necessary objects for system execution. 
 * @author bydga
 */
public class Core {

	private static Core instance;
	private ICoreServices services;
	private ProcessManager processManager;
	private FileManager fileManager;
	private SignalDispatcher dispatcher;
	private volatile boolean osRunning;
	private SwingTerminal terminal;
	private IOutputDevice stdOut;
	private IInputDevice stdIn;
	private IOutputDevice stdErr;

	/**
	 * Returns singleton instance of the Core.
	 * @return 
	 */
	public static synchronized Core getInstance() {
		if (Core.instance == null) {
			Core.instance = new Core();
		}

		return Core.instance;
	}

	private Core() {
		this.osRunning = true;
		this.services = new Core.CoreServices();
		this.processManager = new ProcessManager();

		String separator = System.getProperty("file.separator");
		this.fileManager = new FileManager(System.getProperty("user.home") + separator + "os" + separator);
		this.dispatcher = new SignalDispatcher();
		try {
			this.initStdStreams();
		} catch (IOException ex) {
			//fatal error TODO
		}
	}

	private AbstractIODevice createStdDevice() throws IOException {
		return new PipeDevice(true);
	}

	private void initStdStreams() throws IOException {
		stdIn = this.createStdDevice();
		stdOut = this.createStdDevice();
		stdErr = this.createStdDevice();
	}

	/**
	 * Creates and openes new terminal window. Adds the terminal stream to the caller process.
	 * @param caller 
	 */
	public void openTerminalWindow(Process caller) {
		this.terminal = new SwingTerminal((IInputDevice) this.stdOut, (IOutputDevice) this.stdIn);
		this.processManager.addStreamToProcess(caller.getPid(), this.terminal);
	}

	/**
	 * Gets the core's standard input stream.
	 * @return 
	 */
	public IInputDevice getStdIn() {
		return this.stdIn;
	}

	/**
	 * Gets the core's standard output stream.
	 * @return 
	 */
	public IOutputDevice getStdOut() {
		return this.stdOut;
	}

	/**
	 * Gets the core's standard error stream.
	 * @return 
	 */
	public IOutputDevice getStdErr() {
		return this.stdErr;
	}

	/**
	 * Endpoint for processes when calling any service provided by this operating system.
	 * @return Interface with all supported service calls.
	 */
	public ICoreServices getServices() {
		return this.services;
	}

	/**
	 * Closes all standard streams associated with this OS.
	 * @throws IOException 
	 */
	public void detachOSResources() throws IOException {
		this.getStdIn().detach();
		this.getStdOut().detach();
		this.getStdErr().detach();
	}

	private class CoreServices implements ICoreServices {

		@Override
		public synchronized boolean isRunning() {
			return osRunning;
		}

		@Override
		public Process createProcess(String processName, ProcessProperties properties) throws NoSuchProcessException {
			Process p = Core.this.processManager.createProcess(processName, properties);
			Core.this.dispatcher.addObserver(p);
			return p;
		}

		@Override
		public IInputDevice openFileForRead(Process caller, String path) throws IOException {
			IInputDevice file = (IInputDevice) fileManager.openFile(path, caller.getWorkingDir(), FileMode.READ);
			processManager.addStreamToProcess(caller.getPid(), file);
			return file;
		}

		@Override
		public IOutputDevice openFileForWrite(Process caller, String path, boolean append) throws IOException {
			FileMode mode = append ? FileMode.APPEND : FileMode.WRITE;
			IOutputDevice file = (IOutputDevice) fileManager.openFile(path, caller.getWorkingDir(), mode);
			processManager.addStreamToProcess(caller.getPid(), file);
			return file;
		}

		@Override
		public boolean createDirectory(Process caller, String dirName) {
			return fileManager.createDirectory(dirName, caller.getWorkingDir());
		}

		@Override
		public boolean directoryExists(Process caller, String filename) {
			return fileManager.directoryExists(filename, caller.getWorkingDir());
		}

		@Override
		public void setTerminalCommand(String command) {
			Core.this.terminal.setText(command);
		}

		@Override
		public void dispatchSystemSignal(Signals sig) {
			Process p = Core.this.processManager.getForegroundProcess();
			Core.this.dispatcher.dispatchSystemSignal(p, sig);
		}

		@Override
		public void dispatchSystemSignal(int pid, Signals sig) {
			ProcessTableRecord record = Core.this.processManager.getProcessTable().get(pid);
			if (record != null) {
				Core.this.dispatcher.dispatchSystemSignal(record.getProcess(), sig);
			}
		}

		@Override
		public void dispatchKeyboardEvent(KeyboardEvent evt) {
			Process p = Core.this.processManager.getForegroundProcess();
			Core.this.dispatcher.dispatchKeyboardEvent(p, evt);
		}

		@Override
		public String getTerminalCommand() {
			return Core.this.terminal.getLastLine();
		}

		@Override
		public AbstractIODevice createPipe() {
			return new PipeDevice(false);
		}

		@Override
		public List<ProcessInfo> getProcessTableData() {
			List<ProcessInfo> output = new ArrayList<ProcessInfo>();
			Map<Integer, ProcessTableRecord> processTable = Core.this.processManager.getProcessTable();
			for (Map.Entry<Integer, ProcessTableRecord> entry : processTable.entrySet()) {
				output.add(new ProcessInfo(entry.getValue()));
			}

			return output;
		}

		@Override
		public int readProcessExitCode(Process p) throws InterruptedException {
			int exit = Core.this.processManager.readProcessExitCode(p);
			return exit;
		}

		@Override
		public List<File> listFiles(Process caller, String dir) {
			return Core.this.fileManager.listFiles(dir, caller.getWorkingDir());
		}

		@Override
		public void shutdown(Process caller) {
			shutDownLogger("System received shutdown request.");
			osRunning = false;
			int callerPid = caller == null ? -1 : caller.getPid();	
			try {
				internalShutdown(callerPid);
			} catch (Exception ex) {//something went wrong during the shutdown, log and kill everything
				Utilities.log("Error during shutdown. System collaped.");
				System.exit(0);
			}
		}

		/**
		 * System shutdown processing method.
		 *
		 * First retrieves the current login process. If it doesnt have changes (waiting
		 * for the user to login, it sends SIGTERM signal to the login process)
		 *
		 * Otherwise shell process (child of login) is retrieved and SIGTERM is sent
		 * to all its children except the process calling shutdown.
		 *
		 * Processes have 6 seconds to terminate, after that SIGKILL is sent to all
		 * remaining user processes.
		 * 
		 * @param callerPid
		 * @throws Exception
		 */
		private void internalShutdown(int callerPid) throws Exception {
			Process init = processManager.getProcessTable().get(0).getProcess();

			//send sigterm to all processes
			Process login = init.getChildren().get(0);
			if(!login.getChildren().isEmpty()) {
				Process shell = login.getChildren().get(0);

				shutDownLogger("Sending SIGTERM to user processes.");
				for(Process p : shell.getChildren()) {
					if(p.pid == callerPid) {
						continue;
					}
					Core.getInstance().getServices().dispatchSystemSignal(p.getPid(), Signals.SIGTERM);
				}

				int j = 0;
				//has any process called shutdown?
				int shutDownCount = callerPid == -1 ? 0 : 1;
				while(shell.getChildren().size() > shutDownCount && j < 3) {
					try {
						//wait for processes to terminate
						shutDownLogger("Waiting for processes to die...");
						j++;
						Thread.sleep(2000);
						shell.checkForFinishedChildren();
					} catch (InterruptedException ex) {
						//shutdown cannot be interrupted, go on
					}
				}

				if(shell.getChildren().isEmpty()) {//all child process dead, killing shell
					Core.getInstance().getServices().dispatchSystemSignal(shell.getPid(), Signals.SIGTERM);
					return;
				}

				//all child processes dead except for the one which called shutdown, killing shell
				if(shell.getChildren().size() == 1 && shell.getChildren().get(0).getPid() == callerPid) {
					Core.getInstance().getServices().dispatchSystemSignal(shell.getPid(), Signals.SIGTERM);
					return;
				}

				//killing remaining processes
				shutDownLogger("Killing remaining processes");
				if(init.getChildren().size() > 0) {
					Core.getInstance().getServices().dispatchSystemSignal(login.pid, Signals.SIGKILL);
				}
			} else { //disrupt login
				Core.getInstance().getServices().dispatchSystemSignal(login.getPid(), Signals.SIGTERM);
			}

		}

		/**
		 * Helper method to write system shutdown logs.
		 * Writes to output stream if possible and to log ALWAYS
		 * @param msg message to be written
		 */
		private void shutDownLogger(String msg) {
			try {
				stdOut.writeLine(msg);
			} catch (Exception ex) {
			} finally {
				Utilities.log(msg);
			}
		}

		@Override
		public List<Process> getAllProcessess() {
			List<Process> output = new ArrayList<Process>();
			for (ProcessTableRecord rec : Core.this.processManager.getProcessTable().values()) {
				output.add(rec.getProcess());
			}
			return output;
		}
	}
}
