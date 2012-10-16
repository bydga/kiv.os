/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.core;

import cz.zcu.kiv.os.core.device.IInputDevice;
import cz.zcu.kiv.os.core.device.IOutputDevice;
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

			int pid = caller.getPid();
			PipedInputStream is = null;
			Core.this.processManager.addStreamToProcess(pid, is);
			return is;
		}

		@Override
		public Process createProcess(Process parent, String processName, String[] args, IInputDevice stdIn, IOutputDevice stdOut, IOutputDevice stdErr) throws Exception {
			return Core.this.processManager.createProcess(parent, processName, args, stdIn, stdOut, stdOut);
		}

		@Override
		public void closeFile(Process caller, PipedInputStream stream) {
			Core.this.processManager.removeStreamFromProcess(caller.getPid(), stream);
		}
	}
}
