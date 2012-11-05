package cz.zcu.kiv.os.core.filesystem;

import cz.zcu.kiv.os.core.device.*;
import cz.zcu.kiv.os.core.device.file.FileInputDevice;
import cz.zcu.kiv.os.core.device.file.FileOutputDevice;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.regex.Matcher;

/**
 * Manager class for resolving path and opening files.
 *
 * @author Jakub Danek
 */
public class FileManager {

    public static final String SEPARATOR = System.getProperty("file.separator");
    
    private final String rootPath;

    /**
     * Default constructor
     * @param rootPath real path (in the host system) to the simulated system's root dir
     */
    public FileManager(String rootPath) {
        this.rootPath = rootPath;
    }

    /**
     * Returns device (for read or write) backed by the file on the given path
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

    public boolean createDirectory(String path, String workingDir) {
        String realPath = resolveRealPath(path, workingDir);

        return mkDir(realPath);
    }

    public boolean directoryExists(String path, String workingDir) {
        String realPath = resolveRealPath(path, workingDir);

        return isDir(realPath);
    }

    private boolean isDir(String realPath) {
        File dir = new File(realPath);
        return dir.isDirectory();
    }

    private boolean mkDir(String realPath) {
        File dir = new File(realPath);
        return dir.mkdirs();
    }

    /**
     * Returns the real path (in the host system) to the file on the simulated system's
     * path (which is given as an argument).
     * @param path path to the file in the simulated system
     * @param workingDir directory to which the path is relative to (if related)
     * @return real path to the file in the host system
     */
    public String resolveRealPath(String path, String workingDir) {
        StringBuilder realPath = new StringBuilder(getRootPath());

        String innerPath;
        switch(path.charAt(0)) {
            case '/': //absolute path
                innerPath = resolveRelativePath("/", path);
                break;
            default: //relative path
                innerPath = resolveRelativePath(workingDir, path);
                break;
        }

        innerPath = substituteFileSeparators(innerPath);

        //replace root separator by root path
        realPath.append(innerPath.substring(SEPARATOR.length()));

        return realPath.toString();
    }

    private String substituteFileSeparators(String path) {
        return path.replaceAll("/", Matcher.quoteReplacement(SEPARATOR));
    }

    /**
     * Handles all void directory elements and directory jumps within the path.
     * @param workingDir directory to which the path is relative to
     * @param path the path
     * @return minimal path to the file
     */
    public String resolveRelativePath(String workingDir, String path) {
        StringBuffer result = new StringBuffer(workingDir);
        StringBuilder tmp = new StringBuilder();
        char character;

        for(int i = 0; i < path.length(); i++) {
            character = path.charAt(i);
            switch(character) {
                case '/': //dirname end
                    if(tmp.length() > 0) {//any dirname, otherwise just skip
                        resolveFileName(result, tmp.toString(), true);
                        tmp.delete(0, tmp.length()); //clean temp buffer
                    }
                    break;
                default:
                    tmp.append(character);
                    break;
            }
        }

        if(tmp.length() > 0) {//filename
            resolveFileName(result, tmp.toString(), false);
        }

        return result.toString();
    }

    /**
     * Handles special directory names (level up "..", same direcotry "." etc.)
     * @param result buffer to which the changes are saved
     * @param dirname dirname to be processed
     */
    private void resolveFileName(StringBuffer result, String dirname, boolean apendDirEnd) {
        if(dirname.equals(".")) {
            //do nothing, same directory
        } else if (dirname.equals("..")) {
            levelUpDir(result);
        } else {
            result.append(dirname);
            if(apendDirEnd) {
                result.append("/");
            }
        }
    }

    /**
     * Moves one directory up the path, unless its root already
     * @param path
     */
    private void levelUpDir(StringBuffer path) {
        if(path.length() == 1) { //root only
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
     * @param path path in the simulated system
     * @param mode open mode
     * @return device
     * @throws FileNotFoundException
     */
    private AbstractDevice prepareDevice(String path, FileMode mode) throws FileNotFoundException {
        AbstractDevice dev;
        switch(mode) {
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
