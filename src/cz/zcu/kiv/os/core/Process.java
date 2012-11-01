/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.core;

import cz.zcu.kiv.os.core.interrupts.KeyboardEvent;
import cz.zcu.kiv.os.core.interrupts.Signals;
import cz.zcu.kiv.os.Utilities;
import cz.zcu.kiv.os.core.Process;
import cz.zcu.kiv.os.core.device.IInputDevice;
import cz.zcu.kiv.os.core.device.IOutputDevice;
import cz.zcu.kiv.os.core.interrupts.Interrupt;
import cz.zcu.kiv.os.processes.Echo;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * observable - because of process cleanup during process termination observer because of system/keyboard events
 *
 * @author bydga, Jiri Zikmund
 */
public abstract class Process extends Observable implements Observer {

	public static final int STATE_STOP = 0;
	public static final int STATE_FINISH = 1;
	public static final int STATE_EXCEPTION = 2;
	protected Thread workingThread;
	protected int pid;
	protected List<Process> children;
	private ProcessProperties properties;

	public String getWorkingDir() {
		return this.properties.workingDir;
	}

	public void setWorkingDir(String workingDir) {
		this.properties.workingDir = workingDir;
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

	public boolean isRunning() {
		return this.workingThread == null ? false : this.workingThread.isAlive();
	}

	public void join() {
		try {
			this.workingThread.join();
		} catch (InterruptedException ex) {
			Logger.getLogger(Process.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public IInputDevice getInputStream() {
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
		return this.properties.processGroup;
	}

	public int getPid() {
		return this.pid;
	}

	protected abstract void run(String[] args) throws Exception;

	protected final void init(int pid, ProcessProperties properties) {
		this.children = new ArrayList<Process>();
		this.pid = pid;
		this.properties = properties;
		this.workingThread = new Thread(this.properties.processGroup, new Runnable() {
			@Override
			public void run() {
				try {
					Utilities.log("Process " + Process.this.getClass().getName() + " starting");
					Process.this.run(Process.this.properties.args);

					//TODO: some condition - close streams fwded into files or pipes, DO NOT CLOSE STDIN/OUT
//					Process.this.stdOut.close();
//					Process.this.stdIn.close();
					Process.this.setChanged();
					Process.this.notifyObservers(Process.STATE_FINISH);
					synchronized (Process.this) {
						Process.this.notifyAll();
					}
					Utilities.log("Process " + Process.this.getClass().getName() + " finished");
				} catch (Exception ex) {
					Process.this.setChanged();
					Process.this.notifyObservers(Process.STATE_EXCEPTION);
					Utilities.log("Process " + Process.this.getClass().getName() + " exited with exception " + ex.getMessage());
				}
			}
		}, "process-" + this.getClass().getName());
	}

	protected final void stop(String reason) {
		if (this.workingThread.isAlive()) {
			this.workingThread.stop();
			this.setChanged();
			this.notifyObservers(Process.STATE_STOP);
			Utilities.log("Process " + Process.this.getClass().getName() + " terminated manually ");

			String message = "Process " + Process.this.getClass().getName() + " was stopped";
			if (reason != null) {
				message += ", because " + reason;
			}
			Utilities.log(message);
		}
	}

	protected final void stop() {
		this.stop(null);
	}

	protected final void start() {
		this.workingThread.start();

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
	}

	protected void handleKeyboardEvent(KeyboardEvent e) {
	}

	/**
	 * Implementation of observer pattern. Awaits special events from dispatcher and processes them.
	 */
	@Override
	public final void update(Observable o, Object arg) {
		Interrupt interrupt = (Interrupt) arg;
		if (this == interrupt.getReceiver()) {
			Utilities.log(this.getClass().getSimpleName() + "got " + interrupt.getInterrupt().toString());
			if (arg instanceof Signals) {
				this.handleSignal((Signals) arg);
			} else if (arg instanceof KeyboardEvent) {
				this.handleKeyboardEvent((KeyboardEvent) arg);
			}
		}

	}
}