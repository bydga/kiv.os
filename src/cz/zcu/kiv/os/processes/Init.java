package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.Utilities;
import cz.zcu.kiv.os.core.Core;
import cz.zcu.kiv.os.core.Process;
import cz.zcu.kiv.os.core.ProcessGroup;
import cz.zcu.kiv.os.core.ProcessProperties;
import java.util.List;

/**
 *
 * @author bydga
 */
public class Init extends cz.zcu.kiv.os.core.Process {

	@Override
	public void run(String[] args) throws Exception {

		List<Process> processess = Core.getInstance().getServices().getAllProcessess();
		int cnt = 0;
		for (Process p : processess) {
			if (p.getClass() == Init.class) {
				cnt++;
			}
		}

		if (cnt == 2) { //first (real) init running and user is trying to run second  Init
			this.getOutputStream().writeLine("Init already running...");
			return;
		}

		Utilities.log("INIT running...");

		Core.getInstance().openTerminalWindow(this);

		try {
			while (Core.getInstance().getServices().isRunning()) {
				this.checkForStop();
				ProcessProperties props = new ProcessProperties(this, this.getUser(), null, this.getInputStream(), this.getOutputStream(), this.getErrorStream(), this.getWorkingDir(), new ProcessGroup(new ThreadGroup("logingroup")), true);
				Process login = Core.getInstance().getServices().createProcess("Login", props);
				int code = Core.getInstance().getServices().readProcessExitCode(login);
			}

		} catch (InterruptedException ex) {
			//cleanup only
		} finally {
			cleanup();
		}
	}

	private void cleanup() throws Exception {
		List<Process> processess = Core.getInstance().getServices().getAllProcessess();
		boolean first = true;
		for (Process p : processess) {
			if (first) {
				first = false;
				this.getOutputStream().writeLine("Init joining all remaining processess");
			}
			if (p == this) {
				continue;
			}

			this.getOutputStream().writeLine("Joining for " + p.getClass().getSimpleName() + ", pid " + p.getPid());
			try {
				int i = Core.getInstance().getServices().readProcessExitCode(p);
			} catch (InterruptedException e) {
				continue;
			}
			this.getOutputStream().writeLine("Joined.");

		}
		this.getOutputStream().writeLine("All processes killed. Shutting down...");
		Thread.sleep(5000); //give user some time to read shutdown log from the terminal before init ends and devices get closed
	}
}
