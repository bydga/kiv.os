/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.core.interrupts;


import cz.zcu.kiv.os.core.Process;

/**
 *
 * @author bydga
 */
public class Interrupt {

	protected Process receiver;
	
	protected  Enum data;

	public Process getReceiver() {
		return this.receiver;
	}

	public Enum getInterrupt()
	{
		return this.data;
	}

	public Interrupt(Process receiver, Enum data) {
		this.receiver = receiver;
		this.data = data;
	}
	
	@Override
	public String toString()
	{
		return "Interrupt " + this.getInterrupt().toString() + " for " + this.receiver.getClass().getSimpleName();
	}
}
