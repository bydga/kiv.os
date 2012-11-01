/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.core.interrupts;

/**
 *
 * @author bydga
 */
public class Interrupt {

	protected Process receiver;
	
	protected  Object data;

	public Process getReceiver() {
		return this.receiver;
	}

	public Object getInterrupt()
	{
		return this.data;
	}

	public Interrupt(Process receiver, Object data) {
		this.receiver = receiver;
		this.data = data;
	}
}
