/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.core;

import cz.zcu.kiv.os.Utilities;
import cz.zcu.kiv.os.core.Process;
import cz.zcu.kiv.os.core.device.IInputDevice;
import cz.zcu.kiv.os.core.device.IOutputDevice;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bydga, Jiri Zikmund
 */
public abstract class Process extends Observable {

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
	
	public void setInputStream(IInputDevice stream)
	{
		this.properties.inputStream = stream;
	}
	
	public void setOutputStream(IOutputDevice stream)
	{
		this.properties.outputStream = stream;
	}
	
	public void setErrorStream(IOutputDevice stream)
	{
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
		this.workingThread.interrupt();
		this.setChanged();
		this.notifyObservers(Process.STATE_STOP);
		
		String message = "Process " + Process.this.getClass().getName() + " was stopped";
		if( reason != null ) {
			message += ", because " + reason;
		}
		Utilities.log(message);
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
}