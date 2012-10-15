package cz.zcu.kiv.os.core.device;

import java.io.*;

/**
 *
 * @author Jakub Danek
 */
public class InOutDevice extends AbstractDevice implements IInputDevice, IOutputDevice {

    private IInputDevice reader;
    private IOutputDevice writer;

    public InOutDevice(InputStream inputStream, OutputStream outputStream) {
        reader = new InputDevice(inputStream);
        writer = new OutputDevice(outputStream);
    }

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
