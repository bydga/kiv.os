package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.core.Process;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author Jiri Zikmund
 */
public class Echo extends Process {
	
	public Echo(int pid, int ppid, String[] args, InputStream stdIn, OutputStream stdOut, OutputStream stdErr, Observer observer){
		super(pid, ppid, args, stdIn, stdOut, stdErr, observer);
	}
	
	@Override
	public void run() {
		try {
			for (int i = 0; i < 100; i++) {
				this.stdOut.write(("PID "+(this.pid)+"\n").getBytes());
				this.workingThread.sleep(this.ppid);
			}
		}
		catch(Exception e) {
			
		}
	}

}
