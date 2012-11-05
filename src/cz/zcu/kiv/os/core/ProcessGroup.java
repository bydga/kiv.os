/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.core;

/**
 *
 * @author bydga
 */
public class ProcessGroup {

	private boolean shouldStop = false;

	public boolean getShouldStop() {
		return this.shouldStop;
	}
	
	private ThreadGroup group;

	public void stop() {
		this.shouldStop = true;
		this.group.interrupt();
	}

	public ThreadGroup getGroup() {
		return group;
	}

	public ProcessGroup(ThreadGroup group) {
		this.group = group;
	}
}
