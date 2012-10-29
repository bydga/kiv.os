/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.Utilities;
import cz.zcu.kiv.os.core.Core;
import cz.zcu.kiv.os.core.NoSuchProcessException;
import cz.zcu.kiv.os.core.Process;
import cz.zcu.kiv.os.core.device.*;
import cz.zcu.kiv.os.terminal.InputParser;
import cz.zcu.kiv.os.terminal.ParseException;
import cz.zcu.kiv.os.terminal.ParseResult;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bydga
 */
public class Shell extends Process {

	protected PipedInputStream pis = null;

	private IOutputDevice createOutput(String path, boolean append, IOutputDevice nullValue) {
		if (path != null) {

			try {
				return new OutputDevice(new FileOutputStream(path, append));
			} catch (FileNotFoundException ex) {
				Utilities.log("Error when moving output to " + path);
			}
		}

		return nullValue;
	}

	private IInputDevice createInput(String path, IInputDevice nullValue) {
		if (path != null) {

			try {
				return new InputDevice(new FileInputStream(path));
			} catch (FileNotFoundException ex) {
				Utilities.log("Error when moving stdout to " + path);
			}
		}

		return nullValue;
	}

	private void createProcess(ParseResult parseResult) throws NoSuchProcessException {

		List<Process> list = new ArrayList<Process>();
		IInputDevice in = this.createInput(parseResult.stdIn, this.getInputStream());
		IOutputDevice out;
		IOutputDevice err;
		Process parentProcess = this;


		while (parseResult.pipeline != null) {
			IOQueueDevice pipe = new IOQueueDevice();
			out = this.createOutput(parseResult.stdOut, parseResult.stdOutAppend, pipe);
			err = this.createOutput(parseResult.stdErr, parseResult.stdErrAppend, this.getErrorStream());
			in = this.createInput(parseResult.stdIn, in);
			
			Process p = Core.getInstance().getServices().createProcess(parentProcess, parseResult.args[0], parseResult.args, in, out, err);
			list.add(p);
			in = pipe;
			parentProcess = p;

			parseResult = parseResult.pipeline;
		}

		in = this.createInput(parseResult.stdIn, in);
		out = this.createOutput(parseResult.stdOut, parseResult.stdOutAppend, this.getOutputStream());
		err = this.createOutput(parseResult.stdErr, parseResult.stdErrAppend, this.getErrorStream());

		Process p = Core.getInstance().getServices().createProcess(parentProcess, parseResult.args[0], parseResult.args, in, out, err);
		list.add(p);
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
					ParseResult result = parser.parse(command);
					this.createProcess(result);
					Utilities.log("finished processing command " + command);
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
