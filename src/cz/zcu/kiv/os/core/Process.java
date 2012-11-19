package cz.zcu.kiv.os.core;

import cz.zcu.kiv.os.core.interrupts.KeyboardEvent;
import cz.zcu.kiv.os.core.interrupts.Signals;
import cz.zcu.kiv.os.Utilities;
import cz.zcu.kiv.os.core.device.AbstractIODevice;
import cz.zcu.kiv.os.core.device.IInputDevice;
import cz.zcu.kiv.os.core.device.IOutputDevice;
import cz.zcu.kiv.os.core.filesystem.FileManager;
import cz.zcu.kiv.os.core.interrupts.Interrupt;
import java.util.*;

/**
 * Base class for all processes. Handles common issues - like input/output streams, default signal handling, thread
 * creation and cleanup. observable - because of process cleanup during process termination. observer - because of
 * system/keyboard events
 *
 * @author bydga, Jiri Zikmund
 */
public abstract class Process extends Observable implements Observer {

	protected enum ProcessState {

		PREPARED, RUNNING, FINISHED,
	}
	/**
	 * Package, where reflection looks for the user processess.
	 */
	public static final String PROCESS_PACKAGE = "cz.zcu.kiv.os.processes";
	protected Thread workingThread;
	protected int pid;
	protected List<Process> children;
	protected ProcessProperties properties;
	protected ProcessState processState = ProcessState.PREPARED;
	protected int exitCode = 0;

	/**
	 * Returns actual working directory.
	 *
	 * @return Actual working directory
	 */
	public String getWorkingDir() {
		return this.properties.getWorkingDir();
	}

	/**
	 * Returns parent process or null if this is Init process
	 *
	 * @return Parent process object or null
	 */
	public Process getParent() {
		return this.properties.parent;
	}

	/**
	 * Returns user that started this process
	 *
	 * @return
	 */
	public String getUser() {
		return this.properties.user;
	}

	/**
	 * Returns all children of this process in unmodifiable collection.
	 *
	 * @return List of all child processess of this process.
	 */
	public List<Process> getChildren() {
		synchronized (this.children) {
			return Collections.unmodifiableList(this.children);
		}
	}

	/**
	 * Blocking operation that joins on working thread and returns exitcode of the thread.
	 *
	 * @return Process's exit code.
	 * @throws InterruptedException
	 */
	protected int getExitCode() throws InterruptedException {
		while (this.processState == ProcessState.PREPARED || this.processState == ProcessState.RUNNING) {
			workingThread.join();
		}
		return this.exitCode;
	}

	/**
	 * Returns current process state.
	 *
	 * @return Current process state.
	 */
	public ProcessState getProcessState() {
		return this.processState;
	}

	/**
	 * Changes curent working directory of this process.
	 *
	 * @param workingDir Relative or absolute path to a new working directory.
	 * @throws Exception when the desired path doesn't exist.
	 */
	public void setWorkingDir(String workingDir) throws Exception {
		//resolve destinating path
		if (Core.getInstance().getServices().directoryExists(this, workingDir)) {
			String root;
			if (workingDir.startsWith("/")) {
				root = "/";
			} else {
				root = this.getWorkingDir();
			}
			this.properties.setWorkingDir(FileManager.resolveRelativePath(root, workingDir));
		} else {
			this.getOutputStream().writeLine("Desired path doesn't exist.");
		}
	}

	public void waitToGetForeground() throws InterruptedException {
		while (!this.isForegroundProcess()) {
			synchronized (this.properties) {
				this.properties.wait();
			}
		}
	}

	/**
	 * Notifies of change to
	 */
	public void notifyIsForeground() {
		synchronized (this.properties) {
			this.properties.notifyAll();
		}
	}

	/**
	 * Returns stream that is representing input stream of the process. Can be one of stdIn, pipe from another process
	 * or a file.
	 *
	 * @return Device representing process's inputStream.
	 * @throws InterruptedException
	 */
	protected IInputDevice getInputStream() throws InterruptedException {
		if (this.properties.inputStream.isStdStream()) { //only foreground process can read from stdin
			this.waitToGetForeground();
		}
		return this.properties.inputStream;
	}

	/**
	 * Returns stream that is representing output stream of the process. Can be one of stdOut, pipe to another process
	 * or a file.
	 *
	 * @return Device representing process's outputStream.
	 * @throws InterruptedException
	 */
	public IOutputDevice getOutputStream() {
		return this.properties.outputStream;
	}

	/**
	 * Returns stream that is representing output stream of the process. Can be one of stdErr, pipe to another process
	 * or a file.
	 *
	 * @return Device representing process's errorStream.
	 * @throws InterruptedException
	 */
	public IOutputDevice getErrorStream() {
		return this.properties.errorStream;
	}

	/**
	 * Gets the ProcessGroup this process belongs to.
	 *
	 * @return Process's ProcessGroup
	 */
	public ThreadGroup getProcessGroup() {
		return this.properties.processGroup.getGroup();
	}

	/**
	 * Returns pid of this process.
	 *
	 * @return int pid.
	 */
	public int getPid() {
		return this.pid;
	}

	/**
	 * Someting like main in C-like programming languages. Method that represents each process's entry point.
	 *
	 * @param args Arguments passed to the program. First argument is the name of the process.
	 * @throws Exception
	 */
	protected abstract void run(String[] args) throws Exception;

