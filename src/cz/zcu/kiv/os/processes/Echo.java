package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.core.ProcessArgs;
import cz.zcu.kiv.os.core.ProcessDefinedOptions;

/**
 *
 * @author Jiri Zikmund
 */
public class Echo extends cz.zcu.kiv.os.core.ProcessWithArgs {

	@Override
	protected String getHelpText() {
		return	"------------------------------\n"+
				"This is help for ECHO process \n"+
				"This help is not completed yet\n"+
				"This is help for ECHO process \n"+
				"This help is not completed yet\n"+
				"This is help for ECHO process \n"+
				"This help is not completed yet\n"+
				"------------------------------\n";
	}

	@Override
	protected ProcessDefinedOptions getDefinedOptions() {		
		ProcessDefinedOptions options = new ProcessDefinedOptions();
		options.addOption("-a");
		options.addOption("-b", 2);
		options.addOption("-c", 4);
		return options;
	}
	
	@Override
	protected void runWithArgs(ProcessArgs processArgs) throws Exception {

		this.writeln("ARGUNENTY OK");
		
	}
}