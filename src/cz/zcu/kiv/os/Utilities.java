/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os;

import java.text.SimpleDateFormat;
import java.util.Date;
import cz.zcu.kiv.os.core.ProcessArgs;
import cz.zcu.kiv.os.core.device.IOutputDevice;
import cz.zcu.kiv.os.core.ProcessOption;

/**
 *
 * @author bydga
 */
public class Utilities {
	
	
	public static void log(String text)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(sdf.format(new Date()) + ": " + text);
	}
	
	public static void echoArgs(ProcessArgs processArgs, IOutputDevice output) throws Exception{

		String[] optionNames = processArgs.getAllOptionNames();
		String[] names = processArgs.getAllNames();
		
		output.writeLine("----NAMES------");
		for (int i = 0; i < names.length; i++) {
			output.writeLine(names[i]);
		}
		output.writeLine("---------------");
		
		output.writeLine("----OPTIONS----");
		for (int i = 0; i < optionNames.length; i++) {
			
			ProcessOption option = processArgs.getOption(optionNames[i]);
			String[] optionArgs = option.getOptionArgs();
			
			String argString = "";
			for (int j = 0; j < optionArgs.length; j++) {
				argString += optionArgs[j] + ", ";
			}
			
			if(option.isDefined() == false) {
				argString += " (UNDEFINED) ";
			}
			
			if(option.isArgMissing()) {
				argString += " (missing arg "+ option.getMissingArgPos() +") ";
			}
			
			output.writeLine(option.getOptionName() + ": " + argString);
			
		}
		output.writeLine("---------------");
	}	
	
	
}
