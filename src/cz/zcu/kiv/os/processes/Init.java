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

/**
 *
 * @author bydga
 */
public class Init extends cz.zcu.kiv.os.core.Process {

	private AbstractIODevice createStdDevice() throws IOException {
		return Core.getInstance().getServices().createPipe();
	}

	@Override
	public void run(String[] args) throws Exception {
		Utilities.log("INIT running...");
		this.setInputStream(this.createStdDevice());
		this.setOutputStream(this.createStdDevice());
		this.setErrorStream(this.createStdDevice());
		SwingTerminal terminal = new SwingTerminal((IInputDevice) this.getOutputStream(), (IOutputDevice) this.getInputStream());
		Core.getInstance().setTerminal(terminal);

		while (true) {
			
			ProcessProperties props = new ProcessProperties(this, null, this.getInputStream(), this.getOutputStream(), this.getErrorStream(), this.getWorkingDir(), new ProcessGroup(new ThreadGroup("initgroup")));
			Process login = Core.getInstance().getServices().createProcess("Login", props);
			login.join();
		}
	}
}
