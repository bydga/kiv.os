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
public class Mkdir extends cz.zcu.kiv.os.core.Process{

	@Override
	protected void run(String[] args) throws Exception {
		
		if (args.length > 1) {
			Core.getInstance().getServices().createDirectory(this, args[1]);
		}
		
	}
	
}
