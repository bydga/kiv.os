/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.core;

import cz.zcu.kiv.os.core.interrupts.Interrupt;
import cz.zcu.kiv.os.core.interrupts.KeyboardEvent;
import cz.zcu.kiv.os.core.interrupts.Signals;
import java.util.Observable;

/**
 * Forwards interupts to processes.
 * @author bydga
 */
public class SignalDispatcher extends Observable {

	/**
	 * Dispatches specified signal to a process identified by the first parameter.
	 * @param receiver Process that should receive the signal.
	 * @param sig Type of signal to dispatch.
	 */
	public synchronized void dispatchSystemSignal(Process receiver,Signals sig) {
		this.call(new Interrupt(receiver, sig));
	}

	/**
	 * Dispatches specified keyboard event to a process identified by the first parameter.
	 * @param receiver Process that should receive the event.
	 * @param evt Type of event to dispatch.
	 */
	public synchronized void dispatchKeyboardEvent(Process receiver, KeyboardEvent evt) {
		this.call(new Interrupt(receiver, evt));
	}
	
	
	private void call(Interrupt interrupt) {
		this.setChanged();
		this.notifyObservers(interrupt);
	}
}
