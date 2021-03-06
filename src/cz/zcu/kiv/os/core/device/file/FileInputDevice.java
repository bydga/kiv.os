package cz.zcu.kiv.os.core.device.file;

import cz.zcu.kiv.os.core.device.InputDevice;
import java.io.FileInputStream;

/**
 * InputDevice subclass designed for working with files.
 *
 * @author Jakub Danek
 */
public class FileInputDevice extends InputDevice {
    
    private final String path;

    public FileInputDevice(FileInputStream inputStream, String path) {
        super(inputStream, false);
        this.path = path;
    }

	/**
	 * 
	 * @return System (simulated) path to the file.
	 */
    public String getPath() {
        return path;
    }

}
