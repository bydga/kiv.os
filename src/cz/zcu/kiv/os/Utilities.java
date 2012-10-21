/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os;

import java.text.SimpleDateFormat;
import java.util.Date;

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
	
}
