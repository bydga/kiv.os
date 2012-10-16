package cz.zcu.kiv.os.core.device;

/**
 * Device abstraction.
 *
 * @author Jakub Danek
 */
public interface IDevice {
    /**
     * Shuts the device down.
     */
    public void close();
    /**
     * Checks whether the device is open and can read from/written to.
     * @return
     */
    public boolean isOpen();
}
