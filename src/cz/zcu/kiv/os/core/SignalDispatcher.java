/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.core;

import cz.zcu.kiv.os.Utilities;
import cz.zcu.kiv.os.core.interrupts.Interrupt;
import cz.zcu.kiv.os.core.interrupts.KeyboardEvent;
import cz.zcu.kiv.os.core.interrupts.Signals;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author bydga
 */
public class SignalDispatcher extends Observable {

	public void dispatchSystemSignal(Process receiver,Signals sig) {
		this.call(new Interrupt(receiver, sig));
	}

	public void dispatchKeyboardEvent(Process receiver, KeyboardEvent evt) {
		this.call(new Interrupt(receiver, evt));
	}
	
	
	private void call(Interrupt interrupt)
	{
		this.setChanged();
		this.notifyObservers(interrupt);
	}
}
