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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bydga
 */
public class Shell extends Process {

	private static final String EXIT_COMMAND = "exit";
	private static final String CWD_COMMAND = "cd";

	/**
	 * Initializes new instance of output device - suitable for forwarding process's output/error streams. If given path
	 * is null, last argument is used as a return value.
	 *
	 * @param path Path to a desired file
	 * @param append append to existing file or create/overwrite
	 * @param nullValue Value, that will be returned in case path is null - output is not forwarded
	 * @return
	 */
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

	/**
	 * Initializes new instance of in device - suitable for forwarding process's input stream. If given path is null,
	 * last argument is used as a return value.
	 *
	 * @param path Path to a desired file
	 * @param nullValue Value, that will be returned in case path is null - input is not forwarded
	 * @return
	 */
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

	/**
	 * Handles parseresult received from InputParser and creates process according to given input. In case there are
	 * more processes to be run in a pipeline, this method creates all of them.
	 *
	 * @param parseResult
	 * @return The newly created process, or in case there is the pipeline, the latest process.
	 * @throws NoSuchProcessException
	 */
	private Process createProcess(ParseResult parseResult) throws NoSuchProcessException {

		//only for debugging reasons
		List<Process> list = new ArrayList<Process>();
		IInputDevice in = this.createInput(parseResult.stdIn, this.getInputStream());
		IOutputDevice out;
		IOutputDevice err;


		//there is another process waiting in the pipeline for ouput of this process
		while (parseResult.pipeline != null) {
			//create the pipe
			IOQueueDevice pipe = new IOQueueDevice();
			out = this.createOutput(parseResult.stdOut, parseResult.stdOutAppend, pipe);
			err = this.createOutput(parseResult.stdErr, parseResult.stdErrAppend, this.getErrorStream());
			in = this.createInput(parseResult.stdIn, in);

			Process p = Core.getInstance().getServices().createProcess(this, parseResult.args[0], parseResult.args, in, out, err, this.getWorkingDir());
			list.add(p);
			//pipe was used as an output device here, remember it for next iteration, where it will stand as an input
			in = pipe;

			parseResult = parseResult.pipeline;
		}

		//handle last process of the pipeline - or in case of no pipeline the only executed process
		in = this.createInput(parseResult.stdIn, in);
		out = this.createOutput(parseResult.stdOut, parseResult.stdOutAppend, this.getOutputStream());
		err = this.createOutput(parseResult.stdErr, parseResult.stdErrAppend, this.getErrorStream());

		Process p = Core.getInstance().getServices().createProcess(this, parseResult.args[0], parseResult.args, in, out, err, this.getWorkingDir());
		list.add(p);
		return p;
	}

	@Override
	protected void run(String[] args) throws Exception {
		InputParser parser = new InputParser();

		//this loop reads input from terminal
		while (true) {
			Utilities.log("reading");
			String command = this.stdIn.readLine();
			if (command != null) {
				this.stdOut.writeLine(command);

				//special cases of input command
				if (command.equals(Shell.EXIT_COMMAND)) {
					return;
				} else if (command.equals(Shell.CWD_COMMAND)) {
					Utilities.log("changing directory");
				}

				try {
					//process command
					ParseResult result = parser.parse(command);
					Process p = this.createProcess(result);
					if (!result.isBackgroundTask) {
						Utilities.log("going to sleep");
						p.join();
					} else {
						this.stdOut.writeLine("[1] " + p.getPid());
					}
				} catch (ParseException e) {
					this.stdOut.writeLine("Invalid input: " + e.getMessage());
				} catch (NoSuchProcessException ex) {
					this.stdOut.writeLine("Invalid process name");
				}
			} else {
				this.stdOut.writeLine("");
			}
		}
	}
}
