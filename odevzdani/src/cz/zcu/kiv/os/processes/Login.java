/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.core.Core;
import cz.zcu.kiv.os.core.ProcessGroup;
import cz.zcu.kiv.os.core.ProcessProperties;
import cz.zcu.kiv.os.core.Process;
import cz.zcu.kiv.os.core.filesystem.FileManager;
import cz.zcu.kiv.os.core.filesystem.InvalidPathCharactersException;

/**
 * Process that handles login procedure. It is usually run by init. After successfull login creates new Shell (on foreground).
 * @author bydga
 */
public class Login extends Process {

	private static final String helpText =
		"\nUsage: login\n" +
		"Login as another user.\n";
	
	/**
	 * Returns manual page for Pwd process
	 * 
	 * @return string with manual page
	 */
	public static String getManualPage() {
		return helpText; 
	}
	
	@Override
	protected void run(String[] args) throws Exception {
		
		this.getOutputStream().writeLine("Enter login: ");
		String login = this.getInputStream().readLine();
		try {
			FileManager.checkProhibitedChars(login);
		} catch(InvalidPathCharactersException ex) {
			login = "";
			this.getOutputStream().writeLine("Following characters cannot be used as filename: " + InvalidPathCharactersException.invalidCharsList());
		}

		while (login.equals("")) {
			this.getOutputStream().writeLine("Invalid login name. Enter login:");
			login = this.getInputStream().readLine();
			try {
				FileManager.checkProhibitedChars(login);
			} catch (InvalidPathCharactersException ex) {
				login = "";
				this.getOutputStream().writeLine("Following characters cannot be used as filename: " + InvalidPathCharactersException.invalidCharsList());
			}
		}

		this.getOutputStream().writeLine("Logging in as " + login + "...");

		String path = "/home/" + login + "/";
		if (!Core.getInstance().getServices().directoryExists(this, path)) {
			this.getOutputStream().writeLine("Creating home directory " + path);
			Core.getInstance().getServices().createDirectory(this, path);
		}
		ProcessProperties props = new ProcessProperties(this, login, null, this.getInputStream(), this.getOutputStream(), this.getErrorStream(), path, new ProcessGroup(new ThreadGroup("shellgroup")), true);
		cz.zcu.kiv.os.core.Process shell = Core.getInstance().getServices().createProcess("shell", props);
		Core.getInstance().getServices().readProcessExitCode(shell);

	}
}
