package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.core.Core;

/**
 * Process that initiates the core shutdown procedure.
 * 
 * @author bydga
 */
public class Shutdown extends cz.zcu.kiv.os.core.Process {

	private static final String helpText =
		"\nUsage: shutdown [OPTION]\n" +
		"Shut the system down in a secure way.\n"+
		"OPTION:\n"+
		"      --help        display this help and exit\n";
	
	/**
	 * Returns manual page for Shutdown process
	 * 
	 * @return string with manual page
	 */
	public static String getManualPage() {
		return helpText; 
	}
	
	@Override
	protected void run(String[] args) throws Exception {
		
		if(args.length > 1) {
			if(args[1].equals("--help")) {
				this.getOutputStream().writeLine(Shutdown.getManualPage());
				return; //exit
			}
			else {
				this.getOutputStream().writeLine("shutdown: invalid option " + args[1]);
				this.getOutputStream().writeLine("Try 'shutdown --help' for more information.");
				return; //exit
			}
		}
		
		this.getOutputStream().writeLine("System is going down now...");
		Core.getInstance().getServices().shutdown(this);
		
	}
	
}
