package cz.zcu.kiv.os.core.device;

/**
 * Output device abstraction - e.g. stdout or file opened for writing.
 *
 * @author Jakub Danek
 */
public interface IOutputDevice extends IDevice {

    /**
     * Writes one line to the output stream.
     * @param input
     */
    //TODO better exception
    public void writeLine(String input) throws Exception;

}
