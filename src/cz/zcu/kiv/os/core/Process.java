/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.core;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bydga
 */
public abstract class Process {
	
	protected Thread workingThread;

	public Process parent;
	public int pid;
	public List<Process> children;
	protected abstract void run() throws Exception;
	
	
	public int getPid()
	{
		return this.pid;
	}
	//TODO: supply all params, save them as instant variables
	public Process(int pid, Process parent, String[] args, InputStream stdIn, OutputStream stdOut, OutputStream stdErr) {
		this.pid = pid;
		this.parent = parent;
		this.children = new ArrayList<Process>();
	}
	
	public List<Process> getChildren()
	{
		return this.children;
	}
	
	public final void stop()
	{
		//sth like thread stop..
	}
	
	public final void start() {
		//wrapper above working thread execution
		try {
			this.run();
		} catch (Exception e)  {
			
		}
	}
}
