package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.core.Core;

/**
 * Process class which creates all directories given as arguments.
 *
 * Whole directory-tree branch is created if given as argument.
 *
 * @author Jakub Danek
 */
public class Mkdir extends cz.zcu.kiv.os.core.Process{

	@Override
	protected void run(String[] args) throws Exception {
		boolean success;
		
		for(int i = 1; i < args.length; i++) {
			success = Core.getInstance().getServices().createDirectory(this, args[i]);

			if(!success) {
				this.getOutputStream().writeLine("Directory: \"" + args[i] + "\" couldnt be created!");
			}
		}
		
	}
	
}
