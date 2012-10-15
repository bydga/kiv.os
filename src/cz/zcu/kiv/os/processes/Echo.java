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
	
	
	@Override
	public void run() {
		try {
			for (int i = 0; i < 100; i++) {
				this.stdOut.write(("PID "+(this.pid)+"\n").getBytes());
			}
		}
		catch(Exception e) {
			
		}
	}

	@Override
	public void initProcess(String[] args) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}