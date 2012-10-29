/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.Utilities;
import cz.zcu.kiv.os.core.Core;
import cz.zcu.kiv.os.core.NoSuchProcessException;
import cz.zcu.kiv.os.core.Process;
import cz.zcu.kiv.os.core.device.IInputDevice;
import cz.zcu.kiv.os.core.device.IOutputDevice;
import cz.zcu.kiv.os.core.device.InputDevice;
import cz.zcu.kiv.os.core.device.OutputDevice;
import cz.zcu.kiv.os.terminal.InputParser;
import cz.zcu.kiv.os.terminal.ParseException;
import cz.zcu.kiv.os.terminal.ParseResult;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bydga
 */
public class Shell extends Process {
	
	protected PipedInputStream pis = null;
	
	private Process createProcess(Process caller, ParseResult parseResult, int depth) throws NoSuchProcessException {

		//recursively go into the pipeline - last process's parent is this Shell
		Process par = parseResult.pipeline == null ? caller : this.createProcess(caller, parseResult.pipeline, depth + 1);
		
		IInputDevice in = this.stdIn;
		IOutputDevice out = this.stdOut;
		IOutputDevice err = this.stdErr;
		
		if (parseResult.stdIn != null) {
			try {
				in = new InputDevice(new FileInputStream(parseResult.stdIn));
			} catch (FileNotFoundException ex) {
				Utilities.log("Error when moving stdin to " + parseResult.stdOut);
			}
		}
		
		if (parseResult.stdOut != null) {
			try {
				out = new OutputDevice(new FileOutputStream(parseResult.stdOut, parseResult.stdOutAppend));
			} catch (FileNotFoundException ex) {
				Utilities.log("Error when moving stdout to " + parseResult.stdOut);
			}
		}
		
		if (parseResult.stdErr != null) {
			try {
				err = new OutputDevice(new FileOutputStream(parseResult.stdErr, parseResult.stdErrAppend));
			} catch (FileNotFoundException ex) {
				Utilities.log("Error when moving stderr to " + parseResult.stdErr);
			}
		}
		
		//there is a chain of processes and this is the input stream from parent's process - connect it to its child
		if (this.pis != null) {
			try {
				out = new OutputDevice(new PipedOutputStream(this.pis));
				Utilities.log("connecting piped output stream for process " + parseResult.args[0] + " with " + this.pis.toString() );
				this.pis = null;
			} catch (IOException ex) {
				Logger.getLogger(Shell.class.getName()).log(Level.SEVERE, null, ex);
			}
			
		}
		
		if (depth > 0) { //process in pipeline - input of this process is not std
			this.pis = new PipedInputStream(); //store the stream for its child
			Utilities.log("setting piped input stream "+ this.pis.toString() +"for process " + parseResult.args[0]);
			in = new InputDevice(this.pis);
		} else {
			this.pis = null;
		}

		return Core.getInstance().getServices().createProcess(par, parseResult.args[0], parseResult.args, in, out, err);
	}
	
	@Override
	protected void run(String[] args) throws Exception {
		InputParser parser = new InputParser();
		
		while (true) {
			Utilities.log("reading");
			String command = this.stdIn.readLine();
			if (command != null) {
				this.stdOut.writeLine(command);
				try {
					ParseResult pr = parser.parse(command);
					this.createProcess(this, pr, 0);
					Utilities.log("finished command " + command);
				} catch (ParseException e) {
					this.stdOut.writeLine("Invalid input: " + e.getMessage());
				} catch (NoSuchProcessException ex) {
					this.stdOut.writeLine("Invalid process name");
				}
			} else {
				//write empty line
				this.stdOut.writeLine("");
			}
		}
	}
}
