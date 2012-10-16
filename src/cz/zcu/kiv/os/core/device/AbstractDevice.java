package cz.zcu.kiv.os.core.device;

/**
 * Basic implementation of IDevice interface. Provides basic means for determining
 * whether the device is open or not.
 *
 * @author Jakub Danek
 */
public abstract class AbstractDevice implements IDevice {

    private volatile boolean open;

    protected AbstractDevice() {
        open = true;
    }

    /**
     *
     * @return true if the device is open
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * Closes the device.
     */
    public synchronized void close() {
        if(open) {
            open = false;
            this.closeAction();
        }
    }

    /**
     * Override this action to provide any actions that need to be done upon
     * device close.
     */
    protected abstract void closeAction();

}
