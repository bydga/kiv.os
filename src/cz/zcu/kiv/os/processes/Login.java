/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.core.Core;

/**
 *
 * @author bydga
 */
public class Login extends cz.zcu.kiv.os.core.Process {

	@Override
	protected void run(String[] args) throws Exception {

		this.stdOut.writeLine("Enter login: ");
		String login = this.stdIn.readLine();

		while(login.equals(""))
		{
			this.stdOut.writeLine("Invalid login name. Enter login:");
			login = this.stdIn.readLine();
		}
		
		this.stdOut.writeLine("Logging in as " + login + "...");

		String path = "/home/" + login;
		if (!Core.getInstance().getServices().directoryExists(path)) {
			this.stdOut.writeLine("Creating home directory " + path);
			Core.getInstance().getServices().createDirectory(path);
		}

		cz.zcu.kiv.os.core.Process shell = Core.getInstance().getServices().createProcess(this, "shell", null, this.stdIn, this.stdOut, this.stdErr, "/home/" + login);
		shell.join();

	}
}
