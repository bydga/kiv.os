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

	private final String helpText =
				"Usage: echo [OPTION]... [STRINGS]...\n"+
				" Echo the STRING(s) to standard output.\n"+
				"      --help               display this help and exit";
	
	@Override
	protected void run(String[] args) throws Exception {
		
		if(args[1].equals("--help") && args.length < 3) {
			this.getOutputStream().writeLine(this.helpText);
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