package cz.zcu.kiv.os.core;

import cz.zcu.kiv.os.core.device.IInputDevice;
import cz.zcu.kiv.os.core.device.IOutputDevice;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author bydga
 */
public interface ICoreServices {

        /**
         * System service which opens the file on the given path as read only.
         *
         * @param caller process calling the service
         * @param path path to the file
         * @return readable device backed by the file on the given path
         */
	public IInputDevice openFileForRead(Process caller, String path) throws IOException;
        /**
         * System service which opens the file on the given path for writing.
         * @param caller process calling the service
         * @param path path to the file
         * @param append append to existing file (if false, overwrites existing file or creates a new one)
         * @return writable device backed by the file on the given path
         */
        public IOutputDevice openFileForWrite(Process caller, String path, boolean append) throws IOException;
	public Process createProcess(Process parent, String processName, String[] args, IInputDevice stdIn, IOutputDevice stdOut, IOutputDevice stdErr, String workingDir) throws NoSuchProcessException;
}
