package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.core.Core;

/**
 * Process that initiates the core shutdown procedure.
 * @author bydga
 */
public class Shutdown extends cz.zcu.kiv.os.core.Process {

	@Override
	protected void run(String[] args) throws Exception {
		
		this.getOutputStream().writeLine("System is going down now...");
		Core.getInstance().getServices().shutdown(this);
		
	}
	
}
