package cz.zcu.kiv.os.core.filesystem;

import cz.zcu.kiv.os.core.device.*;
import cz.zcu.kiv.os.core.device.file.FileInputDevice;
import cz.zcu.kiv.os.core.device.file.FileOutputDevice;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Manager class for resolving path and opening files.
 *
 * @author Jakub Danek
 */
public class FileManager {

	public static final String SEPARATOR = System.getProperty("file.separator");
	public static String[] ILLEGAL_FILE_CHARS = new String[]{"\\", "*", "?", ":", "\"", "<", ">", "|"};
	private final String rootPath;

	/**
	 * Default constructor
	 *
	 * @param rootPath real path (in the host system) to the simulated system's root dir
	 */
	public FileManager(String rootPath) {
		this.rootPath = rootPath;
	}

	/**
	 * Returns device (for read or write) backed by the file on the given path
	 *
	 * @param path path to the file
	 * @param workingDir working dir the path is relative to (if the path is relative)
	 * @param mode mode in which the file shall be opened
	 * @return created device
	 * @throws FileNotFoundException if file doesnt exist and couldnt be created
	 */
	public AbstractDevice openFile(String path, String workingDir, FileMode mode) throws FileNotFoundException {
		String realPath = resolveRealPath(path, workingDir);

		return prepareDevice(realPath, mode);
	}

	/**
	 * Creates directory/directory branch corresponding to the given path
	 *
	 * @param path dirpath
	 * @param workingDir working dir the path is relative to
	 * @return
	 */
	public boolean createDirectory(String path, String workingDir) {
		String realPath = resolveRealPath(path, workingDir);

		return mkDir(realPath);
	}

	/**
	 * Checks whether directory on the given path exists
	 *
	 * @param path dirpath
	 * @param workingDir working dir the path is relative to
	 * @return
	 */
	public boolean directoryExists(String path, String workingDir) {
		String realPath = resolveRealPath(path, workingDir);

		return isDir(realPath);
	}

	/**
	 * Lists all files on the given path
	 *
	 * @param path dirpath
	 * @param workingDir working dir the path is relative to
	 * @return list of files in the dir given, or filename if path is file, or null otherwise
	 */
	public List<File> listFiles(String path, String workingDir) {
		String realPath = resolveRealPath(path, workingDir);

		File dir = new File(realPath);
		List<File> l = new ArrayList<File>();
		if (dir.isDirectory()) {//return dir filelist
			l.add(new File(dir, "."));
			l.add(new File(dir, ".."));
			l.addAll(Arrays.asList(dir.listFiles()));
			return l;
		} else if (dir.isFile()) {//return filename
			l.add(dir);
			return l;
		} else {//not a dir, not a file, invalid path
			return null;
		}
	}

	private boolean isDir(String realPath) {
		File dir = new File(realPath);
		return dir.isDirectory();
	}

	/**
	 * Create dir branch.
	 *
	 * @param realPath
	 * @return
	 */
	private boolean mkDir(String realPath) {
		File dir = new File(realPath);
		return dir.mkdirs();
	}

	/**
	 * Returns the real path (in the host system) to the file on the simulated system's path (which is given as an
	 * argument).
	 *
	 * @param pathArg path to the file in the simulated system
	 * @param workingDir directory to which the path is relative to (if related)
	 * @return real path to the file in the host system
	 */
	public String resolveRealPath(String pathArg, String workingDir) {
		StringBuilder realPath = new StringBuilder(getRootPath());
		String path = removeProhibitedChars(pathArg);

		String innerPath;
		switch (path.charAt(0)) {
			case '/': //absolute path
				innerPath = resolveRelativePath("/", path);
				break;
			default: //relative path
				innerPath = resolveRelativePath(workingDir, path);
				break;
		}

		innerPath = innerPath.substring(1);
		innerPath = substituteFileSeparators(innerPath);

		//replace root separator by root path
		realPath.append(innerPath);

		return realPath.toString();
	}

