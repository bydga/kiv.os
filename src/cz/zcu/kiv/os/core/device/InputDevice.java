package cz.zcu.kiv.os.core.device;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jakub Danek
 */
public class InputDevice extends AbstractDevice implements IInputDevice {
    
    private BufferedReader reader;

    public InputDevice(InputStream inputStream) {
        InputStreamReader isr = new InputStreamReader(inputStream);
        reader = new BufferedReader(isr);
    }

    @Override
    public String readLine() {
        try {
            return reader.readLine();
        } catch (IOException ex) {
            Logger.getLogger(InputDevice.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    protected void closeAction() {
        try {
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(InputDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
