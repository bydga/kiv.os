/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.core;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author bydga, george
 */
public abstract class Process extends Observable implements Runnable {
	
	protected static final int STATE_STOPPED = 0;
	protected static final int STATE_FINISHED = 0;
	
	protected Thread workingThread;
	
	protected int pid;
	protected int ppid;
	protected int[] children;
	protected InputStream stdIn;
	protected OutputStream stdOut;
	protected OutputStream stdErr;

	@Override
	public abstract void run();
	
	public Process(int pid, int ppid, InputStream stdIn, OutputStream stdOut, OutputStream stdErr, Observer processManager) {
		this.pid = pid;
		this.ppid = ppid;
		this.stdIn = stdIn;
		this.stdOut = stdOut;
		this.stdErr = stdErr;
		this.workingThread = new Thread(this);
		this.addObserver(processManager);
	}
	
	public final void stop()
	{
		this.workingThread.interrupt();
		this.setChanged();
		this.notifyObservers(this.STATE_STOPPED);
	}
	
	public final void start()
	{	
		this.workingThread.start();	
	}
	
	/**
     * Add children process id
     */
	public final void addChildren()
	{
		
	}
	
	/**
     * Remove children process id
     */
	public final void removeChildren()
	{
		
	}
}