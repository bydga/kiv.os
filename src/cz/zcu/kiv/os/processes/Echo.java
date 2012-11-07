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
				"------------------------------\n"+
				"This is help for ECHO process \n"+
				"This help is not completed yet\n"+
				"This is help for ECHO process \n"+
				"This help is not completed yet\n"+
				"This is help for ECHO process \n"+
				"This help is not completed yet\n"+
				"------------------------------\n";
	
	@Override
	protected void run(String[] args) throws Exception {

		ProcessDefinedOptions definedOptions = new ProcessDefinedOptions();
		definedOptions.addOption("-e", 0);
		ProcessArgs processArgs = new ProcessArgs(args, definedOptions);	
		
		Utilities.echoArgs(processArgs, this.stdOut);
		
		ProcessOption[] options = processArgs.getAllOptions();
		
		for (int i = 0; i < options.length; i++) {
			
			if(options[i].getOptionName().equals("--help")) {
				this.stdOut.writeLine(this.helpText);
				return;
			}
			else if(options[i].getOptionName().equals("-n")) {
			
			}
		}
		
		String[] names = processArgs.getAllNames();
		
		String echoText = "";
		for (int i = 0; i < names.length; i++) {
			echoText += names[i];
		}
		this.stdOut.writeLine(echoText);
	}
	
	
	
	
}