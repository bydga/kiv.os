package cz.zcu.kiv.os.core.device;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * IODevice based on Queue implementation, storing Strings.
 *
 * Serves as thread-safe replacement for IODevice backed by PipedStreams.
 * Typical usage as STDIO or PIPE between processes.
 *
 * @author Jakub Danek
 */
public class IOQueueDevice extends AbstractIODevice {

    private Queue<String> buffer;

    public IOQueueDevice() {
        buffer = new LinkedList<String>();
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
                    try { //sleep if no messages to pass on
                        buffer.wait();
                    } catch (InterruptedException ex) {
                        //TODO handle exception
                        Logger.getLogger(IOQueueDevice.class.getName()).log(Level.SEVERE, null, ex);
                    }
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