	/**
	 * Removes characters from the path, which would cause problems on Windows
	 * systems.
	 * @param path path to be cleared of prohibited characters
	 * @return cleared path
	 */
	public static String removeProhibitedChars(final String path) {
		String ret = path;
		for(String c : ILLEGAL_FILE_CHARS) {
			ret = ret.replace(c, "");
		}
		return ret;
	}

	private String substituteFileSeparators(String path) {
		return path.replaceAll("/", Matcher.quoteReplacement(SEPARATOR));
	}

	/**
	 * Handles all void directory elements and directory jumps within the path.
	 *
	 * @param workingDir directory to which the path is relative to
	 * @param path the path
	 * @return minimal path to the file
	 */
	public static String resolveRelativePath(String workingDir, String path) {
		StringBuffer result = new StringBuffer(workingDir);

		if (!workingDir.endsWith("/")) {
			result.append("/"); //end working dir with dir separator
		}

		StringBuilder tmp = new StringBuilder();
		char character;

		for (int i = 0; i < path.length(); i++) {
			character = path.charAt(i);
			switch (character) {
				case '/': //dirname end
					if (tmp.length() > 0) {//any dirname, otherwise just skip
						resolveFileName(result, tmp.toString(), true);
						tmp.delete(0, tmp.length()); //clean temp buffer
					}
					break;
				default:
					tmp.append(character);
					break;
			}
		}

		if (tmp.length() > 0) {//filename
			resolveFileName(result, tmp.toString(), false);
		}

		return result.toString();
	}

	/**
	 * Handles special directory names (level up "..", same direcotry "." etc.)
	 *
	 * @param result buffer to which the changes are saved
	 * @param dirname dirname to be processed
	 */
	private static void resolveFileName(StringBuffer result, String dirname, boolean apendDirEnd) {
		if (dirname.equals(".")) {
			//do nothing, same directory
		} else if (dirname.equals("..")) {
			levelUpDir(result);
		} else {
			result.append(dirname);
			if (apendDirEnd) {
				result.append("/");
			}
		}
	}

	/**
	 * Moves one directory up the path, unless its root already
	 *
	 * @param path
	 */
	private static void levelUpDir(StringBuffer path) {
		if (path.length() == 1) { //root only
			return;
		}
		int index = path.substring(0, path.length() - 1).lastIndexOf("/"); //last "not-final" directory separator
		path.delete(index + 1, path.length()); //delete last directory from the path
	}

	private String getRootPath() {
		return rootPath;
	}

	/**
	 * Opens file on the given path for reading
	 *
	 * @param path real path in the host system
	 * @return
	 * @throws FileNotFoundException
	 */
	private FileInputStream openFileForRead(String path) throws FileNotFoundException {
		File file = new File(path);
		//TODO set read only?
		return new FileInputStream(file);
	}

	/**
	 * Opens file on the given path for writing
	 *
	 * @param path real path in the host system
	 * @param append append?
	 * @return
	 * @throws FileNotFoundException
	 */
	private FileOutputStream openFileForWrite(String path, boolean append) throws FileNotFoundException {
		File file = new File(path);
		//TODO set writable?
		return new FileOutputStream(file, append);
	}

	/**
	 * Convenience class for preparing device for reading/writing from/to the file on the given path.
	 *
	 * @param path path in the simulated system
	 * @param mode open mode
	 * @return device
	 * @throws FileNotFoundException
	 */
	private AbstractDevice prepareDevice(String path, FileMode mode) throws FileNotFoundException {
		AbstractDevice dev;
		switch (mode) {
			case READ:
				dev = new FileInputDevice(openFileForRead(path), path);
				break;
			case WRITE:
				dev = new FileOutputDevice(openFileForWrite(path, false), path);
				break;
			case APPEND:
				dev = new FileOutputDevice(openFileForWrite(path, true), path);
				break;
			default:
				//TODO better exception
				throw new RuntimeException();
		}
		return dev;
	}
}
