package cz.zcu.kiv.os.core.device;

import java.io.*;

/**
 * Device that can be both read from/written to.
 *
 * It's very unprobable that processes would use this type of reference on a device.
 * Usually they will need only IInputDevice or IOutputDevice reference.
 *
 * One exception could be e.g. a file open for both reading and writing.
 *
 * @author Jakub Danek
 */
public class InOutDevice extends AbstractDevice implements IInputDevice, IOutputDevice {

    private IInputDevice reader;
    private IOutputDevice writer;

    /**
     * Default constructor
     * @param inputStream open input stream that can be read from
     * @param outputStream  open output stream that can be written to
     */
    public InOutDevice(InputStream inputStream, OutputStream outputStream) {
        reader = new InputDevice(inputStream);
        writer = new OutputDevice(outputStream);
    }

    /**
     * Merge input and output devices into one IO device.
     * @param reader
     * @param writer
     */
    public InOutDevice(IInputDevice reader, IOutputDevice writer) {
        this.reader = reader;
        this.writer = writer;
    }

    @Override
    public String readLine() {
        return reader.readLine();
    }

    @Override
    public void writeLine(String input) {
        writer.writeLine(input);
    }

    @Override
    protected void closeAction() {
        writer.close();
        reader.close();
    }

}