	/**
	 * Initializes the process object, wraps the thread run, catches derived process's exceptions and handles cleanup
	 * after the process terminates.
	 *
	 * @param pid the integer representing process id of the process.
	 * @param properties ProcessProperties object defining the definition of the process startup.
	 */
	protected final void init(int pid, ProcessProperties properties) {
		this.children = new ArrayList<Process>();
		this.pid = pid;
		this.properties = properties;
		//define the thread runnable entry
		this.workingThread = new Thread(this.properties.processGroup.getGroup(), new Runnable() {
			@Override
			public void run() {
				try {
					//set state, do the process run, set state, finished.
					Process.this.processState = ProcessState.RUNNING;
					Utilities.log("Process " + Process.this.getClass().getName() + " starting");
					Process.this.run(Process.this.properties.args);
					Utilities.log("Process " + Process.this.getClass().getName() + " finished successfully");

				} catch (InterruptedException ex) {
					Utilities.log("Process " + Process.this.getClass().getSimpleName() + " stopped manually (got InterruptedException).");
				} catch (Exception ex) {
					Utilities.log("Process " + Process.this.getClass().getSimpleName() + " exited with unhandled exception: " + ex.getClass().getSimpleName() + ", message: " + ex.getMessage() + "\n ");
				} finally {
					//do cleanup and notify processmanager of the finish.
					Process.this.processState = ProcessState.FINISHED;
					Process.this.getOutputStream().EOF();//im not gonna write into this anymore
					Process.this.getErrorStream().EOF();//im not gonna write into this anymore
					Process.this.setChanged();
					Process.this.notifyObservers();
				}
			}
		}, "process-" + this.getClass().getName());
	}

	/**
	 * Method that derived processess MUST check when running in a "longer term" loop. Throws interrupted exception -
	 * just like when the thread is interrupted from a blocking call. This allows us to react on forced exit of a
	 * process independently on the state, what was the process curently doing.
	 *
	 * @throws InterruptedException
	 */
	protected void checkForStop() throws InterruptedException {
		if (this.properties.processGroup.getShouldStop()) {
			throw new InterruptedException("manual termination");
		}
	}

	/**
	 * Signals the process and the whole process group to stop. Doesn't actually stop the thread, only sets the flag
	 * that the processes must check with method checkForStop.
	 */
	protected final void stop() {
		this.properties.processGroup.stop();
	}

	/**
	 * Starts the thread.
	 */
	protected final void start() {
		this.workingThread.start();
	}

	/**
	 * Returns if the process is curently a background process.
	 *
	 * @return
	 */
	protected boolean isForegroundProcess() {
		return !this.properties.isBackgroundProcess();
	}

	/**
	 * Tells this process that its currently a foreground process
	 *
	 * @param value Indicating is the process is on FG or not.
	 */
	protected void setForegroundProcess(boolean value) {
		this.properties.setBackgroundProcess(!value);
	}

	/**
	 * Adds new child process to this process's children collection.
	 */
	protected final void addChildren(Process p) {
		synchronized (this.children) {
			this.children.add(p);
		}
	}

	/**
	 * Removes existing child process from this process's children collection.
	 */
	protected final void removeChildren(Process p) {
		synchronized (this.children) {
			this.children.remove(p);
		}
	}

	/**
	 * Base handler of incomming signal. Calls apropriate signal handler - some of them might be overriden by derived
	 * process and some can not (like SIGKILL).
	 *
	 * @param sig Received signal.
	 */
	private void handleSignal(Signals sig) {
		switch (sig) {

			case SIGQUIT:
				this.handleSignalSIGQUIT();
				break;

			case SIGTERM:
				this.handleSignalSIGTERM();
				break;

			case SIGKILL:
				this.handleSignalSIGKILL();

			default:

				break;
		}
	}

	/**
	 * Default handler for SIGTERM signal.
	 */
	protected void handleSignalSIGTERM() {
		this.stop();
	}

	/**
	 * Default handler for SIGKILL signal - cannot be overriden.
	 */
	private void handleSignalSIGKILL() {
		this.stop();
	}

	/**
	 * Default handler for SIGQUIT signal.
	 */
	protected void handleSignalSIGQUIT() {
		try {
			if (this.getInputStream() instanceof AbstractIODevice) { //if input device writable
				AbstractIODevice io = (AbstractIODevice) this.getInputStream();
				if (io.isStdStream()) { //stop reading from this point
					io.EOF(); //stop reading from this point
				}
			}
		} catch (InterruptedException ex) {
			Utilities.log("dafuq?");
		}
	}

	/**
	 * Default handler for any keyboard event.
	 */
	protected void handleKeyboardEvent(KeyboardEvent e) {
	}

	/**
	 * Nonblocking call that iterates over children and reads exit code of the finished ones.
	 *
	 * @throws InterruptedException
	 */
	protected void checkForFinishedChildren() throws InterruptedException {
		//check for finished children
		for (Process p : new ArrayList<Process>(this.children)) {
			if (p.getProcessState() == Process.ProcessState.FINISHED) {
				int res = Core.getInstance().getServices().readProcessExitCode(p);
				try {
					this.getOutputStream().writeLine("Process " + p.getPid() + " " + p.getClass().getSimpleName() + " exited: " + res);
				} catch (Exception ex) {
					//stdout is down, log the result
				} finally {
					Utilities.log("Process " + p.getPid() + " " + p.getClass().getSimpleName() + " exited: " + res);
				}
			}
		}
	}

	/**
	 * Implementation of observer pattern. Awaits special events from dispatcher and handles it in case this process should receive it.
	 */
	@Override
	public final void update(Observable o, Object arg) {
		Interrupt interrupt = (Interrupt) arg;
		if (this == interrupt.getReceiver()) {
			if (interrupt.getInterrupt() instanceof Signals) {
				this.handleSignal((Signals) interrupt.getInterrupt());
			} else if (interrupt.getInterrupt() instanceof KeyboardEvent) {
				this.handleKeyboardEvent((KeyboardEvent) interrupt.getInterrupt());
			}
		}

	}
}
