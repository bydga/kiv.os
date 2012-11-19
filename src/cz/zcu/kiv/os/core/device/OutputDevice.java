package cz.zcu.kiv.os.core.device;

import cz.zcu.kiv.os.Utilities;
import java.io.*;

/**
 * Output device implementation. Can write into any open output stream provided
 * to it.
 *
 * @author Jakub Danek
 */
public class OutputDevice extends AbstractDevice implements IOutputDevice {

    private BufferedWriter writer;

    /**
     * Default constructor
     * @param outputStream open output stream that can be written to.
     */
    public OutputDevice(OutputStream outputStream, boolean stdStream) {
        super(stdStream);
        OutputStreamWriter osw = new OutputStreamWriter(outputStream);
        writer = new BufferedWriter(osw, 1024);
    }

    @Override
    public void writeLine(String input) {
        try {
            writer.write(input);
            writer.newLine();
            writer.flush();
        } catch (IOException ex) {
            Utilities.log("Error while writing to the OutputDevice.");
        }
    }

    @Override
    protected void detachAction() {
        try {
            writer.close();
        } catch (IOException ex) {
            Utilities.log("Error while closing OutputDevice.");
        } finally {
			writer = null;
		}
    }

    @Override
    public void EOF() {
        try {
            writer.close();
        } catch (IOException ex) {
            Utilities.log("Error while closing OutputDevice.");
        }
    }

	@Override
	public void write(String input) {
		try {
            writer.write(input);
            writer.flush();
        } catch (IOException ex) {
            Utilities.log("Error while writing to the OutputDevice.");
        }
	}
}
