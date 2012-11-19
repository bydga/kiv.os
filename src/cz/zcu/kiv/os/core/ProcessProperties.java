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
	private String workingDir;
	public ProcessGroup processGroup;
	private boolean backgroundProcess;
	public String user;

	public ProcessProperties(Process parent, String user, String[] args, IInputDevice inputStream, IOutputDevice outputStream, IOutputDevice errorStream, String workingDir, ProcessGroup processGroup) {
		this(parent, user, args, inputStream, outputStream, errorStream, workingDir, processGroup, false);	
	}
	public ProcessProperties(Process parent, String user, String[] args, IInputDevice inputStream, IOutputDevice outputStream, IOutputDevice errorStream, String workingDir, ProcessGroup processGroup, boolean  isBackgroundProcess) {
		this.parent = parent;
		this.user = user;
		this.args = args;
		this.inputStream = inputStream;
		this.outputStream = outputStream;
		this.errorStream = errorStream;
		this.workingDir = workingDir;
		this.processGroup = processGroup;
		this.backgroundProcess = isBackgroundProcess;
	}

	public synchronized boolean isBackgroundProcess() {
		return this.backgroundProcess;
	}

	public synchronized String getWorkingDir() {
		return this.workingDir;
	}

	public synchronized void setBackgroundProcess(boolean backgroundProcess) {
		this.backgroundProcess = backgroundProcess;
	}

	public synchronized void setWorkingDir(String workingDir) {
		this.workingDir = workingDir;
	}
}
