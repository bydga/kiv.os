/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.core;

import cz.zcu.kiv.os.core.device.IInputDevice;
import cz.zcu.kiv.os.core.device.IOutputDevice;

/**
 *
 * @author bydga
 */
public class ProcessProperties {

	public Process parent;
	public String[] args;
	public IInputDevice inputStream;
	public IOutputDevice outputStream;
	public IOutputDevice errorStream;
	public String workingDir;
	public ThreadGroup processGroup;
	public boolean isBackgroundProcess;

	public ProcessProperties(Process parent, String[] args, IInputDevice inputStream, IOutputDevice outputStream, IOutputDevice errorStream, String workingDir, ThreadGroup processGroup) {
		this(parent, args, inputStream, outputStream, errorStream, workingDir, processGroup, false);	
	}
	public ProcessProperties(Process parent, String[] args, IInputDevice inputStream, IOutputDevice outputStream, IOutputDevice errorStream, String workingDir, ThreadGroup processGroup, boolean  isBackgroundProcess) {
		this.parent = parent;
		this.args = args;
		this.inputStream = inputStream;
		this.outputStream = outputStream;
		this.errorStream = errorStream;
		this.workingDir = workingDir;
		this.processGroup = processGroup;
		this.isBackgroundProcess = isBackgroundProcess;
	}
}
