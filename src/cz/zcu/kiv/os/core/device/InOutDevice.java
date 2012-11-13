package cz.zcu.kiv.os.core.device;

import java.io.*;

/**
 * Device that can be both read from/written to.
 *
 * It's very unprobable that processes would use this type of reference on a device.
 * Usually they will need only IInputDevice or IOutputDevice reference.
 *
 * This object is NOT thread-safe!
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
    public InOutDevice(InputStream inputStream, OutputStream outputStream, boolean stdStream) {
        this(new InputDevice(inputStream, stdStream), new OutputDevice(outputStream, stdStream), stdStream);
    }

    /**
     * Merge input and output devices into one IO device.
     * @param reader
     * @param writer
     */
    public InOutDevice(IInputDevice reader, IOutputDevice writer, boolean stdStream) {
        super(stdStream);
        this.reader = reader;
        this.writer = writer;
    }

    @Override
    //TODO better exception
    public String readLine() throws Exception {
        return reader.readLine();
    }

    @Override
    //TODO better exception
    public void writeLine(String input) throws Exception {
        writer.writeLine(input);
    }
	
	 @Override
    //TODO better exception
    public void write(String input) throws Exception {
        writer.write(input);
    }

    @Override
    protected void detachAction() throws IOException {
        writer.detach();
        reader.detach();
    }

    @Override
    public void EOF() {
        writer.EOF();
    }

}
