package cz.zcu.kiv.os.core.device;

import java.io.Closeable;
import java.io.IOException;

/**
 * Device abstraction.
 *
 * @author Jakub Danek
 */
public interface IDevice {

    public boolean isStdStream();

    public boolean isOpen();

    public void detach() throws IOException;
}
