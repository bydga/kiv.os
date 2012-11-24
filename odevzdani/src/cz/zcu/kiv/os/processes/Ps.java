package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.Utilities;
import cz.zcu.kiv.os.core.Core;
import cz.zcu.kiv.os.core.ProcessInfo;
import java.util.List;

/**
 * Writes information about active processes to the standard output.
 * 
 * @author bydga
 */
public class Ps extends cz.zcu.kiv.os.core.Process {

	private static final String helpText =
		"\nUsage: ps [OPTION]\n" +
		"Show information about active processes.\n"+
		"OPTION:\n"+
		"      --help        display this help and exit\n";
	
	/**
	 * Returns manual page for Ps process
	 * 
	 * @return string with manual page
	 */
	public static String getManualPage() {
		return helpText; 
	}
	
	@Override
	protected void run(String[] args) throws Exception {

		if(args.length > 1) {
			if(args[1].equals("--help")) {
				this.getOutputStream().writeLine(Ps.getManualPage());
				return; //exit
			}
			else {
				this.getOutputStream().writeLine("ps: invalid option " + args[1]);
				this.getOutputStream().writeLine("Try 'ps --help' for more information.");
				return; //exit
			}
		}
		
		StringBuilder builder = new StringBuilder();
		int maxLength = 3; //PID.length
		List<ProcessInfo> list = Core.getInstance().getServices().getProcessTableData();
		for (ProcessInfo info : list) {
			String s = "" + info.pid;
			maxLength = s.length() > maxLength ? s.length() : maxLength;
		}
		String header = String.format("%" + maxLength + "s CMD", "PID");
		for (ProcessInfo info : list) {
			builder.append(String.format("%" + maxLength + "s", "" +info.pid)).append(" ").append(info.processName).append("\n");
		}
		
		this.getOutputStream().writeLine(header);
		this.getOutputStream().writeLine(builder.toString());
	}
}
