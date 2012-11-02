package cz.zcu.kiv.os.core.device;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    public OutputDevice(OutputStream outputStream) {
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
            //TODO handle exception
            Logger.getLogger(OutputDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void detachAction() {
        try {
            writer.close();
            writer = null;
        } catch (IOException ex) {
            //TODO handle exception
            Logger.getLogger(OutputDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void EOF() {
        try {
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(OutputDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
