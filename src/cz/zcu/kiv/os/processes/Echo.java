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
	public void run(String[] args) {
		for (int i = 0; i < 10; i++) {
			try {
				this.stdOut.writeLine(("PID " + (this.pid)));
			} catch (Exception ex) {
				Logger.getLogger(Echo.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
}
