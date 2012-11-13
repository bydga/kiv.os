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
 *
 * @author bydga
 */
public class ProcessManager implements Observer {

	private final Map<Integer, ProcessTableRecord> processTable;
	
	private int lastPID;

	private final Object foregroundProcessLock = new Object();
	private Process foregroundProcess = null;

	public ProcessManager() {
		this.processTable = new HashMap<Integer, ProcessTableRecord>();
		this.lastPID = -1;
	}

	public Map<Integer, ProcessTableRecord> getProcessTable() {
		return this.processTable;
	}

	public Process getForegroundProcess() {
		synchronized(foregroundProcessLock) {
			return this.foregroundProcess;
		}
	}

	public Process createProcess(String processName, ProcessProperties properties) throws NoSuchProcessException {
		synchronized(this.processTable) {
			int pid;
			do {
				this.lastPID = (lastPID + 1 <= 0) ? 0 : lastPID + 1;
				pid = lastPID;
			} while (this.processTable.containsKey(this.lastPID));
		

			try {
				//class name begins with Capital letter
				String className = Character.toUpperCase(processName.charAt(0)) + processName.substring(1).toLowerCase();
				String fullClassName = Process.PROCESS_PACKAGE + "." + className;
				Class procClass = Class.forName(fullClassName);
				Constructor constructor = procClass.getConstructor();

				Process p = (Process) constructor.newInstance();
				p.init(pid, properties);

				ProcessTableRecord record = new ProcessTableRecord(p, properties.isBackgroundProcess);
				this.processTable.put(pid, record);
				addStreamsToProcessTable(p);

				if (properties.parent != null) { //because of init
					properties.parent.addChildren(p);
				}
				if (!properties.isBackgroundProcess) {
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

	public int readProcessExitCode(Process p) {
		try {
			synchronized(this.processTable) {
				if (!this.processTable.containsKey(p.getPid())) {
					throw new RuntimeException("Process with pid " + p.getPid() + " doesn't exist in the process table.");
				}
			}

			int res = p.getExitCode();
			if (p.getParent() != null) {
				p.getParent().removeChildren(p);
			}
			return res;
		} catch (InterruptedException ex) {
			Logger.getLogger(ProcessManager.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			this.cleanUpProcess(p);
		}

		return -1;
	}

	public void addStreamToProcess(int pid, IDevice stream) {
		synchronized(this.processTable) {
			this.processTable.get(pid).getOpenedStreams().add(stream);
		}
	}

	public void removeStreamFromProcess(int pid, IDevice stream) {
		synchronized(this.processTable) {
			this.processTable.get(pid).getOpenedStreams().remove(stream);
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		Utilities.log("process manager got update from " + o.getClass().getSimpleName());
		Process finished = (Process) o;
		// TODO: handle change of Observable object (stopped process etc)
		switchForegroundProcess(finished.getParent());
	}

	private void switchForegroundProcess(Process newFg) {
		synchronized(foregroundProcessLock) {
			this.foregroundProcess = newFg;
		}
		synchronized(newFg) {
			newFg.setForegroundProcess(true);
			newFg.notifyAll();
		}
	}

	private void cleanUpProcess(Process p) {
		synchronized(this.processTable) {
			closeStreams(p.getPid());
			this.processTable.remove(p.getPid());
		}

	}

	private void closeStreams(int pid) {
		List<IDevice> devs = processTable.get(pid).getOpenedStreams();
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
