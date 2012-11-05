/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.processes;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jirka
 */
public class Echopid extends cz.zcu.kiv.os.core.Process {

	@Override
	protected void run(String[] args) throws Exception {
		try {
			Thread.sleep(1000);
			this.stdOut.writeLine("start");
			for (int i = 0; i < 10; i++) {
				this.stdOut.writeLine(("PID " + (this.pid)));
				Thread.sleep(400);
			}
			this.stdOut.writeLine("finish");
		} catch (Exception ex) {
			Logger.getLogger(Echo.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
}
