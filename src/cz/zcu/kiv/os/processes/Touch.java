/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.core.Core;
import cz.zcu.kiv.os.core.device.IInputDevice;
import cz.zcu.kiv.os.core.device.IOutputDevice;

/**
 *
 * @author bydga
 */
public class Touch extends cz.zcu.kiv.os.core.Process{

	@Override
	protected void run(String[] args) throws Exception {
		
		if (args.length < 1) {
			this.getOutputStream().writeLine("usage: touch filename");
		}
		
		IOutputDevice file = Core.getInstance().getServices().openFileForWrite(this, args[1], true);
		file.EOF();
		
		
		
	}
	
}
