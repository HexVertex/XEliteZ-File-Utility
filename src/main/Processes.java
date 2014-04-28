package main;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Processes 
{
	private String processes = "";
	private String OS = System.getProperty("os.name").toLowerCase();
	
	/**
	 * Updates process list by syncing it
	 * @return true if succeeded, false in any other case
	 */
	public boolean updateRunningProcesses()
	{
		try
		{
			processes = "";
			Process p;
			if(OS.indexOf("win") >= 0)
			{
				p = Runtime.getRuntime().exec(System.getenv("windir") +"\\system32\\"+"tasklist.exe /v");
			}
			else
			{
				p = Runtime.getRuntime().exec("ps -e -v");
			}
			
			if(p == null)
			{
				return false;
			}
			BufferedReader input =  new BufferedReader(new InputStreamReader(p.getInputStream()));
			String temp = "";
			String line;
			
			while ((line = input.readLine()) != null) 
			{
				temp += line; 
			}
			processes = temp;
			return true;
		}
		catch(Exception e)
		{
			
		}
		return false;
	}
	
	/**
	 * Checks if the given process is running ATM
	 * @param processName The name of the running process
	 * @return true if the process is running
	 */
	public boolean getIsProcessRunning(String processName)
	{
		if(processes.contains(processName))
		{
			return true;
		}
		return false;
	}
	
	public String getProcesses()
	{
		return processes;
	}
	
	public String getOS()
	{
		return OS;
	}
}
