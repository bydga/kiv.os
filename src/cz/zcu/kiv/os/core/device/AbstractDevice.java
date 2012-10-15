package cz.zcu.kiv.os.core.device;

/**
 *
 * @author veveri
 */
public abstract class AbstractDevice implements IDevice {

    private volatile boolean open;

    protected AbstractDevice() {
        open = true;
    }

    public boolean isOpen() {
        return open;
    }

    public synchronized void close() {
        if(open) {
            open = false;
            this.closeAction();
        }
    }

    protected abstract void closeAction();

}
