package cz.zcu.kiv.os.core.device;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
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
        buffer = new ConcurrentLinkedQueue<String>();
    }

    @Override
    protected void closeAction() {
        synchronized(buffer) {
            buffer.clear();
            buffer = null;
        }
    }

    @Override
    public String readLine() throws Exception {
        if(isOpen()) {
            synchronized(buffer) {
                while(buffer.isEmpty()) {
                    try { //sleep if no messages to pass on
                        buffer.wait();
                    } catch (InterruptedException ex) {
                        //TODO handle exception
                        Logger.getLogger(IOQueueDevice.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                return buffer.poll();
            }
        } else {
            //TODO better exception
            throw new Exception();
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



}
