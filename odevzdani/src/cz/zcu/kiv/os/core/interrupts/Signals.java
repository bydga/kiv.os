package cz.zcu.kiv.os.core.interrupts;

/**
 * Available signals, that the operating system supports.
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
	 * Unoverridable signal indicating the process to stop.
	 */
	SIGKILL,
	
}
