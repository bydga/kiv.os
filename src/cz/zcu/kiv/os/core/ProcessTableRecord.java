/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.core;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bydga
 */
public class ProcessTableRecord {

	protected Process process;
	protected boolean isRunning;
	protected List<Closeable> openedStreams;

	public List<Closeable> getOpenedStreams() {
		return this.openedStreams;
	}

	public boolean isRunning() {
		return this.isRunning;
	}

	public void setIsRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public Process getProcess() {
		return this.process;
	}

	public ProcessTableRecord(Process p) {
		this.process = p;
		this.openedStreams = new ArrayList<Closeable>();
	}
}
