/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.Utilities;
import cz.zcu.kiv.os.core.Core;
import cz.zcu.kiv.os.core.NoSuchProcessException;
import cz.zcu.kiv.os.core.Process;
import cz.zcu.kiv.os.core.ProcessGroup;
import cz.zcu.kiv.os.core.ProcessProperties;
import cz.zcu.kiv.os.core.device.*;
import cz.zcu.kiv.os.core.interrupts.KeyboardEvent;
import cz.zcu.kiv.os.terminal.InputParser;
import cz.zcu.kiv.os.terminal.ParseException;
import cz.zcu.kiv.os.terminal.ParseResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bydga
 */
public class Shell extends Process {

	private static final String EXIT_COMMAND = "exit";
	private static final String CWD_COMMAND = "cd";
	private List<String> history;
	private int historyIndex = 0;
	private String curentCommand;

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
				return Core.getInstance().getServices().openFileForWrite(this, path, append);
			} catch (IOException ex) {
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
				return Core.getInstance().getServices().openFileForRead(this, path);
			} catch (IOException ex) {
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

		ProcessGroup group = new ProcessGroup(new ThreadGroup("group_" + parseResult.args[0]));
		//only for debugging reasons
		List<Process> list = new ArrayList<Process>();
		IInputDevice in = this.createInput(parseResult.stdIn, this.getInputStream());
		IOutputDevice out;
		IOutputDevice err;


		//there is another process waiting in the pipeline for ouput of this process
		while (parseResult.pipeline != null) {
			//create the pipe
			AbstractIODevice pipe = Core.getInstance().getServices().createPipe();
			out = this.createOutput(parseResult.stdOut, parseResult.stdOutAppend, pipe);
			err = this.createOutput(parseResult.stdErr, parseResult.stdErrAppend, this.getErrorStream());
			in = this.createInput(parseResult.stdIn, in);

			ProcessProperties properties = new ProcessProperties(this, parseResult.args, in, out, err, this.getWorkingDir(), group, parseResult.isBackgroundTask);
			Process p = Core.getInstance().getServices().createProcess(parseResult.args[0], properties);
			list.add(p);
			//pipe was used as an output device here, remember it for next iteration, where it will stand as an input
			in = pipe;

			parseResult = parseResult.pipeline;
		}

		//handle last process of the pipeline - or in case of no pipeline the only executed process
		in = this.createInput(parseResult.stdIn, in);
		out = this.createOutput(parseResult.stdOut, parseResult.stdOutAppend, this.getOutputStream());
		err = this.createOutput(parseResult.stdErr, parseResult.stdErrAppend, this.getErrorStream());

		ProcessProperties properties = new ProcessProperties(this, parseResult.args, in, out, err, this.getWorkingDir(), group, parseResult.isBackgroundTask);
		Process p = Core.getInstance().getServices().createProcess(parseResult.args[0], properties);

		list.add(p);
		return p;
	}

	@Override
	protected void run(String[] args) throws Exception {
		InputParser parser = new InputParser();
		this.history = new ArrayList<String>();

		//this loop reads input from terminal
		while (true) {
			Utilities.log("reading, cwd: " + this.getWorkingDir());
			String command = this.getInputStream().readLine();
			if (command != null && !command.isEmpty()) {
				this.getOutputStream().writeLine(command);

				this.history.add(command);
				this.historyIndex++;

				try {
					//process command
					ParseResult result = parser.parse(command);

					//special cases of input command
					if (result.args[0].equals(Shell.EXIT_COMMAND)) {
						return; //return from the main loop - ends the process
					} else if (result.args[0].equals(Shell.CWD_COMMAND)) {
						if (result.args.length > 1) {
							this.setWorkingDir(result.args[1]);
						}
						
						continue;
					} else if (result.args[0].equals("")) {
						continue;
					}

					Process p = this.createProcess(result);
					if (!result.isBackgroundTask) {
						p.join();
					} else {
						this.getOutputStream().writeLine("[1] " + p.getPid());
					}
				} catch (ParseException e) {
					this.getOutputStream().writeLine("Invalid input: " + e.getMessage());
				} catch (NoSuchProcessException ex) {
					this.getOutputStream().writeLine("Invalid process name");
				}
			} else {
				this.getOutputStream().writeLine("");
			}
		}
	}

	@Override
	protected void handleSignalSIGTERM() {
		try {
			this.getOutputStream().writeLine("");
		} catch (Exception ex) {
			Logger.getLogger(Shell.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	@Override
	protected void handleKeyboardEvent(KeyboardEvent e) {

		if (e == KeyboardEvent.ARROW_DOWN) {
			if (this.historyIndex < this.history.size() - 1) {
				this.historyIndex++;
				Core.getInstance().getServices().setTerminalCommand(this.history.get(this.historyIndex));
			} else {
				Core.getInstance().getServices().setTerminalCommand(this.curentCommand);

			}
		} else if (e == KeyboardEvent.ARROW_UP) {

			if (this.historyIndex == this.history.size()) {
				this.curentCommand = Core.getInstance().getServices().getTerminalCommand();
			}

			if (this.historyIndex > 0) {
				this.historyIndex--;
				Core.getInstance().getServices().setTerminalCommand(this.history.get(this.historyIndex));

			}
		}
	}
}
