package cz.zcu.kiv.os;

import cz.zcu.kiv.os.core.Core;
import cz.zcu.kiv.os.core.Process;
import cz.zcu.kiv.os.core.device.InputDevice;
import cz.zcu.kiv.os.core.device.OutputDevice;
import java.io.IOError;
import javax.management.RuntimeErrorException;

/**
 *
 * @author bydga
 */
public class Main {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws Exception {

		Utilities.log("starting OS");
		Process p = Core.getInstance().getServices().createProcess(null, "Init", null, new InputDevice(System.in), new OutputDevice(System.out), new OutputDevice(System.err));

	}
}
