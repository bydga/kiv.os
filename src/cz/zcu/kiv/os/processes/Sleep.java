/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.processes;

/**
 *
 * @author bydga
 */
public class Sleep extends cz.zcu.kiv.os.core.Process {

	@Override
	protected void run(String[] args) throws Exception {
		if (args.length == 2) {
			if (args[1].compareTo("--help") == 0) {
				this.getOutputStream().writeLine(Sleep.helpText);
			} else {
				try {
					int time = 1000 * Integer.parseInt(args[1]);
					Thread.sleep(time);
				} catch (NumberFormatException ex) {
					this.getOutputStream().writeLine(Sleep.helpText);
				}
			}
		} else {
			this.getOutputStream().writeLine(Sleep.helpText);
		}
	}
	private static final String helpText =
			"Usage: sleep TIME\n"
			+ " Suspends the current process execution for specified ammount of seconds."
			+ "      --help               display this help and exit";

	public static String getManualPage() {
		return helpText;
	}
}
