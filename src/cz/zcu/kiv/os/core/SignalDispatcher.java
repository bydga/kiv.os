/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.core;

import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author bydga
 */
public class SignalDispatcher extends Observable {

	public void dispatchSystemSignal(Signals sig) {
		this.call(sig);
	}

	public void dispatchKeyboardEvent(KeyboardEvent evt) {
		
		this.call(evt);
	}
	
	
	private void call(Object o)
	{
		this.setChanged();
		this.notifyObservers(o);
	}
}
