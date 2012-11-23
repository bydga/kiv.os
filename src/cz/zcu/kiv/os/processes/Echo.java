package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.Utilities;
import cz.zcu.kiv.os.core.ProcessArgs;
import cz.zcu.kiv.os.core.ProcessDefinedOptions;
import cz.zcu.kiv.os.core.ProcessOption;

/**
 *
 * @author Jiri Zikmund
 */
public class Echo extends cz.zcu.kiv.os.core.Process {

	private static final String helpText =
			"\nUsage: echo [OPTION] [STRING]...\n"+
			"Echo all specified STRING(s) to standard output.\n"+
			"OPTION:\n"+
			"      --help           display this help and exit\n";
	
	public static String getManualPage() {
		return helpText;
	}
	
	@Override
	protected void run(String[] args) throws Exception {
		
		if(args.length == 2 && args[1].equals("--help")) {
			this.getOutputStream().writeLine(Echo.getManualPage());
			return;
		}
		
		StringBuilder bf = new StringBuilder();
		String space = "";
		for (int i = 1; i < args.length; i++) {
			bf.append(space);
			bf.append(args[i]);
			space = " ";
		}
		this.getOutputStream().writeLine(bf.toString());
	}
	
}