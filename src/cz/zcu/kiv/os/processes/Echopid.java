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
				"\nUsage: echopid [OPTION]...\n"+
				"Write PID of current echopid process in loop.\n"+
				"OPTIONS:\n"+
				"  -s, --sleep SLEEPTIME    sleeptime in miliseconds after each write\n"+
				"  -c, --count COUNT        count of loops\n"+
				"      --help               display this help and exit\n"+
				"If no option set, default values are:\n"+
				"  SLEEPTIME: 10, COUNT: 1000\n";
	
	public static String getManualPage() {
		return helpText;
	}
	
	private final int defaultCount = 1000;
	private final int defaultSleep = 10;
	
	@Override
	protected void run(String[] args) throws Exception {
		int count = this.defaultCount;
		int sleepTime = this.defaultSleep;
		
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
				this.getOutputStream().writeLine(Echopid.getManualPage());
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
		
		this.getOutputStream().writeLine("Echopid started writing");

		for (int i = 0; i < count; i++) {
			this.getOutputStream().writeLine(("PID " + (this.pid)) + ", iteration " + i);
			Thread.sleep(sleepTime);
		}
		this.getOutputStream().writeLine("Echopid finished writing");

	}
}
