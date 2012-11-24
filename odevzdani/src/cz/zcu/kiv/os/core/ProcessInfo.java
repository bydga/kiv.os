/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.core;

/**
 *
 * @author bydga
 */
public class ProcessInfo {
	
	public String processName;
	public int pid;
	
	public ProcessInfo(ProcessTableRecord record)
	{
		this.processName = record.getProcess().getClass().getSimpleName();
		this.pid = record.getProcess().getPid();
	}
}
