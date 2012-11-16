/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.Utilities;
import cz.zcu.kiv.os.core.Core;
import cz.zcu.kiv.os.core.Process;
import cz.zcu.kiv.os.core.ProcessGroup;
import cz.zcu.kiv.os.core.ProcessProperties;
import cz.zcu.kiv.os.core.device.*;
import cz.zcu.kiv.os.terminal.SwingTerminal;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author bydga
 */
public class Init extends cz.zcu.kiv.os.core.Process {

	private AbstractIODevice createStdDevice() throws IOException {
		return new PipeDevice(true);
	}

	@Override
	public void run(String[] args) throws Exception {
		Utilities.log("INIT running...");
		this.setInputStream(this.createStdDevice());
		this.setOutputStream(this.createStdDevice());
		this.setErrorStream(this.createStdDevice());
		final SwingTerminal terminal = new SwingTerminal((IInputDevice) this.getOutputStream(), (IOutputDevice) this.getInputStream());
		Core.getInstance().setTerminal(terminal);

		try {
			while (true) {
				this.checkForStop();
				ProcessProperties props = new ProcessProperties(this, this.getUser(), null, this.getInputStream(), this.getOutputStream(), this.getErrorStream(), this.getWorkingDir(), new ProcessGroup(new ThreadGroup("logingroup")));
				Process login = Core.getInstance().getServices().createProcess("Login", props);
				int code = Core.getInstance().getServices().readProcessExitCode(login);
			}

		} catch (InterruptedException ex) {
			this.getOutputStream().writeLine("Init joining all remaining processess");
			List<Process> processess = Core.getInstance().getServices().getAllProcessess();
			for (Process p : processess) {
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

			Utilities.log("Bye!");
			terminal.closeFrame();
			this.getOutputStream().detach();
			this.getInputStream().detach();
			this.getErrorStream().detach();
		}
	}
}
