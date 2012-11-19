/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.Utilities;
import cz.zcu.kiv.os.core.Core;
import cz.zcu.kiv.os.core.ProcessArgs;
import cz.zcu.kiv.os.core.ProcessDefinedOptions;
import cz.zcu.kiv.os.core.ProcessOption;
import cz.zcu.kiv.os.core.device.IInputDevice;
import java.io.FileNotFoundException;


/**
 *
 * @author Jiri Zikmund
 */
public class Cat extends cz.zcu.kiv.os.core.Process{

	private boolean optionLineNumber = false;
	private boolean optionShowEnds = false;
	private int lineNumber = 0;
	
	private static final String helpText =
				"Usage: cat [OPTION] [FILE]...\n"+
				"Concatenate FILE(s), or standard input, to standard output.\n"+
				"  -E, --show-ends          display $ at end of each line\n"+
				"  -n, --number             number all output lines\n"+
				"      --help               display this help and exit\n"+
				"\n"+
				"With no FILE, or when FILE is -, read standard input.\n"+
				""+
				"Examples:\n"+
				"  cat f - g  Output f's contents, then standard input, then g's contents.\n"+
				"  cat        Copy standard input to standard output.";
	
	@Override
	protected void run(String[] args) throws Exception {
		
		ProcessDefinedOptions definedOptions = new ProcessDefinedOptions();
		definedOptions.addOption("-n", 0);
		definedOptions.addOption("--number", 0);
		definedOptions.addOption("-E", 0);
		definedOptions.addOption("--show-ends", 0);
		definedOptions.addOption("--help", 0);
		ProcessArgs processArgs = new ProcessArgs(args, definedOptions, "-");
		
		ProcessOption[] options = processArgs.getAllOptions();
		
		//Utilities.echoArgs(processArgs, this.getOutputStream());
		
		// apply all options
		for (int i = 0; i < options.length; i++) {
			if(options[i].notRecognized()) {
				StringBuilder bf = new StringBuilder();
				bf.append("cat: invalid option ");
				bf.append(options[i].getOptionName());
				bf.append("\n");
				bf.append("Try 'cat --help' for more information.");
				this.getOutputStream().writeLine(bf.toString());
				return; // exit
			}
			else if(options[i].getOptionName().equals("--help")) {
				this.getOutputStream().writeLine(this.helpText);
				return; //exit
			}
			else if(options[i].getOptionName().equals("-n") || options[i].getOptionName().equals("--number")) {
				this.optionLineNumber = true;
			}
			else if(options[i].getOptionName().equals("-E") || options[i].getOptionName().equals("--show-ends")) {
				this.optionShowEnds = true;
			}
		}
		
		// write all files
		String[] names = processArgs.getAllNames();
		if(names.length == 0) {
			String userInput = this.readStandardInput();
			if(userInput != null) {
				this.getOutputStream().writeLine(userInput);
			}
		}
		for (int i = 0; i < names.length; i++) {
			
			if( names[i].equals("-") ) {
				String userInput = this.readStandardInput();
				if(userInput != null) {
					this.getOutputStream().writeLine(userInput);
				}
			}
			else {
				try {
					IInputDevice file = Core.getInstance().getServices().openFileForRead(this, names[i]);
					this.readFile(file);
				} catch (FileNotFoundException e) {
					this.getOutputStream().writeLine("cat: " + names[i] + ": No such file");
				}
				
			}
		}
		
	}
	
	private void readFile(IInputDevice input) throws Exception {
		
		String line;
		while ((line = input.readLine()) != null) {
			line = this.editLineByOptions(line);
			this.getOutputStream().writeLine(line);
		}
		input.detach();
	}
	
	private String readStandardInput() throws Exception {
		
		StringBuilder bf = new StringBuilder();
		String newLineChar = "";
		String line = this.getInputStream().readLine();
		
		while(line != null) {
			this.checkForStop();
//			this.getOutputStream().writeLine(line);
			line = this.editLineByOptions(line);
			if(this.optionLineNumber == true) {
				bf.append(newLineChar);
				bf.append(line);
				newLineChar = "\n";
			}
			else {
				this.getOutputStream().writeLine(line);
			}
			line = this.getInputStream().readLine();
		}
		if(this.optionLineNumber == true) {
			return bf.toString();
		}
		else return null;
	}

	
	private String editLineByOptions(String line) {
		
		if(this.optionShowEnds == true) {
			line += '$';
		}
		
		if(this.optionLineNumber == true) {
			this.lineNumber++;
			line = "   " + this.lineNumber +  "  " + line;
		}
		
		return line;
	}

	public static String getManualPage() {
		return helpText;
	}

}
