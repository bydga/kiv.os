package cz.zcu.kiv.os.core;

import cz.zcu.kiv.os.Utilities;
import cz.zcu.kiv.os.core.device.IDevice;
import cz.zcu.kiv.os.core.device.PipeDevice;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for the OS Core. Encapsulates process table, processes in them and opened streams.
 * @author bydga
 */
public class ProcessManager implements Observer {

	private final Map<Integer, ProcessTableRecord> processTable;
	private int lastPID;
	private final Object foregroundProcessLock = new Object();
	private Process foregroundProcess = null;

	/**
	 * Creates new instance of ProcessManager class.
	 */
	public ProcessManager() {
		this.processTable = new HashMap<Integer, ProcessTableRecord>();
		this.lastPID = -1;
	}

	/**
	 * Returns processTable map containing info about all running processes.
	 * @return 
	 */
	public Map<Integer, ProcessTableRecord> getProcessTable() {
		return this.processTable;
	}

	/**
	 * Returns current FG process.
	 * @return Process that is currently operating in foreground.
	 */
	public Process getForegroundProcess() {
		synchronized (foregroundProcessLock) {
			return this.foregroundProcess;
		}
	}

	public Process createProcess(String processName, ProcessProperties properties) throws NoSuchProcessException {
		int pid;
		synchronized (this.processTable) {
			do {
				this.lastPID = (lastPID + 1 <= 0) ? 0 : lastPID + 1;
				pid = lastPID;
			} while (this.processTable.containsKey(this.lastPID));
		}


		try {
			//class name begins with Capital letter
			String className = Character.toUpperCase(processName.charAt(0)) + processName.substring(1).toLowerCase();
			String fullClassName = Process.PROCESS_PACKAGE + "." + className;
			Class procClass = Class.forName(fullClassName);
			Constructor constructor = procClass.getConstructor();

			Process p = (Process) constructor.newInstance();
			p.init(pid, properties);

			ProcessTableRecord record = new ProcessTableRecord(p);
			synchronized(this.processTable) {
				this.processTable.put(pid, record);
			}
			addStreamsToProcessTable(p);

			if (properties.parent != null) { //because of init
				properties.parent.addChildren(p);
			}
			if (!properties.isBackgroundProcess()) {
				this.foregroundProcess = p;
			}
			Utilities.log("FG: " + p.getClass().getName());

			p.addObserver(this);
			p.start();

			return p;
		} catch (Exception ex) {
			throw new NoSuchProcessException("Process " + processName + " failed to create: " + ex.getClass().getName() + ": " + ex.getMessage());

		}
	}

	/**
	 * Puts references of streams owned by the process (IO) into the process table.
	 *
	 * @param p
	 */
	private void addStreamsToProcessTable(Process p) {
		IDevice dev = p.properties.outputStream;
		//std stream and inputpipeend are not owned by the process
		if (!dev.isStdStream() && !(dev instanceof PipeDevice)) {
			this.addStreamToProcess(p.getPid(), dev);
		}

		//stdStreams arent owned by the process
		dev = p.properties.inputStream;
		if (!dev.isStdStream()) {
			this.addStreamToProcess(p.getPid(), dev);
		}

		//stdStreams arent owned by the process
		dev = p.properties.errorStream;
		if (!dev.isStdStream()) {
			this.addStreamToProcess(p.getPid(), dev);
		}
	}

	/**
	 * Blocking call to the process. Waits for its exit code, cleanup after the process terminates and returns its exit value.
	 * @param p Process to wait for.
	 * @return Exit code of the finished process.
	 * @throws InterruptedException Is thrown when the caller (reader of the exit code) is interrupted in the waiting.
	 */
	public int readProcessExitCode(Process p) throws InterruptedException {
		try {
			int res = p.getExitCode();
			if (p.getParent() != null) {
				p.getParent().removeChildren(p);
			}
			return res;
		} catch (InterruptedException ex) {
			throw ex;
		} finally {
			synchronized (this.processTable) {
				if (this.processTable.containsKey(p.getPid())) {
					this.cleanUpProcess(p);
				}
			}
		}
	}

	/**
	 * Adds new Device to the process specified by the pid.
	 * @param pid Pid of process to attach stream to.
	 * @param stream Stream to be attached.
	 */
	public void addStreamToProcess(int pid, IDevice stream) {
		ProcessTableRecord rec;
		synchronized (this.processTable) {
			rec = this.processTable.get(pid);
		}
		rec.addOpenStream(stream);
	}

	/**
	 * Removes existing Device from the process specified by the pid.
	 * @param pid Pid of process to remove stream from.
	 * @param stream Stream to be removed.
	 */
	public void removeStreamFromProcess(int pid, IDevice stream) {
		ProcessTableRecord rec;
		synchronized (this.processTable) {
			rec = this.processTable.get(pid);
		}
		rec.removeStream(stream);
	}

	/**
	 * Implementation of the observable pattern.
	 * This method is called when a process finishes and the foreground must be given back to its parent.
	 * @param o Finished process.
	 * @param arg null
	 */
	@Override
	public void update(Observable o, Object arg) {
		Process finished = (Process) o;
		switchForegroundProcess(finished.getParent());
	}

	/**
	 * Switches the foreground process.
	 * @param newFg new process that will operate on foreground.
	 */
	private void switchForegroundProcess(Process newFg) {
		synchronized (foregroundProcessLock) {
			this.foregroundProcess = newFg;
		}

		if (newFg != null) {
			newFg.setForegroundProcess(true);
			//wakeup processes waiting for newFg to get foreground
			newFg.notifyIsForeground();
		}
	}

	/**
	 * Does cleanup after process ends. Removes it from the processtable and closes it's unclosed streams.
	 * @param p Process to cleanup after.
	 */
	private void cleanUpProcess(Process p) {
		closeStreams(p.getPid());
		synchronized (this.processTable) {
			this.processTable.remove(p.getPid());
		}

	}

	/**
	 * Closes all attached streams to a finished process.
	 * @param pid 
	 */
	private void closeStreams(int pid) {
		ProcessTableRecord rec;
		synchronized(this.processTable) {
			rec = processTable.get(pid);
		}
		List<IDevice> devs = rec.getOpenedStreams();
		for (IDevice device : devs) {
			try {
				device.detach();

			} catch (IOException ex) {
				Logger.getLogger(ProcessManager.class
						.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
}
