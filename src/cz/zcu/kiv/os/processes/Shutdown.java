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
public class Shutdown extends cz.zcu.kiv.os.core.Process {

	@Override
	protected void run(String[] args) throws Exception {
		
		this.getOutputStream().writeLine("System is going down now...");
		Core.getInstance().getServices().shutdown(this);
		
	}
	
}
