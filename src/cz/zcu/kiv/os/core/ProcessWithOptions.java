/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.core;

import cz.zcu.kiv.os.Utilities;
import cz.zcu.kiv.os.core.device.IInputDevice;
import cz.zcu.kiv.os.core.device.IOutputDevice;
import cz.zcu.kiv.os.core.device.OutputDevice;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bydga, Jiri Zikmund
 */
public abstract class ProcessWithOptions extends Process {

	protected String helpText =	  "-------------------------------"
								+ "There is no help text available"
								+ "-------------------------------";
	
	protected String[] definedOptions = {};
	
	private ProcessArgs processArgs = null;
	
	protected abstract void runWithOptions(String[] args, String[] names, String[] options) throws Exception;
	
	@Override
	public void run(String[] args) throws Exception {
		this.processArgs = new ProcessArgs(this.args, this.definedOptions);
		
		String[] options = this.processArgs.getOptions();
		String[] names = this.processArgs.getNames();
		
		for (int i = 0; i < options.length; i++) {
			
			if(options[i].equals("--help")) {
				this.writeln(this.helpText);
				return;
			}
			
			if(!processArgs.validOption(options[i])) {
				this.writeln( processArgs.getProcessName() + ": invalid option " + options[i] );
				this.writeln( "Try '" + processArgs.getProcessName() + " --help' for more information.");
				//this.stop( "undefined option " + options[i] );
				return;
				// TODO: this.stop or return?
			}
		}
		
		ProcessWithOptions.this.runWithOptions(this.args, this.processArgs.getNames(), this.processArgs.getOptions());
	}
	
	private void writeln(String line) {
		try {
			this.stdOut.writeLine(line);
		} catch (Exception ex) {
			this.stop("stdOut writeLine exception: " + ex.getMessage());
		}
	}
	
	private String readln() {
		try {
			return this.stdIn.readLine();
		} catch (Exception ex) {
			this.stop("stdIn readLine exception: " + ex.getMessage());
			return null;
		}
	}
	
	
}