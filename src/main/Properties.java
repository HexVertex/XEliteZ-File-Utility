package main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Properties 
{
	public String OS = System.getProperty("os.name").toLowerCase();
	
	public boolean pause = false;
	public List<String> namesToLookFor = new ArrayList<String>();
	public List<File> filesToMove = new ArrayList<File>();
	public File baseFolder = null;
	public String Closer = "Minecraft";
	
	public void parseOptionArguments(String[] args)
	{
		for(int i = 0;i < args.length;i++)
    	{
    		if(args[i].matches("-s"))
    		{
    			String params = args[i + 1];
    			while(params.contains(","))
    			{
    				namesToLookFor.add(params.substring(0, params.indexOf(",")));
    				params = params.substring(params.indexOf(",") + 1);
    			}
    			namesToLookFor.add(params);
    		}
    		if(args[i].matches("-m"))
    		{
    			String params = args[i + 1];
    			while(params.contains(","))
    			{
    				filesToMove.add(new File(params.substring(0, params.indexOf(","))));
    				params = params.substring(params.indexOf(",") + 1);
    			}
    			filesToMove.add(new File(params));
    		}
    		if(args[i].matches("-f"))
    		{
    			baseFolder = new File(args[i + 1]);
    		}
    		if(args[i].matches("-p"))
    		{
    			pause = true;;
    		}
    		if(args[i].matches("-t"))
    		{
    			Closer = args[i + 1];
    		}
    	}
		
    	try
    	{
    		Main.frame.progressbar.setString("Read information");
    		Main.frame.progressbar.setStringPainted(true);
    		Main.frame.addLine("Information read");
    		if(baseFolder != null)
    		{
    			Main.frame.addLine("Minecraft base folder: " + baseFolder.getCanonicalPath());
    		}
    		String names = "";
    		for(String str : namesToLookFor)
    		{
    			names += str;
    			names += ",";
    		}
    		if(names.length() > 0)
    		{
    			Main.frame.addLine("Strings to look for and remove: " + names.substring(0, names.length() - 1));
    		}
    		Main.frame.addLine("Number of files to move: " + new Integer(filesToMove.size()).toString());
    	} catch(Exception e)
    	{
    		e.printStackTrace();
    	}
	}
	
}
