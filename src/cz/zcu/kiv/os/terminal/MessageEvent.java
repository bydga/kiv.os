/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.terminal;

import java.awt.AWTEvent;

/**
 *
 * @author veveri
 */
public class MessageEvent extends AWTEvent {

	public static final int EVENT_ID = AWTEvent.RESERVED_ID_MAX + 1;

	private String message;

	public MessageEvent(Object source, String message) {
		super(source, EVENT_ID);
		this.message = message;

	}

	public String getMessage() {
		return message;
	}
	

}
