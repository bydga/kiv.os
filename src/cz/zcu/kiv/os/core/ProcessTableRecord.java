/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.core;

/**
 *
 * @author bydga
 */
public class ProcessTableRecord {
	
	protected Process process;
	protected boolean isRunning;

	public boolean isRunning() {
		return isRunning;
	}

	public void setIsRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public Process getProcess() {
		return process;
	}
	
	public ProcessTableRecord(Process p)
	{
		this.process = p;
	}
	
	
}
