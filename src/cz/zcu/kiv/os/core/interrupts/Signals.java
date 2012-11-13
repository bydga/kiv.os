/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.core.interrupts;

/**
 *
 * @author bydga
 */
public enum Signals {
	/**
	 * CTRL+C
	 */
	SIGTERM, 
	
	/**
	 * CTRL+D
	 */
	SIGQUIT,
	
	/**
	 * CTRL+Z
	 */
	SIGPAUSE,
	
}
