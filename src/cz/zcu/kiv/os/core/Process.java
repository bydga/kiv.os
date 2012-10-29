/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.core;

import cz.zcu.kiv.os.Utilities;
import cz.zcu.kiv.os.core.device.IInputDevice;
import cz.zcu.kiv.os.core.device.IOutputDevice;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author bydga, george
 */
public abstract class Process extends Observable {

	public static final int STATE_STOP = 0;
	public static final int STATE_FINISH = 1;
	public static final int STATE_EXCEPTION = 2;
	protected Thread workingThread;
	protected int pid;
	protected Process parent;
	protected List<Process> children;
	protected IInputDevice stdIn;
	protected IOutputDevice stdOut;
	protected IOutputDevice stdErr;
	
	public IInputDevice getInputStream()
	{
		return this.stdIn;
	}
	
	public IOutputDevice getOutputStream()
	{
		return this.stdOut;
	}
	
	public IOutputDevice getErrorStream()
	{
		return this.stdErr;
	}

	public int getPid() {
		return this.pid;
	}
	
	protected abstract void run(String[] args) throws Exception;


	protected final void init(int pid, Process parent, final String[] args, IInputDevice stdIn, IOutputDevice stdOut, IOutputDevice stdErr) {
		this.children = new ArrayList<Process>();
		this.pid = pid;
		this.parent = parent;
		this.stdIn = stdIn;
		this.stdOut = stdOut;
		this.stdErr = stdErr;
		this.workingThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Utilities.log("Process " + Process.this.getClass().getName() + " starting");
					Process.this.run(args);
					
					//TODO: some condition - close streams fwded into files or pipes, DO NOT CLOSE STDIN/OUT
//					Process.this.stdOut.close();
//					Process.this.stdIn.close();
					Process.this.setChanged();
					Process.this.notifyObservers(Process.STATE_FINISH);
					Utilities.log("Process " + Process.this.getClass().getName() + " finished");
				} catch (Exception ex) {
					Process.this.setChanged();
					Process.this.notifyObservers(Process.STATE_EXCEPTION);
					Utilities.log("Process " + Process.this.getClass().getName() + " exited with exception" + ex.getMessage());
				}
			}
		}, "process-" + this.getClass().getName());
	}

	protected final void stop() {
		this.workingThread.interrupt();
		this.setChanged();
		this.notifyObservers(Process.STATE_STOP);
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