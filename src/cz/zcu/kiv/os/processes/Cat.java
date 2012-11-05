/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.core.ProcessArgs;
import cz.zcu.kiv.os.core.ProcessDefinedOptions;
import cz.zcu.kiv.os.core.ProcessOption;
import java.io.*;

/**
 *
 * @author Jiri Zikmund
 */
public class Cat extends cz.zcu.kiv.os.core.ProcessWithArgs{

	private boolean optionNumberLine = false;
	
	@Override
	protected String getHelpText() {
		return	"------------------------------\n"+
				"This is help for CAT process  \n"+
				"This help is not completed yet\n"+
				"This is help for CAT process  \n"+
				"This help is not completed yet\n"+
				"This is help for CAT process  \n"+
				"This help is not completed yet\n"+
				"------------------------------\n";
	}

	@Override
	protected ProcessDefinedOptions getDefinedOptions() {
		ProcessDefinedOptions options = new ProcessDefinedOptions();
		options.addOption("-n", 0);
		return options;
	}

	@Override
	protected void runWithArgs(ProcessArgs processArgs) throws Exception {
		
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
					writeln("Error while opening file: '" + names[i] + "'");
					//this.stop( "Error while opening file: " + e.getMessage() );
					return;
				}
				
			}
		}
		
	}
	
	
	private void repeatMode() {
		String line = readln();
		//TODO: null instead of exit
		//while(line != null) {
		while(!line.equals("")) {
			writeln(line);
			line = readln();
		}
	}
	
	private void readFile(String fileName) throws FileNotFoundException, IOException {

		int lineNumber = 0;
		
		FileInputStream fstream = new FileInputStream(fileName);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;
		while ((line = br.readLine()) != null) {
			lineNumber++;
			if(this.optionNumberLine == true) {
				line = "  " +lineNumber +  ": " + line;
			}
			writeln(line);
		}
		in.close();
	}
	
	
}
