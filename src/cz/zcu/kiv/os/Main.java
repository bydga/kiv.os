package cz.zcu.kiv.os;

import cz.zcu.kiv.os.core.Core;
import cz.zcu.kiv.os.core.Process;
import cz.zcu.kiv.os.core.ProcessGroup;
import cz.zcu.kiv.os.core.ProcessProperties;

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
		Core core = Core.getInstance();

		ProcessProperties props = new ProcessProperties(null, "root", null, core.getStdIn(), core.getStdOut(), core.getStdErr(), "/", new ProcessGroup(new ThreadGroup("initgroup")), true);
		Process p = Core.getInstance().getServices().createProcess("Init", props);
		Utilities.log("after init created");
		Core.getInstance().getServices().readProcessExitCode(p);
		Utilities.log("Unloading remaining OS resources...");
		Core.getInstance().detachOSResources();
		Utilities.log("End of simulation. Bye!");

	}
}
