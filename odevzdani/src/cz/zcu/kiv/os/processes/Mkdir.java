package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.core.Core;
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
		"It is possible to enter the whole direcory path.\n"+
		"OPTIONS:\n"+
		"  -v, --verbose    write a message for each created directory\n"+
		"      --help       display this help and exit\n"+
		"EXAMPLES:\n"+
		"  mkdir dir_1 dir_2   Creates 2 directories 'dir_1' and 'dir_2'\n"+
		"                      in the current working direcotory\n"+
		"  mkdir /dir_1/dir_2  Creates new directory 'dir_1' and another\n"+
		"                      directory 'dir_2' inside 'dir_1'\n";
	
	/**
	 * Returns manual page for Mkdir process
	 * 
	 * @return string with manual page
	 */
	public static String getManualPage() {
		return helpText; 
	}
	
	/**
	 * Public constructor, sets default values
	 */
	public Mkdir() {
		this.names = new ArrayList<String>();
	}
	
	@Override
	protected void run(String[] args) throws Exception {

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
					this.getOutputStream().writeLine("mkdir: invalid option " + arg);
					this.getOutputStream().writeLine("Try 'mkdir --help' for more information.");
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
		
		boolean success;
		
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
