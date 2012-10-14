/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.core;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;

/**
 *
 * @author bydga
 */
public class Core {

	protected static Core instance;
	protected ICoreServices services;
	protected ProcessManager processManager;

	public static synchronized Core getInstance() {
		if (Core.instance == null) {
			Core.instance = new Core();
		}

		return Core.instance;
	}

	protected Core() {
		this.services = new Core.CoreServices();
		this.processManager = new ProcessManager();
	}

	public ICoreServices getServices() {
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
		public Process createProcess(Process parent, String processName, String[] args, InputStream stdIn, OutputStream stdOut, OutputStream stdErr) throws Exception {
			Process p = Core.this.processManager.createProcess(parent, processName, stdIn, stdOut, stdErr);
			
			return p;
		}

		@Override
		public Process createProcess(Process parent, String processName, String[] args) throws Exception {
			return this.createProcess(parent, processName, args, null, null, null);
		}

		@Override
		public void closeFile(Process caller, PipedInputStream stream) {
			Core.this.processManager.removeStreamFromProcess(caller.getPid(), stream);
		}
	}
}
