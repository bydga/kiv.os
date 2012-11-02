package cz.zcu.kiv.os.core.device;

import java.io.IOException;

/**
 * Basic implementation of IDevice interface. Provides basic means for determining
 * whether the device is open or not.
 *
 * @author Jakub Danek
 */
public abstract class AbstractDevice implements IDevice {

    private boolean open;

    protected AbstractDevice() {
        open = true;
    }

    /**
     *
     * @return true if the device is open
     */
    @Override
    public boolean isOpen() {
        return open;
    }

    /**
     * Closes the device.
     */
    @Override
    public synchronized void detach() throws IOException {
        if(open) {
            open = false;
            this.detachAction();
        }
    }

    /**
     * Override this action to provide any actions that need to be done upon
     * device close.
     *
     * If explicitly called by child class or another package class, must be synchronized
     * via the owning instance monitor!!!
     */
    protected abstract void detachAction() throws IOException;

}
