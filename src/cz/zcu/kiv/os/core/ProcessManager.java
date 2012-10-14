/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.core;

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
public class ProcessManager implements Observer{

	protected static final String PROCESS_PACKAGE = "cz.zcu.kiv.os.processes";
	protected Map<Integer, ProcessTableRecord> processTable;
	int counter;

	public ProcessManager() {
		this.processTable = new HashMap<Integer, ProcessTableRecord>();
		this.counter = -1;
	}

	public synchronized Process createProcess(Process parent, String processName, String[] args, InputStream stdIn, OutputStream stdOut, OutputStream stdErr) throws Exception {

		do {
			this.counter = (counter + 1 <= 0) ? 0 : counter + 1;
		} while (this.processTable.containsKey(this.counter));

		//class name begins with Capital letter
		String className = Character.toUpperCase(processName.charAt(0)) + processName.substring(1).toLowerCase();
		String fullClassName = ProcessManager.PROCESS_PACKAGE + "." + className;
		Class procClass = Class.forName(fullClassName);
		Constructor constructor = procClass.getConstructor(int.class, int.class, InputStream.class, OutputStream.class, OutputStream.class, ProcessManager.class);
		
		Process p = (Process) constructor.newInstance(this.counter, parent.pid, stdIn, stdOut, stdErr, this);
		ProcessTableRecord record = new ProcessTableRecord(p);
		record.setIsRunning(true);
		this.processTable.put(this.counter, record);
		p.start();

		return p;
	}

	public synchronized void killProcess(int pid) throws Exception {
		ProcessTableRecord p = this.processTable.remove(pid);
		p.getProcess().stop();
		for (Closeable stream : p.openedStreams) {
			stream.close();
		}
	}

	public synchronized void addStreamToProcess(int pid, Closeable stream) {
		this.processTable.get(pid).getOpenedStreams().add(stream);
	}

	public synchronized void removeStreamFromProcess(int pid, Closeable stream) {
		this.processTable.get(pid).getOpenedStreams().remove(stream);
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO: handle change of Observable object (stopped process etc)
	}
}
