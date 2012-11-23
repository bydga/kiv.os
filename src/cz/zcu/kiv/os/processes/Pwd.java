/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.processes;

/**
 *
 * @author bydga, Jiri Zikmund
 */
public class Pwd extends cz.zcu.kiv.os.core.Process {

	private static final String helpText =
		"\nUsage: pwd [OPTION]\n" +
		"Print the full filename of the current working directory.\n"+
		"OPTION:\n"+
		"      --help        display this help and exit\n";
	
	public static String getManualPage() {
		return helpText; 
	}
	
	@Override
	protected void run(String[] args) throws Exception {
		
		if(args.length > 1) {
			if(args[1].equals("--help")) {
				this.getOutputStream().writeLine(Pwd.getManualPage());
				return; //exit
			}
			else {
				this.getOutputStream().writeLine("pwd: invalid option " + args[1]);
				this.getOutputStream().writeLine("Try 'pwd --help' for more information.");
				return; //exit
			}
		}
		
		this.getOutputStream().writeLine(this.getWorkingDir());
		
	}
	
}
