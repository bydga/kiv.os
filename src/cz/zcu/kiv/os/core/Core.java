package cz.zcu.kiv.os.core;

import cz.zcu.kiv.os.core.device.IInputDevice;
import cz.zcu.kiv.os.core.device.IOutputDevice;
import cz.zcu.kiv.os.core.filesystem.FileManager;
import cz.zcu.kiv.os.core.filesystem.FileMode;

/**
 *
 * @author bydga
 */
public class Core {

	private static Core instance;
	private ICoreServices services;
	private ProcessManager processManager;
        private FileManager fileManager;

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
            public Process createProcess(Process parent, String processName, String[] args, IInputDevice stdIn, IOutputDevice stdOut, IOutputDevice stdErr) throws NoSuchProcessException {
                    return Core.this.processManager.createProcess(parent, processName, args, stdIn, stdOut, stdOut);
            }

            @Override
            public IInputDevice openFileForRead(Process caller, String path) {
                IInputDevice file = fileManager.openFile(path, caller.getWorkingDir(), FileMode.READ);
                processManager.addStreamToProcess(caller.getPid(), file);
                return file;
            }

            @Override
            public IOutputDevice openFileForWrite(Process caller, String path, boolean append) {
                FileMode mode = append ? FileMode.APPEND : FileMode.WRITE;
                IOutputDevice file = fileManager.openFile(path, caller.getWorkingDir(), mode);
                processManager.addStreamToProcess(caller.getPid(), file);
                return file;
            }

	}
}
