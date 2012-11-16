package cz.zcu.kiv.os;

import cz.zcu.kiv.os.core.Core;
import cz.zcu.kiv.os.core.Process;
import cz.zcu.kiv.os.core.ProcessGroup;
import cz.zcu.kiv.os.core.ProcessProperties;
import cz.zcu.kiv.os.core.device.InputDevice;
import cz.zcu.kiv.os.core.device.OutputDevice;

/**
 *
 * @author bydga
 */
public class Main {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("User Home Path: "
				+ System.getProperty("user.home"));

		Utilities.log("starting OS");

		ProcessProperties props = new ProcessProperties(null, "root", null, new InputDevice(System.in, false), new OutputDevice(System.out, false), new OutputDevice(System.err, false), "/", new ProcessGroup(new ThreadGroup("initgroup")));
		Process p = Core.getInstance().getServices().createProcess("Init", props);
		Utilities.log("after init created");

	}
}
