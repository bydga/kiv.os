/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.core.ProcessArgs;
import cz.zcu.kiv.os.core.ProcessDefinedOptions;
import cz.zcu.kiv.os.core.ProcessOption;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;


/**
 *
 * @author Jiri Zikmund
 */
public class Cat extends cz.zcu.kiv.os.core.Process{

	private boolean optionNumberLine = false;
	private int lineNumber = 0;
	
	private final String helpText =
				"------------------------------\n"+
				"This is help for CAT process  \n"+
				"This help is not completed yet\n"+
				"This is help for CAT process  \n"+
				"This help is not completed yet\n"+
				"This is help for CAT process  \n"+
				"This help is not completed yet\n"+
				"------------------------------\n";

	@Override
	protected void run(String[] args) throws Exception {
		
		ProcessDefinedOptions definedOptions = new ProcessDefinedOptions();
		definedOptions.addOption("-n", 0);
		ProcessArgs processArgs = new ProcessArgs(args, definedOptions);	
		
		ProcessOption[] options = processArgs.getAllOptions();
		
		// apply all options
		for (int i = 0; i < options.length; i++) {
			if(options[i].getOptionName().equals("-n")) {
				this.optionNumberLine = true;
			}
		}
		
		// write all files
		String[] names = processArgs.getAllNames();
		//
		for (int i = 0; i < names.length; i++) {
			
			if( names[i].equals("-") ) {
				repeatMode();
				continue;
			}
			else {
				try {
					this.readFile(names[i]);
				} catch (Exception e) {
					// TODO: pokračovat nebo ukončit proces?
					this.stdOut.writeLine("Error while opening file: '" + names[i] + "'");
					//this.stop( "Error while opening file: " + e.getMessage() );
					return;
				}
				
			}
		}
		
	}
	
	
	private void repeatMode() throws Exception {
		String line = this.stdIn.readLine();
		//TODO: null instead of exit
		//while(line != null) {
		while(!line.equals("")) {
			this.lineNumber++;
			if(this.optionNumberLine == true) {
				line = "   " +this.lineNumber +  "  " + line;
			}
			this.stdOut.writeLine(line);
			line = this.stdIn.readLine();
		}
	}
	
	private void readFile(String fileName) throws Exception {
		
		FileInputStream fstream = new FileInputStream(fileName);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;
		while ((line = br.readLine()) != null) {
			this.lineNumber++;
			if(this.optionNumberLine == true) {
				line = "   " +this.lineNumber +  "  " + line;
			}
			this.stdOut.writeLine(line);
		}
		in.close();
	}
	
	
}
