/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.core.Core;
import java.text.ParseException;

/**
 *
 * @author bydga
 */
public class Kill extends cz.zcu.kiv.os.core.Process {

	@Override
	protected void run(String[] args) throws Exception {

		for (int i = 1; i < args.length; i++) {

			try {
				int pid = Integer.parseInt(args[i]);
				this.getOutputStream().writeLine("Killing process " + pid);
				Core.getInstance().getServices().killProcess(pid);
			} catch (NumberFormatException ex) {
				this.getOutputStream().writeLine("Pid not a number");
			}
		}


	}
}
