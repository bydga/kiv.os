/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.core;

import cz.zcu.kiv.os.processes.Sort;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.Map;

/**
 *
 * @author bydga
 */
public class ProcessManager {

	protected static final String PROCESS_PACKAGE = "cz.zcu.kiv.os.processes";
	protected Map<Integer, ProcessTableRecord> processTable;
	int counter;

	public ProcessManager() {
		this.processTable = new Hashtable<Integer, ProcessTableRecord>();
		this.counter = -1;
	}

	public synchronized Process createProcess(String processName, InputStream stdIn, OutputStream stdOut, OutputStream stdErr, Process parent) throws Exception {

		do {
			this.counter = (counter + 1 <= 0) ? 0 : counter + 1;
		} while (this.processTable.containsKey(this.counter));

		String className = Character.toUpperCase(processName.charAt(0)) + processName.substring(1).toLowerCase();
		String fullClassName = ProcessManager.PROCESS_PACKAGE + "." + className;
		Class procClass = Class.forName(fullClassName);
		Constructor constructor = procClass.getConstructor(int.class, InputStream.class, OutputStream.class, OutputStream.class);
		
		Process p = (Process) constructor.newInstance(this.counter, System.in, System.out, System.out);
		ProcessTableRecord record = new ProcessTableRecord(p);
		record.setIsRunning(true);
		this.processTable.put(this.counter, record);
		p.start();

		return p;
	}

	public synchronized void killProcess(int pid) throws Exception {
		ProcessTableRecord p = this.processTable.get(pid);
		p.getProcess().stop();
		
		

	}

	public synchronized void killProcess(Process p) {
	}
}
