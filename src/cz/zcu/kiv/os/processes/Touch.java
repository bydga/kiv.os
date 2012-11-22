/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.core.Core;
import cz.zcu.kiv.os.core.device.IOutputDevice;
import cz.zcu.kiv.os.core.filesystem.InvalidPathCharactersException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bydga, Jiri Zikmund
 */
public class Touch extends cz.zcu.kiv.os.core.Process{

	private List<String> names;
	
	private static final String helpText =
		"\nUsage: touch [OPTION]... FILE...\n" +
		"Update access times of specified FILE(s) to the current time.\n"+
		"OPTIONS:\n"+
		"      --help     display this help and exit\n";	
	
	public static String getManualPage() {
		return helpText; 
	}
	
	public Touch() {
		this.names = new ArrayList<String>();
	}
	
	@Override
	protected void run(String[] args) throws Exception {
		
		for(int i = 1; i < args.length; i++) {
			
			String arg = args[i];
			// options
			if(arg.startsWith("-") && arg.length() > 1) {
				
				if(arg.equals("--help")) {
					this.getOutputStream().writeLine(Touch.getManualPage());
					return; //exit
				}
				else {
					this.getOutputStream().writeLine("touch: invalid option " + arg);
					this.getOutputStream().writeLine("Try 'touch --help' for more information.");
					return; //exit
				}
			}
			// names
			else {
				this.names.add(arg);
			}
		}
		
		if(this.names.isEmpty()) {
			this.getOutputStream().writeLine("touch: no file name specified");
			this.getOutputStream().writeLine("Try 'touch --help' for more information.");
			return; //exit
		}

		for(String name : this.names) {
			try {
				IOutputDevice file = Core.getInstance().getServices().openFileForWrite(this, name, true);
				file.EOF();
			} catch (InvalidPathCharactersException ex) {
				this.getOutputStream().writeLine("Following characters cannot be used as filename: " + InvalidPathCharactersException.invalidCharsList());
				return;
			}
		}	
		
	}
	
}
