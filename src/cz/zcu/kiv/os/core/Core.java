package cz.zcu.kiv.os.core;

import cz.zcu.kiv.os.core.device.IInputDevice;
import cz.zcu.kiv.os.core.device.IOutputDevice;
import cz.zcu.kiv.os.core.filesystem.FileManager;
import cz.zcu.kiv.os.core.filesystem.FileMode;
import cz.zcu.kiv.os.terminal.SwingTerminal;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author bydga
 */
public class Core {

	private static Core instance;
	private ICoreServices services;
	private ProcessManager processManager;
	private FileManager fileManager;
	private SwingTerminal terminal;

	public void setTerminal(SwingTerminal t) {
		this.terminal = t;
	}

	public static synchronized Core getInstance() {
		if (Core.instance == null) {
			Core.instance = new Core();
		}

		return Core.instance;
	}

	private Core() {
		this.services = new Core.CoreServices();
		this.processManager = new ProcessManager();
		//TODO path separator
		this.fileManager = new FileManager(System.getProperty("user.home") + "os");
	}

	public synchronized ICoreServices getServices() {
		return this.services;
	}

	private class CoreServices implements ICoreServices {

		@Override
		public Process createProcess(String processName, ProcessProperties properties) throws NoSuchProcessException {
			return Core.this.processManager.createProcess(processName, properties);
		}

		@Override
		public IInputDevice openFileForRead(Process caller, String path) throws IOException {
			IInputDevice file = (IInputDevice) fileManager.openFile(path, caller.getWorkingDir(), FileMode.READ);
			processManager.addStreamToProcess(caller.getPid(), file);
			return file;
		}

		@Override
		public IOutputDevice openFileForWrite(Process caller, String path, boolean append) throws IOException {
			FileMode mode = append ? FileMode.APPEND : FileMode.WRITE;
			IOutputDevice file = (IOutputDevice) fileManager.openFile(path, caller.getWorkingDir(), mode);
			processManager.addStreamToProcess(caller.getPid(), file);
			return file;
		}

		@Override
		public void createDirectory(String dirName) {
			return;
		}

		@Override
		public boolean directoryExists(String filename) {
			return false;
		}

		@Override
		public void setTerminalCommand(String command) {
			Core.this.terminal.setText(command);
		}
	}
}
