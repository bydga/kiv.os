package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.core.Process;

/**
 *
 * @author Jiri Zikmund
 */
public class Echo extends Process {

	@Override
	public void run(String[] args) {
		for (int i = 0; i < 10; i++) {
			this.stdOut.writeLine(("PID " + (this.pid) ));
		}

	}
}
