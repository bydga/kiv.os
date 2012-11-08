package cz.zcu.kiv.os.core;

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
 *
 * @author bydga
 */
public class Core {

	private static Core instance;
	private ICoreServices services;
	private ProcessManager processManager;
	private FileManager fileManager;
	private SwingTerminal terminal;
	private SignalDispatcher dispatcher;

	public void setTerminal(SwingTerminal t) {
		this.terminal = t;
	}

	public static synchronized Core getInstance() {
		if (Core.instance == null) {
			Core.instance = new Core();
		}

		return Core.instance;
	}

	private Core() {
		this.services = new Core.CoreServices();
		this.processManager = new ProcessManager();

		String separator = System.getProperty("file.separator");
		this.fileManager = new FileManager(System.getProperty("user.home") + separator + "os" + separator);
		this.dispatcher = new SignalDispatcher();
	}

	public synchronized ICoreServices getServices() {
		return this.services;
	}

	private class CoreServices implements ICoreServices {

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
		public void dispatchKeyboardEvent(KeyboardEvent evt) {
			Process p = Core.this.processManager.getForegroundProcess();
			Core.this.dispatcher.dispatchKeyboardEvent(p, evt);
		}

		@Override
		public String getTerminalCommand() {
			return Core.this.terminal.getText();
		}

                @Override
                public AbstractIODevice createPipe() {
                    return new PipeDevice(false);
                }

		@Override
		public List<ProcessInfo> getProcessTableData() {
			List<ProcessInfo> output = new ArrayList<ProcessInfo>();
			Map<Integer, ProcessTableRecord> processTable = Core.this.processManager.getProcessTable();
			for (Map.Entry<Integer, ProcessTableRecord> entry : processTable.entrySet())
			{
				output.add(new ProcessInfo(entry.getValue()));
			}
			
			return output;
		}

		@Override
		public int readProcessExitCode(Process p) {
			int exit = Core.this.processManager.readProcessExitCode(p);
			return exit;
		}

		@Override
		public List<File> listFiles(Process caller, String dir) {
			return Core.this.fileManager.listFiles(dir, caller.getWorkingDir());
		}
	}
}
