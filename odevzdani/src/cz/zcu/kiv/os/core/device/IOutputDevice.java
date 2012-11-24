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
    public void writeLine(String input) throws Exception;

	/**
	 * Writes string to the output stream.
	 * @param input
	 * @throws Exception
	 */
	public void write(String input) throws Exception;

	/**
	 * Closes the stream (for writing only, where possible).
	 */
    public void EOF();
}
