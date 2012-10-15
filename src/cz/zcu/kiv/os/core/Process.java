/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.core;

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
	protected String[] args;
	protected InputStream stdIn;
	protected OutputStream stdOut;
	protected OutputStream stdErr;

	public int getPid() {
		return this.pid;
	}

	public abstract void run() throws Exception;

	public abstract void initProcess(String[] args);

	public final void init(int pid, Process parent, String[] args, InputStream stdIn, OutputStream stdOut, OutputStream stdErr) {
		this.children = new ArrayList<Process>();
		this.pid = pid;
		this.parent = parent;
		this.stdIn = stdIn;
		this.stdOut = stdOut;
		this.stdErr = stdErr;
		this.initProcess(args);
		this.workingThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Process.this.run();
					Process.this.setChanged();
					Process.this.notifyObservers(Process.STATE_FINISH);
				} catch (Exception ex) {
					Process.this.setChanged();
					Process.this.notifyObservers(Process.STATE_EXCEPTION);
				}
			}
		});
	}

	public final void stop() {
		this.workingThread.interrupt();
		this.setChanged();
		this.notifyObservers(Process.STATE_STOP);
	}

	public final void start() {
		this.workingThread.start();
	}

	/**
	 * Add children process id
	 */
	public final void addChildren(Process p) {
		this.children.add(p);
	}

	/**
	 * Remove children process id
	 */
	public final void removeChildren(Process p) {
		this.children.remove(p);
		
	}
}