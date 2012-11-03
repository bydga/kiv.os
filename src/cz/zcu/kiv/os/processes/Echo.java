package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.Utilities;
import cz.zcu.kiv.os.core.Process;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jiri Zikmund
 */
public class Echo extends Process {
	
	@Override
	public void run(String[] args) throws Exception {
		this.getOutputStream().writeLine("start");
		for (int i = 0; i < 1000000; i++) {
			this.getOutputStream().writeLine(("PID " + (this.pid) + " " + i));
		}
	}
}
