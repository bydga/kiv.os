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
import cz.zcu.kiv.os.terminal.InputParser;
import cz.zcu.kiv.os.terminal.ParseException;
import cz.zcu.kiv.os.terminal.ParseResult;

/**
 *
 * @author bydga
 */
public class Shell extends Process {

	private Process createProcess(Process caller, ParseResult parseResult) throws NoSuchProcessException {

		//recursively go into the pipeline - last process's parent is this Shell
		Process par = parseResult.pipeline == null ? caller : this.createProcess(caller, parseResult.pipeline);
		
		IInputDevice in = this.stdIn;
		IOutputDevice out = this.stdOut;
		IOutputDevice err = this.stdErr;
		//TODO: connect streams, init in/out/err...

		return Core.getInstance().getServices().createProcess(par, parseResult.args[0], parseResult.args, in, out, err);
	}


	@Override
	protected void run(String[] args) throws Exception {
		InputParser parser = new InputParser();

		while (true) {
			String command = this.stdIn.readLine();
			if (command != null) {
                            this.stdOut.writeLine(command);
                            try {
                                    ParseResult pr = parser.parse(command);
                                    this.createProcess(this, pr);
                                    Utilities.log("finished command " + command);
                            } catch (ParseException e) {
                                    this.stdOut.writeLine("Invalid input: " + e.getMessage());
                            }
			} else {
                            //write empty line
                            this.stdOut.writeLine("");
                        }
		}
	}
}
