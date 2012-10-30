package cz.zcu.kiv.os.core.device;

import java.io.Closeable;

/**
 * Device abstraction.
 *
 * @author Jakub Danek
 */
public interface IDevice extends Closeable{
    /**
     * Checks whether the device is open and can read from/written to.
     * @return
     */
    public boolean isOpen();
}
