/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.processes;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jiri Zikmund
 */
public class Echopid extends cz.zcu.kiv.os.core.Process {

	@Override
	protected void run(String[] args) throws Exception {
		try {
			Thread.sleep(1000);
			this.getOutputStream().writeLine("start");
			for (int i = 0; i < 1000000; i++) {
				this.getOutputStream().writeLine(("PID " + (this.pid)) + ", iteration " + i);
				Thread.sleep(20);
			}
			this.getOutputStream().writeLine("finish");
		} catch (Exception ex) {
			Logger.getLogger(Echo.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
}
