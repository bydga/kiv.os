package cz.zcu.kiv.os.core.device;

/**
 * Input device abstraction. Works for reading from stdin or e.g. from an
 * open file.
 *
 * @author Jakub Danek
 */
public interface IInputDevice extends IDevice {

    /**
     * Read one line from the input stream if there is any. Blocks otherwise.
     * @return
     */
    //TODO better exception
    public String readLine() throws Exception;

}
