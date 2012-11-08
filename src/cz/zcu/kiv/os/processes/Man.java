package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.core.Process;
import java.lang.reflect.Method;

/**
 *
 * @author Jakub Danek
 */
public class Man extends Process {

	@Override
	protected void run(String[] args) throws Exception {
		if(args.length < 2) {
			this.getOutputStream().writeLine("Which manual you want to show?");
			return;
		}

		StringBuilder bf = new StringBuilder();
		for(int i = 1; i < args.length; i++) {
			bf.append(args[i]).append(":\n");
			bf.append(getManualEntry(args[i])).append("\n\n");
		}

		this.getOutputStream().writeLine(bf.toString());
	}

	private String getManualEntry(String processName) {
		Class clazz;
		String className;
		try {
			 className = Character.toUpperCase(processName.charAt(0)) + processName.substring(1).toLowerCase();
			clazz = Class.forName(Process.PROCESS_PACKAGE + "." + className);
			Method m = clazz.getMethod("getManualPage");
			return (String) m.invoke(null);
		} catch (Exception ex) {
			return "No record for " + processName + "!";
		}
	}

	public static String getManualPage() {
		return "Displays manual entry for the processes passed as arguments.";
	}

}
