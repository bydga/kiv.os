package cz.zcu.kiv.os.core.device;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Pipe is an IO device with buffer for storing data.
 *
 * @author Jakub Danek
 */
public class PipeDevice extends AbstractIODevice {

    private Queue<String> buffer;

    /**
     * Constructor which enables setting the device as standard stream (which is not
     * closed on regular process end)
     *
     * @param stdStream
     */
    public PipeDevice(boolean stdStream) {
        super(stdStream);
        buffer = new LinkedList<String>();
    }

    /**
     * Default constructor. Sets stdStream flag on false.
     */
    public PipeDevice() {
        this(false);
    }

    @Override
    protected void detachAction() {
        synchronized(buffer) {
            buffer.clear();
            buffer = null;
        }
    }

    @Override
    public String readLine() throws Exception {
        if(isOpen()) {
            synchronized(buffer) {
                while(buffer.isEmpty() && isOpen()) {
					buffer.wait();//sleep if no messages to pass on
                }
                if(!isOpen()) {
                    return null;
                }
                return buffer.poll();
            }
        } else {
            return null;
        }

    }

    @Override
    public void writeLine(String input) throws Exception {
        if(isOpen()) {

            synchronized(buffer) {
                buffer.add(input);
                buffer.notifyAll(); //wake-up potential readers
            }
            
        } else {
            //TODO better exception
            throw new Exception();
        }
    }

    @Override
    public void EOF() {
        synchronized(buffer) {
            buffer.add(null);
            buffer.notifyAll();
        }
    }

}
