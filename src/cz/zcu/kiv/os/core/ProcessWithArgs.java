/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.core;

/**
 *
 * @author bydga, Jiri Zikmund
 */
public abstract class ProcessWithArgs extends Process {

	protected abstract String getHelpText();	
	protected abstract ProcessDefinedOptions getDefinedOptions();
	protected abstract void runWithArgs(ProcessArgs processArgs) throws Exception;
	
	private ProcessArgs processArgs;
	
	@Override
	public void run(String[] args) throws Exception {
		
		this.processArgs = new ProcessArgs(this.args, this.getDefinedOptions());
		
		ProcessOption[] options = this.processArgs.getAllOptions();
		
		for (int i = 0; i < options.length; i++) {
			if(options[i].getOptionName().equals("--help")) {
				this.writeln(this.getHelpText());
				return;
			}
		}
		
		for (int i = 0; i < options.length; i++) {
			
			if(options[i].isDefined() == false) {
				this.writeln( processArgs.getProcessName() + ": invalid option " + options[i].getOptionName() );
				this.writeln( "Try '" + processArgs.getProcessName() + " --help' for more information.");
				return;
			}
			
			if(options[i].isArgMissing()) {
				this.writeln( processArgs.getProcessName() + ": missing argument "+ options[i].getMissingArgPos() +" for option " + options[i].getOptionName() );
				this.writeln( "Try '" + processArgs.getProcessName() + " --help' for more information.");
				return;
			}
		}
		
		ProcessWithArgs.this.runWithArgs(this.processArgs);
	}
	
	
	protected void echoArgs() {

		String[] optionNames = this.processArgs.getAllOptionNames();
		String[] names = this.processArgs.getAllNames();
		
		this.writeln("----NAMES------");
		for (int i = 0; i < names.length; i++) {
			this.writeln(names[i]);
		}
		this.writeln("---------------");
		
		this.writeln("----OPTIONS----");
		for (int i = 0; i < optionNames.length; i++) {
			
			ProcessOption option = this.processArgs.getOption(optionNames[i]);
			String[] optionArgs = option.getOptionArgs();
			
			String argString = "";
			for (int j = 0; j < optionArgs.length; j++) {
				argString += optionArgs[j] + ", ";
			}
			
			if(option.isDefined() == false) {
				argString += " (UNDEFINED) ";
			}
			
			if(option.isArgMissing()) {
				argString += " (missing arg "+ option.getMissingArgPos() +") ";
			}
			
			this.writeln(option.getOptionName() + ": " + argString);
			
		}
		this.writeln("---------------");
	}
	
	protected void writeln(String line) {
		try {
			this.stdOut.writeLine(line);
		} catch (Exception ex) {
			this.stop("stdOut writeLine exception: " + ex.getMessage());
		}
	}
	
	protected void writeln() {
		this.writeln("");
	}
	
	protected String readln() {
		try {
			return this.stdIn.readLine();
		} catch (Exception ex) {
			this.stop("stdIn readLine exception: " + ex.getMessage());
			return null;
		}
	}
	
	
}