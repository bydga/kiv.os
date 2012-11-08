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
	private ThreadGroup group;

	public ProcessGroup(ThreadGroup group) {
		this.group = group;
	}
	
	public boolean getShouldStop() {
		return this.shouldStop;
	}
	
	public ThreadGroup getGroup() {
		return group;
	}

	public void stop() {
		this.shouldStop = true;
		this.group.interrupt();
	}
}
