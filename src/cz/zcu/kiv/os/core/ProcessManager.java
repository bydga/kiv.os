/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.core;

import cz.zcu.kiv.os.core.device.IDevice;
import cz.zcu.kiv.os.core.device.IInputDevice;
import cz.zcu.kiv.os.core.device.IOutputDevice;
import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author bydga
 */
public class ProcessManager implements Observer {

	private static final String PROCESS_PACKAGE = "cz.zcu.kiv.os.processes";
	private Map<Integer, ProcessTableRecord> processTable;
	private int counter;
	private  Process foregroundProcess = null;

	public ProcessManager() {
		this.processTable = new HashMap<Integer, ProcessTableRecord>();
		this.counter = -1;
	}
	
	public Process getForegroundProcess()
	{
		return this.foregroundProcess;
	}

	public synchronized Process createProcess(String processName, ProcessProperties properties) throws NoSuchProcessException {

		do {
			this.counter = (counter + 1 <= 0) ? 0 : counter + 1;
		} while (this.processTable.containsKey(this.counter));

		try {
			//class name begins with Capital letter
			String className = Character.toUpperCase(processName.charAt(0)) + processName.substring(1).toLowerCase();
			String fullClassName = ProcessManager.PROCESS_PACKAGE + "." + className;
			Class procClass = Class.forName(fullClassName);
			Constructor constructor = procClass.getConstructor();

			Process p = (Process) constructor.newInstance();
			p.init(this.counter, properties);
			ProcessTableRecord record = new ProcessTableRecord(p, properties.isBackgroundProcess);
			this.processTable.put(this.counter, record);
			p.addObserver(this);
			p.start();

			if (properties.parent != null) { //because of init
				properties.parent.addChildren(p);
			}
			if (!properties.isBackgroundProcess) {
				this.foregroundProcess = p;
			}
			Utilities.log("FG: " + p.getClass().getName());
			return p;
		} catch (Exception ex) {
			throw new NoSuchProcessException("Process " + processName + " failed to create: " + ex.getClass().getName() + ": " + ex.getMessage());

		}
	}

	public synchronized void killProcess(int pid) throws Exception {
		ProcessTableRecord p = this.processTable.remove(pid);
		p.getProcess().stop();
		for (IDevice stream : p.getOpenedStreams()) {
                    stream.detach();
		}
	}

	public synchronized void addStreamToProcess(int pid, IDevice stream) {
		this.processTable.get(pid).getOpenedStreams().add(stream);
	}

	public synchronized void removeStreamFromProcess(int pid, IDevice stream) {
		this.processTable.get(pid).getOpenedStreams().remove(stream);
	}

	@Override
	public void update(Observable o, Object arg) {
		Utilities.log("process manager got from " + o + ": " + arg);
		Process finished = (Process) o;
		// TODO: handle change of Observable object (stopped process etc)
		this.foregroundProcess = finished.getParent();
	}
}
