/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.core.ProcessArgs;
import java.io.*;

import cz.zcu.kiv.os.core.Process;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Observer;

/**
 *
 * @author Jiri Zikmund
 */
public class Cat extends cz.zcu.kiv.os.core.Process{
	
// 	
//	TODO!!! Error outputs in whole process
//	
	private final String[] definedOptions = {
		"-n",
		"--help"
	};
	
	private boolean optionNumberLine = false;
	
	@Override
	public void run(String[] args) throws Exception {
		
		// no arguments
		if( args.length < 2 ) {
			repeatMode();
			return;
		}
		
		ProcessArgs processArgs = new ProcessArgs(args, this.definedOptions);
		
		// apply all options
		String[] options = processArgs.getOptions();
		//
		for (int i = 0; i < options.length; i++) {
			
			if( options[i].equals("--help") ) {
				showHelp();
				return;
			}
			
			if(!processArgs.validOption(options[i])) {
				writeln( processArgs.getProcessName() + ": invalid option " + options[i] );
				writeln( "Try 'cat --help' for more information.");
				this.stop( "undefined option " + options[i] );
				return;
				// TODO: this.stop or return?
			}
			
			if( options[i].equals("-n") ) {
				this.optionNumberLine = true;
			}	
		}
		
		// write all files
		String[] names = processArgs.getNames();
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
					this.stop( "Error while opening file: " + e.getMessage() );
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
	
	// TODO: finish help
	private void showHelp() {
		writeln("------------------------------");
		writeln("This is help for cat process");
		writeln("This help is not completed yet");
		writeln("This is help for cat process");
		writeln("This help is not completed yet");
		writeln("This is help for cat process");
		writeln("This help is not completed yet");
		writeln("------------------------------");
	}
	
	
	private void writeln(String line) {
		try {
			this.stdOut.writeLine(line);
		} catch (Exception ex) {
			this.stop("stdOut writeLine exception " + ex.getMessage());
		}
	}
	
	private String readln() {
		try {
			return this.stdIn.readLine();
		} catch (Exception ex) {
			this.stop("stdIn readLine exception " + ex.getMessage());
			return null;
		}
	}
	
	
	
}
