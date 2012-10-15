package cz.zcu.kiv.os.core.device;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jakub Danek
 */
public class OutputDevice extends AbstractDevice implements IOutputDevice {

    private BufferedWriter writer;

    public OutputDevice(OutputStream outputStream) {
        OutputStreamWriter osw = new OutputStreamWriter(outputStream);
        writer = new BufferedWriter(osw, 1024);
    }

    @Override
    public void writeLine(String input) {
        try {
            writer.write(input);
            writer.flush();
        } catch (IOException ex) {
            Logger.getLogger(OutputDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void closeAction() {
        try {
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(OutputDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
