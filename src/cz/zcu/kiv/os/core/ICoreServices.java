package cz.zcu.kiv.os.core;

import cz.zcu.kiv.os.core.device.AbstractIODevice;
import cz.zcu.kiv.os.core.interrupts.KeyboardEvent;
import cz.zcu.kiv.os.core.interrupts.Signals;
import cz.zcu.kiv.os.core.device.IInputDevice;
import cz.zcu.kiv.os.core.device.IOutputDevice;
import java.io.File;
import java.io.IOException;
import java.util.List;

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
	 *
	 * @param caller process calling the service
	 * @param path path to the file
	 * @param append append to existing file (if false, overwrites existing file or creates a new one)
	 * @return writable device backed by the file on the given path
	 */
	public IOutputDevice openFileForWrite(Process caller, String path, boolean append) throws IOException;

	/**
	 * System service that creates new runnable process. The process is already started.
	 * @param processName The name of desired process to create.
	 * @param properties Object describing desired attributes of the new process.
	 * @return Instance of newly created process.
	 * @throws NoSuchProcessException When process with the specified process name doesn't exist.
	 */
	public Process createProcess(String processName, ProcessProperties properties) throws NoSuchProcessException;

	/**
	 * Creates new directory specified by the dirname. If the dirname is a relative path, 
	 * it's relative to the caller's working directory.
	 * @param caller Process, that calls this service.
	 * @param dirName Requested name of the directory.
	 * @return true when the directory was created sucessfully. False when the directory already exists.
	 */
	public boolean createDirectory(Process caller, String dirName);

	/**
	 * Checks for existance of a desired directory.
	 * @param caller Process, that calls this service.
	 * @param filename Path to a directory to check for existance.
	 * @return True when the specified directory exists, false otherwise.
	 */
	public boolean directoryExists(Process caller, String filename);

	/**
	 * Service that lists all files and folders in the specified path.
	 * @param caller Process that calls this service.
	 * @param dir Directory name that should be listed. If its a relative path, its relative to the process working directory.
	 * @return Collection of all files and folders on the specified path.
	 */
	public List<File> listFiles(Process caller, String dir);

	/**
	 * Sets specified string to a last line of terminal. 
	 * @param command String to be appended to the terminal window.
	 */
	public void setTerminalCommand(String command);

	/**
	 * Returns last command written to the terminal.
	 * @return 
	 */
	public String getTerminalCommand();

	/**
	 * Sends specified signal to a current foreground process.
	 * @param sig Signal to be dispatched.
	 */
	public void dispatchSystemSignal(Signals sig);

	/**
	 * Sends specified signal to a process specified by the pid.
	 * @param pid Process pid that should receive the signal.
	 * @param sig Signal to be dispatched.
	 */
	public void dispatchSystemSignal(int pid, Signals sig);

	/**
	 * Sends specified keyboard event (curently supported are KEY_UP and KEY_DOWN) to a current foreground process.
	 * @param evt Event to be dispatched.
	 */
	public void dispatchKeyboardEvent(KeyboardEvent evt);

	/**
	 * Returns new piped device. Usable for connecting two processes in the pipeline.
	 * @return 
	 */
	public AbstractIODevice createPipe();

	/**
	 * Queries the processmanager and fetches information about all running processess.
	 * @return Collection of ProcessInfo objects, each containing info about one running process.
	 */
	public List<ProcessInfo> getProcessTableData();

	/**
	 * Blocking system call, that waits for a process to finish and returns its exitcode.
	 * @param p Process to wait for.
	 * @return Exitcode of the finished process.
	 * @throws InterruptedException This exception is thrown when the caller is interrupted wile waiting for the process exit code.
	 */
	public int readProcessExitCode(Process p) throws InterruptedException;

	/**
	 * Initiates the shutdown procedure. Kills all user processes, closes standard streams and exits the system.
	 * @param caller Process that calls this service.
	 */
	public void shutdown(Process caller);

	/**
	 * Returns list of all currently running processes.
	 * @return 
	 */
	public List<Process> getAllProcessess();

	/**
	 * Checks if the OS is running.
	 * @return 
	 */
	public boolean isRunning();
}
