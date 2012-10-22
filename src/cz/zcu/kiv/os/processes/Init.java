/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.Utilities;
import cz.zcu.kiv.os.core.Core;
import cz.zcu.kiv.os.core.Process;
import cz.zcu.kiv.os.core.device.*;
import cz.zcu.kiv.os.terminal.SwingTerminal;
import java.io.IOException;

/**
 *
 * @author bydga
 */
public class Init extends cz.zcu.kiv.os.core.Process {

	private AbstractIODevice createStdDevice() throws IOException {
		return new IOQueueDevice();
	}

	@Override
	public void run(String[] args) throws Exception {
		Utilities.log("INIT running...");
		this.stdIn = this.createStdDevice();
		this.stdOut = this.createStdDevice();
		this.stdErr = this.createStdDevice();
		SwingTerminal terminal = new SwingTerminal((IInputDevice) stdOut, (IOutputDevice) stdIn);
		Process shell = Core.getInstance().getServices().createProcess(this, "shell", null, this.stdIn, this.stdOut, this.stdErr);
		Utilities.log("Terminal and shell started");
		while (true) {
			
		}
	}
}
