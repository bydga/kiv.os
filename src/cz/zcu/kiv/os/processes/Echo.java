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
	public void run(String[] args) {
		try {
			for (int i = 0; i < 100; i++) {
				this.stdOut.writeLine(("PID "+(this.pid)+"\n"));
			}
		}
		catch(Exception e) {
			
		}
	}

}
