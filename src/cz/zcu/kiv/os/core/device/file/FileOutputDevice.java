package cz.zcu.kiv.os.core.device.file;

import cz.zcu.kiv.os.core.device.OutputDevice;
import java.io.FileOutputStream;

/**
 * OutputDevice subclass designed to be used with files.
 *
 * @author Jakub Danek
 */
public class FileOutputDevice extends OutputDevice {

    private final String path;


    public FileOutputDevice(FileOutputStream outputStream, String path) {
        super(outputStream, false);
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
