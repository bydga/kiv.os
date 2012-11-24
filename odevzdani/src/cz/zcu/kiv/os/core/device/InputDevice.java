package cz.zcu.kiv.os.core.device;

import cz.zcu.kiv.os.Utilities;
import java.io.*;

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
			Utilities.log("Error while reading from the InputDevice.");
			return null;
		}
	}

	@Override
	protected void detachAction() {
		try {
			reader.close();
            
		} catch (IOException ex) {
			Utilities.log("Error while closing the InputDevice.");
		} finally {
			reader = null;
		}
	}
}
