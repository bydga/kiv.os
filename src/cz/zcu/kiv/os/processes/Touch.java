/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.core.Core;
import cz.zcu.kiv.os.core.device.IOutputDevice;
import cz.zcu.kiv.os.core.filesystem.InvalidPathCharactersException;

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

		try {
			IOutputDevice file = Core.getInstance().getServices().openFileForWrite(this, args[1], true);
			file.EOF();
		} catch (InvalidPathCharactersException ex) {
			this.getOutputStream().writeLine("Following characters cannot be used as filename: " + InvalidPathCharactersException.invalidCharsList());
			return;
		}
		
		
		
	}
	
}
