/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.core.Core;
import cz.zcu.kiv.os.core.interrupts.Signals;
import java.text.ParseException;

/**
 * Process for sending sigkill signal to other processess.
 * @author bydga
 */
public class Kill extends cz.zcu.kiv.os.core.Process {

	private static final String helpText =
			"\nUsage: kill [OPTION] [PID]...\n"+
			"Kill process(es) with specified PID(s).\n"+
			"OPTION:\n"+
			"      --help           display this help and exit\n";
	
	/**
	 * Returns manual page for Echo process.
	 * 
	 * @return string with manual page
	 */
	public static String getManualPage() {
		return helpText;
	}
	
	@Override
	protected void run(String[] args) throws Exception {

		for (int i = 1; i < args.length; i++) {

			if(args[i].equals("--help")) {
				this.getOutputStream().writeLine(Kill.getManualPage());
				return; //exit
			}
			
			try {
				int pid = Integer.parseInt(args[i]);
				this.getOutputStream().writeLine("Killing process " + pid);
				Core.getInstance().getServices().dispatchSystemSignal(pid, Signals.SIGKILL);
			} catch (NumberFormatException ex) {
				this.getOutputStream().writeLine("Pid not a number");
			}
		}


	}
}
