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

	private static final String helpText =
				"Usage: echopid [OPTIONS]...\n"+
				"Write PID of current echopid process instance.\n\n"+
				"  [-s, --sleep SLEEPTIME]   Sleep time in miliseconds\n"+
				"  [-c, --count COUNT]       Number of lines\n\n"+
				"If no option set, default values are:\n"+
				"sleep: 10, count: 1000";
	
	public static String getManualPage() {
		return helpText;
	}
	
	private final int defaultCount = 1000;
	private final int defaultSleep = 10;
	
	@Override
	protected void run(String[] args) throws Exception {
		int count = this.defaultCount;
		int sleepTime = this.defaultSleep;
				
		this.getOutputStream().writeLine("Echopid started");
		
		for (int i = 1; i < args.length; i++) {	
			String arg = args[i];

			if(arg.equals("-c") || arg.equals("--count")) {
				i++;
				if(i >= args.length) {
					this.getOutputStream().writeLine("echopid: count missing");
					return; // exit
				}
				try {
					count = Integer.parseInt(args[i]);
				} catch (NumberFormatException e) {
					this.getOutputStream().writeLine("echopid: invalid count");
					return; // exit
				}
			}
			else if(arg.equals("-s") || arg.equals("--sleep")) {
				i++;
				if(i >= args.length) {
					this.getOutputStream().writeLine("echopid: sleeptime missing");
					return; // exit
				}
				try {
					sleepTime = Integer.parseInt(args[i]);
				} catch (NumberFormatException e) {
					this.getOutputStream().writeLine("echopid: invalid sleeptime");
					return; // exit
				}
			}
			else if(arg.equals("--help")) {	
				this.getOutputStream().writeLine(this.helpText);
				return; //exit
			}
			else {
				StringBuilder bf = new StringBuilder();
					bf.append("echopid: invalid option ");
					bf.append(arg);
					bf.append("\n");
					bf.append("Try 'echopid --help' for more information.");
					this.getOutputStream().writeLine(bf.toString());
					return; //exit
			}
		}

		for (int i = 0; i < count; i++) {
			this.getOutputStream().writeLine(("PID " + (this.pid)) + ", iteration " + i);
			Thread.sleep(sleepTime);
		}
		this.getOutputStream().writeLine("Echopid finished");

	}
}
