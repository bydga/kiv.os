/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.processes;

import com.sun.xml.internal.ws.util.StringUtils;
import cz.zcu.kiv.os.core.Core;
import cz.zcu.kiv.os.core.ProcessGroup;
import cz.zcu.kiv.os.core.ProcessProperties;
import cz.zcu.kiv.os.core.Process;
import cz.zcu.kiv.os.core.filesystem.FileManager;

/**
 * Process that handles login procedure. It is usually run by init. After successfull login creates new Shell (on foreground).
 * @author bydga
 */
public class Login extends Process {

	@Override
	protected void run(String[] args) throws Exception {

		this.getOutputStream().writeLine("Enter login: ");
		String login = this.getInputStream().readLine();
		login = FileManager.removeProhibitedChars(login);

		while (login.equals("")) {
			this.getOutputStream().writeLine("Invalid login name. Enter login:");
			login = this.getInputStream().readLine();
			login = FileManager.removeProhibitedChars(login);
		}

		this.getOutputStream().writeLine("Logging in as " + login + "...");

		String path = "/home/" + login + "/";
		if (!Core.getInstance().getServices().directoryExists(this, path)) {
			this.getOutputStream().writeLine("Creating home directory " + path);
			Core.getInstance().getServices().createDirectory(this, path);
		}
		ProcessProperties props = new ProcessProperties(this, login, null, this.getInputStream(), this.getOutputStream(), this.getErrorStream(), path, new ProcessGroup(new ThreadGroup("shellgroup")));
		cz.zcu.kiv.os.core.Process shell = Core.getInstance().getServices().createProcess("shell", props);
		Core.getInstance().getServices().readProcessExitCode(shell);

	}
}
