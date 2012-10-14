/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.core;

import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author bydga
 */
public abstract class Process {
	
	protected Thread workingThread;

	protected abstract void run() throws Exception;
	
	
	public int getPid()
	{
		return 1;
	}
	//TODO: supply all params, save them as instant variables
	public Process(int pid, InputStream stdIn, OutputStream stdOut, OutputStream stdErr) {
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
