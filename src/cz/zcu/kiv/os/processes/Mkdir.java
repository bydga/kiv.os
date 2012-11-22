package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.core.Core;
//TODO java.lang.NoClassDefFoundError: cz/zcu/kiv/os/core/filesystem/InvalidPathCharactersException
import cz.zcu.kiv.os.core.filesystem.InvalidPathCharactersException;
import java.util.ArrayList;
import java.util.List;

/**
 * Process class which creates all directories given as arguments.
 *
 * Whole directory-tree branch is created if given as argument.
 *
 * @author Jakub Danek, Jiri Zikmund
 */
public class Mkdir extends cz.zcu.kiv.os.core.Process{

	
	private boolean optionVerbose = false;
	private List<String> names;
	
	private static final String helpText =
		"\nUsage: mkdir [OPTION]... DIRECTORY...\n" +
		"Create spicified DIRECTORY(ies).\n"+
		"OPTIONS:\n"+
		"-v, --verbose    write a message for each created direcotry\n"+
		"      --help     display this help and exit\n";	
	
	public static String getManualPage() {
		return helpText; 
	}
	
	public Mkdir() {
		this.names = new ArrayList<String>();
	}
	
	@Override
	protected void run(String[] args) throws Exception {
		boolean success;
		
		for(int i = 1; i < args.length; i++) {
			
			String arg = args[i];
			// options
			if(arg.startsWith("-") && arg.length() > 1) {
				
				if(arg.equals("-v") || arg.equals("--verbose")) {
					this.optionVerbose = true;
				}
				else if(arg.equals("--help")) {
					this.getOutputStream().writeLine(Mkdir.getManualPage());
					return; //exit
				}
				else {
					StringBuilder bf = new StringBuilder();
					this.getOutputStream().writeLine("mkdir: invalid option " + arg);
					this.getOutputStream().writeLine("Try 'sort --help' for more information.");
					return; //exit
				}
			}
			// names
			else {
				this.names.add(arg);
			}
			
		}
		
		if(this.names.isEmpty()) {
			this.getOutputStream().writeLine("mkdir: no directory name specified");
			this.getOutputStream().writeLine("Try 'mkdir --help' for more information.");
			return;
		}
		
		for(String name : this.names) {
			try {
				success = Core.getInstance().getServices().createDirectory(this, name);
			} catch (InvalidPathCharactersException ex) {
				this.getOutputStream().writeLine("mkdir: following characters cannot be used as filename: " + InvalidPathCharactersException.invalidCharsList());
				return;
			}

			if(success) {
				if(this.optionVerbose) {
					this.getOutputStream().writeLine("mkdir: created directory '" + name + "'");
				}
			}
			else {
				this.getOutputStream().writeLine("mkdir: directory \"" + name + "\" couldn't be created!");
			}
		}
		
	}
	
}
