package cz.zcu.kiv.os.core.device;

import java.io.Closeable;
import java.io.IOException;

/**
 * Device abstraction.
 *
 * @author Jakub Danek
 */
public interface IDevice {

	/**
	 *
	 * @return true if device is considered to be standard system stream.
	 */
    public boolean isStdStream();

	/**
	 *
	 * @return true if device can be used
	 */
    public boolean isOpen();

	/**
	 * Closes all streams owned by this device and detaches all used resources
	 * from the system.
	 * @throws IOException
	 */
    public void detach() throws IOException;
}
