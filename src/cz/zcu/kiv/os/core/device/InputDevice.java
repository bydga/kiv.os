package cz.zcu.kiv.os.core.device;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Input device implementation. Can read from any InputStream provided to it.
 *
 * @author Jakub Danek
 */
public class InputDevice extends AbstractDevice implements IInputDevice {

	private BufferedReader reader;

	/**
	 * Basic constructor
	 * @param inputStream open input stream that can be read from
	 */
	public InputDevice(InputStream inputStream, boolean stdStream) {
                super(stdStream);
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
	protected void detachAction() {
		try {
			reader.close();
                        reader = null;
		} catch (IOException ex) {
			Logger.getLogger(InputDevice.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
