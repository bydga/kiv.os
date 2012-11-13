/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.core;

import cz.zcu.kiv.os.core.interrupts.KeyboardEvent;
import cz.zcu.kiv.os.core.interrupts.Signals;
import cz.zcu.kiv.os.Utilities;
import cz.zcu.kiv.os.core.device.AbstractIODevice;
import cz.zcu.kiv.os.core.device.IInputDevice;
import cz.zcu.kiv.os.core.device.IOutputDevice;
import cz.zcu.kiv.os.core.filesystem.FileManager;
import cz.zcu.kiv.os.core.interrupts.Interrupt;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * observable - because of process cleanup during process termination observer because of system/keyboard events
 *
 * @author bydga, Jiri Zikmund
 */
public abstract class Process extends Observable implements Observer {

	protected enum ProcessState {

		PREPARED, RUNNING, FINISHED_KILLED, FINISHED_OK,
	}
	public static final String PROCESS_PACKAGE = "cz.zcu.kiv.os.processes";
	protected Thread workingThread;
	protected int pid;
	protected List<Process> children;
	ProcessProperties properties;
	protected ProcessState processState = ProcessState.PREPARED;
	protected int exitCode = 0;

	public String getWorkingDir() {
		return this.properties.workingDir;
	}

	public Process getParent() {
		return this.properties.parent;
	}

	protected int getExitCode() throws InterruptedException {
		if (this.processState == ProcessState.PREPARED || this.processState == ProcessState.RUNNING) {
			this.workingThread.join();
		}
		return this.exitCode;
	}

	public ProcessState getProcessState() {
		return this.processState;
	}

	public void setWorkingDir(String workingDir) throws Exception {
		//resolve destinating path
		if (Core.getInstance().getServices().directoryExists(this, workingDir)) {
			String root;
			if (workingDir.startsWith("/")) {
				root = "/";
			} else {
				root = this.getWorkingDir();
			}
			this.properties.workingDir = FileManager.resolveRelativePath(root, workingDir);
		} else {
			this.getOutputStream().writeLine("Desired path doesn't exist.");
		}
	}

	public void setInputStream(IInputDevice stream) {
		this.properties.inputStream = stream;
	}

	public void setOutputStream(IOutputDevice stream) {
		this.properties.outputStream = stream;
	}

	public void setErrorStream(IOutputDevice stream) {
		this.properties.errorStream = stream;
	}

	protected synchronized IInputDevice getInputStream() throws InterruptedException {
		while(!this.isForegroundProcess() && this.properties.inputStream.isStdStream()) { //only foreground process can read from stdin
				this.wait();
		}
		return this.properties.inputStream;
	}

	public IOutputDevice getOutputStream() {
		return this.properties.outputStream;
	}

	public IOutputDevice getErrorStream() {
		return this.properties.errorStream;
	}

	public String[] getArgs() {
		return this.properties.args;
	}

	public ThreadGroup getProcessGroup() {
		return this.properties.processGroup.getGroup();
	}

	public int getPid() {
		return this.pid;
	}

	protected abstract void run(String[] args) throws Exception;

	protected final void init(int pid, ProcessProperties properties) {
		this.children = new ArrayList<Process>();
		this.pid = pid;
		this.properties = properties;
		this.workingThread = new Thread(this.properties.processGroup.getGroup(), new Runnable() {
			@Override
			public void run() {
				try {
					Process.this.processState = ProcessState.RUNNING;
					Utilities.log("Process " + Process.this.getClass().getName() + " starting");
					Process.this.run(Process.this.properties.args);
					Process.this.processState = Process.ProcessState.FINISHED_OK;
					Utilities.log("Process " + Process.this.getClass().getName() + " finished successfully");

				} catch (InterruptedException ex) {
					Process.this.processState = Process.ProcessState.FINISHED_KILLED;
					Utilities.log("Process " + Process.this.getClass().getSimpleName() + " stopped manually (got InterruptedException).");
				} catch (Exception ex) {
					Utilities.log("Process " + Process.this.getClass().getSimpleName() + " exited with unhandled exception: " + ex.getClass().getSimpleName() + ", message: " + ex.getMessage() + "\n ");
				} finally {
					Process.this.getOutputStream().EOF();//im not gonna write into this anymore
					Process.this.getErrorStream().EOF();//im not gonna write into this anymore
					Process.this.setChanged();
					Process.this.notifyObservers();
				}
			}
		}, "process-" + this.getClass().getName());
	}

	protected void checkForStop() throws InterruptedException {
		if (this.properties.processGroup.getShouldStop()) {
			throw new InterruptedException("manual termination");
		}
	}

	protected final void stop() {
		this.properties.processGroup.stop();
	}

	protected final void start() {
		this.workingThread.start();
	}

	protected boolean isForegroundProcess() {
		return !this.properties.isBackgroundProcess;
	}

	protected void setForegroundProcess(boolean value) {
		this.properties.isBackgroundProcess = !value;
	}

	/**
	 * Add children process id
	 */
	protected final void addChildren(Process p) {
		this.children.add(p);
	}

	/**
	 * Remove children process id
	 */
	protected final void removeChildren(Process p) {
		this.children.remove(p);
	}

	private void handleSignal(Signals sig) {
		switch (sig) {

			case SIGQUIT:
				this.handleSignalSIGQUIT();
				break;

			case SIGTERM:
				this.handleSignalSIGTERM();
				break;

			default:

				break;
		}
	}

	protected void handleSignalSIGTERM() {
		this.stop();
	}

	protected void handleSignalSIGQUIT() {
		try {
			if(this.getInputStream() instanceof AbstractIODevice) { //if input device writable
				AbstractIODevice io = (AbstractIODevice) this.getInputStream();
				if(io.isStdStream()) { //stop reading from this point
					io.EOF(); //stop reading from this point
				}
			}
		} catch (InterruptedException ex) {
			Utilities.log("dafuq?");
		}
	}

	protected void handleKeyboardEvent(KeyboardEvent e) {
	}

	/**
	 * Implementation of observer pattern. Awaits special events from dispatcher and resends it to apropriate process.
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
