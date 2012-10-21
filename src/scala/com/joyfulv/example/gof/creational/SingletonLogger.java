/*******************************************
 * Name     : SingletonLogger
 * Function : Example class to show the use of singleton
 * Author   : Victor@JoyfulVillage
 * Date     : August 27, 2012 
 * 
 * modification:
 * Date  :
 * Author:
 * Change:
 * Ref   :
 * 
 * 
 */

package com.joyfulv.example.gof.creational;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Demonstration of singleton object
 */
public class SingletonLogger implements Serializable {
	
	/**
	 * auto-generated UID
	 */
	private static final long serialVersionUID = 1016424525695103585L;
	
	/**
	 * This Enum declaration should be extracted to an isolated file
	 * so that the LogLevel should be extended without touching
	 * the logger code
	 */
	public enum LogLevel {
		DEBUG, INFO, WARNING, ERROR
	}
	
	final String fileDate = "yyyyMMdd_HHmmss";
	final String logDate  = "yyyyMMdd_HH:mm:ss.SSSSSS Z";
	
	private static SingletonLogger logger;
	private String           processName;
	private FileOutputStream fileout;
	private PrintStream      printstream;
	
	private String           logDir = System.getenv("LOG_DIR");
	private String           defaultLogLevel = 
								System.getProperty("com.joyfulv.LOG_LEVEL");
	private SimpleDateFormat sdf = new SimpleDateFormat(fileDate);
	
	
	/**
	 * private constructor
	 * @param processName
	 */
	private SingletonLogger(String processName) {
		this.processName = processName;
		init();
	}
	
	/**
	 * Initialize settings
	 */
	private void init(){
		String fileName = logDir + "/" + processName + 
				          "_" + sdf.format(new Date());
		try {
			fileout     = new FileOutputStream(fileName);
			printstream = new PrintStream(fileout);
		} catch (IOException ioe){
			System.err.println(ioe.getMessage());
			ioe.printStackTrace();
		}
		sdf.applyPattern(logDate);
	}
	
	/**
	 * called immediately after the object is de-serialized
	 * @return the singleton logger object
	 */
	protected Object readResolve(){
		return getLogger(processName);
	}
	
	/**
	 * public getInterface
	 * implemented lazy instantiation and double locking mechanism
	 * @param processName
	 * @return singleton logger object
	 */
	public static SingletonLogger getLogger(String processName) {
		if ( logger == null ){
			synchronized(SingletonLogger.class){
				if (logger == null){
					logger = new SingletonLogger(processName);
				}
			}
		}
		return logger;
	}
	
	/**
	 * public logging interface
	 * @param logLevel
	 * @param className
	 * @param fnName
	 * @param msg
	 */
	public void log(LogLevel logLevel, String className, 
			        String fnName, String msg){
		
		if (defaultLogLevel!= null) {
			if (defaultLogLevel.contains(logLevel.toString())){
				
				String logMsg = logLevel.toString() + " : ";
				logMsg += "\""+sdf.format(new Date()) + "\" ";
				logMsg += "["+className+":"+fnName+"]\n";
				logMsg += msg + "\n";
				printstream.println(logMsg);
			}
		}
	}
	
	/**
	 * Add new LogLevel
	 * @param level to add
	 * @return new logging level
	 */
	public synchronized String addLogLevel(LogLevel level){
		if (!defaultLogLevel.contains(level.toString())){
			defaultLogLevel += " "+level.toString();
		}
		return defaultLogLevel;
	}
	
	/**
	 * Drop LogLevel
	 * @param level
	 * @return new logging level
	 */
	public synchronized String dropLogLevel(LogLevel level){
		
		if (defaultLogLevel.contains(level.toString())){
			defaultLogLevel = 
				defaultLogLevel.replaceAll(level.toString(), "");
		}
		return defaultLogLevel;
	}
	
	/**
	 * Retrieve the current logLevel
	 * @return logLevel
	 */
	public synchronized String getLogLevel() {
		return defaultLogLevel;
	}
	
	/**
	 * Shutdown the Logging
	 */
	public void terminateLogging(){
		printstream.close();
	}
	
	/**
	 * Embedded testing module
	 * VM argument:
	 * -Dcom.joyfulv.LOG_LEVEL="DEBUG INFO"
	 * System variable:
	 * LOG_DIR "C:\Log"
	 * 
	 * @param args
	 */
	public static void main(String[] args){
		SingletonLogger logger = SingletonLogger.getLogger("TESTER");
		
		//logged
		logger.log(LogLevel.INFO, "Tester", "main", "Starting...");
		
		//Not logged
		logger.log(LogLevel.ERROR, "Tester", "main", "Starting 2...");
		
		logger.addLogLevel(LogLevel.ERROR);
		//logged
		logger.log(LogLevel.ERROR, "Tester", "main", "Starting 3...");
		
		logger.addLogLevel(LogLevel.WARNING);
		//logged
		logger.log(LogLevel.WARNING, "Tester", "main", "Starting 4...");
		
		logger.dropLogLevel(LogLevel.WARNING);
		logger.dropLogLevel(LogLevel.WARNING);

		//Not logged
		logger.log(LogLevel.WARNING, "Tester", "main", "Starting 5...");
		
		logger.terminateLogging();
		
		//Not logged
		logger.log(LogLevel.WARNING, "Tester", "main", "Starting 6...");
	}
}
