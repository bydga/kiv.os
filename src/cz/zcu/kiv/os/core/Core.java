/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.core;

import cz.zcu.kiv.os.terminal.InputParser;
import cz.zcu.kiv.os.terminal.ParseResult;
import java.io.*;

/**
 *
 * @author bydga
 */
public class Core {

	protected static Core instance;
	protected ICoreServices services;
	protected ProcessManager processManager;
	protected InputParser inputParser;

	public static synchronized Core getInstance() {
		if (Core.instance == null) {
			Core.instance = new Core();
		}

		return Core.instance;
	}

	protected Core() {
		this.services = new Core.CoreServices();
		this.processManager = new ProcessManager();
		this.inputParser = new InputParser();
	}

	public synchronized ICoreServices getServices() {
		return this.services;
	}

	protected class CoreServices implements ICoreServices {

		@Override
		public PipedInputStream openFile(Process caller, String fileName, String rights) {

			int pid = 0;
			PipedInputStream is = null;
			Core.this.processManager.addStreamToProcess(pid, is);
			return is;
		}

		@Override
		public Process createProcess(Process parent, String processName, String[] args, String stdIn, String stdOut, String stdErr) throws Exception {
			InputStream in;
			OutputStream out, err;
			if (stdIn == null) {
				in = new PipedInputStream();
			} else {
				in = new FileInputStream(stdIn);
			}

			if (stdOut == null) {
				out = new PipedOutputStream();
			} else {
				out = new FileOutputStream(stdOut);
			}

			if (stdErr == null) {
				err = new PipedOutputStream();
			} else {
				err = new FileOutputStream(stdErr);
			}

			Process p = Core.this.processManager.createProcess(parent, processName, args, in, out, err);

			return p;
		}

		@Override
		public void closeFile(Process caller, PipedInputStream stream) {
			Core.this.processManager.removeStreamFromProcess(caller.getPid(), stream);
		}

		@Override
		public Process createProcess(Process caller, ParseResult parseResult) throws Exception {

			Process parent = parseResult.pipeline == null ? caller : this.createProcess(caller, parseResult.pipeline);
			Process result = this.createProcess(parent, parseResult.args[0], parseResult.args, parseResult.stdIn, parseResult.stdOut, parseResult.stdErr);
			//TODO: connect streams, init in/out/err...

			return result;
		}
	}
}
