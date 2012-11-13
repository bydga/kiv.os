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
		int length = args.length > 1 ? Integer.parseInt(args[1]) : 1000;
		this.getOutputStream().writeLine("start");
		for (int i = 0; i < length; i++) {
			this.getOutputStream().writeLine(("PID " + (this.pid)) + ", iteration " + i);
			Thread.sleep(10);
		}
		this.getOutputStream().writeLine("finish");

	}
}
