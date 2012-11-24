package cz.zcu.kiv.os.processes;

/**
 * Process that suspeds execution of current process
 * 
 * @author bydga
 */
public class Sleep extends cz.zcu.kiv.os.core.Process {

	private static final String helpText =
			"Usage: sleep [OPTION] TIME\n"+
			"Suspends the current process execution for specified ammount of seconds.\n"+
			"OPTION:\n"+
			"      --help               display this help and exit\n";

	/**
	 * Returns manual page for Sleep process
	 * 
	 * @return string with manual page
	 */
	public static String getManualPage() {
		return helpText;
	}
	
	@Override
	protected void run(String[] args) throws Exception {
		if (args.length == 2) {
			if (args[1].compareTo("--help") == 0) {
				this.getOutputStream().writeLine(Sleep.getManualPage());
			} else {
				try {
					int time = 1000 * Integer.parseInt(args[1]);
					Thread.sleep(time);
				} catch (NumberFormatException ex) {
					this.getOutputStream().writeLine(Sleep.getManualPage());
				}
			}
		} else {
			this.getOutputStream().writeLine(Sleep.getManualPage());
		}
	}
}
