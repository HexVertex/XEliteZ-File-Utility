package main;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

public class Main 
{
	
    public static void main(String[] par0ArrayOfStr)
    {
    	boolean pause = false;
    	Processes pro = new Processes();
    	FileFrame frame = new FileFrame();
    	frame.addWindowListener(new WindowListener());
    	List<String> namesToLookFor = new ArrayList<String>();
    	List<File> filesToMove = new ArrayList<File>();
    	File baseFolder = null;
    	String dispTitle = "Minecraft";
    	for(int i = 0;i < par0ArrayOfStr.length;i++)
    	{
    		if(par0ArrayOfStr[i].matches("-s"))
    		{
    			String params = par0ArrayOfStr[i + 1];
    			while(params.contains(","))
    			{
    				namesToLookFor.add(params.substring(0, params.indexOf(",")));
    				params = params.substring(params.indexOf(",") + 1);
    			}
    			namesToLookFor.add(params);
    		}
    		if(par0ArrayOfStr[i].matches("-m"))
    		{
    			String params = par0ArrayOfStr[i + 1];
    			while(params.contains(","))
    			{
    				filesToMove.add(new File(params.substring(0, params.indexOf(","))));
    				params = params.substring(params.indexOf(",") + 1);
    			}
    			filesToMove.add(new File(params));
    		}
    		if(par0ArrayOfStr[i].matches("-f"))
    		{
    			baseFolder = new File(par0ArrayOfStr[i + 1]);
    		}
    		if(par0ArrayOfStr[i].matches("-p"))
    		{
    			pause = true;;
    		}
    		if(par0ArrayOfStr[i].matches("-t"))
    		{
    			dispTitle = par0ArrayOfStr[i + 1];
    		}
    	}
    	try
    	{
    		frame.progressbar.setString("Read information");
    		frame.progressbar.setStringPainted(true);
    		frame.addLine("Information read");
    		if(baseFolder != null)
    		{
    			frame.addLine("Minecraft base folder: " + baseFolder.getCanonicalPath());
    		}
    		String names = "";
    		for(String str : namesToLookFor)
    		{
    			names += str;
    			names += ",";
    		}
    		if(names.length() > 0)
    		{
    			frame.addLine("Strings to look for and remove: " + names.substring(0, names.length() - 1));
    		}
    		frame.addLine("Number of files to move: " + new Integer(filesToMove.size()).toString());
    	} catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    	int maxTries = 5;
    	
    	while(pro.updateRunningProcesses())
    	{
    		if(!pro.getIsProcessRunning(dispTitle)) break;
    		if(pro.getOS().toLowerCase().indexOf("win") < 0)
    		{
        		frame.addLine("This feature is developed under windows \n\rand therefore this feature has not been tested in other OS's");
        		frame.addLine("Skipping program check for now");
        		frame.addLine("Increasing max-trycount to 20");
        		maxTries = 20;
        		break;
    		}
    		frame.addLine("Please close all windows with title: " + dispTitle);
        	try {
    			Thread.sleep(2500);
    		} catch (InterruptedException e) 
    		{
    			e.printStackTrace();
    		}
    	}
    	
    	int tries = 1;
    	while(tries <= maxTries)
    	{
    		frame.progressbar.setString("Removing files: attempt " + new Integer(tries).toString() + " of " + new Integer(maxTries).toString());
    		if(baseFolder == null)
    			break;
			try
			{
				File modsDir = new File(baseFolder, "mods");
				File[] mods = modsDir.listFiles();
				boolean failed = false;
				for(int fileNumber = 0;fileNumber < mods.length;fileNumber++)
				{
					File f = mods[fileNumber];
					frame.progressbar.setValue((fileNumber * 100) / mods.length);
					for(String s : namesToLookFor)
					{
						if(f.getName().contains(s))
						{
							if(f.delete())
							{
								System.out.println("Successfully removed " + f.getName());
								frame.addLine("Successfully removed " + f.getName());
							}
							else
							{
								System.out.println("Failed to remove " + f.getName());
								frame.addLine("Failed to remove " + f.getName());
								failed = true;
							}
						}
					}
				}
				if(failed)
				{
					System.out.println("Due to fail now waiting 3 seconds.");
					frame.addLine("Failed to copy... attempt " + new Integer(tries).toString() + " of  " + new Integer(maxTries).toString());
					tries++;
					Thread.sleep(3000);
					continue;
				}
				else
				{
					break;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
    	}
    	frame.progressbar.setString("Moving file to mods folder");
		for(int fileNumber = 0;fileNumber < filesToMove.size();fileNumber++)
		{
			File f = filesToMove.get(fileNumber);
			frame.progressbar.setValue((fileNumber * 100) / filesToMove.size());
			File destinationFile = new File(baseFolder, "mods/" + f.getName());
			if(destinationFile.exists())
			{
				frame.addLine("Found another instance of " + f.getName() + ". Attempting to overwrite...");
				destinationFile.delete();
			}
			if(f.renameTo(new File(baseFolder, "mods/" + f.getName())))
			{
				System.out.println("Successfully moved " + f.getName() + " to mods directory.");
				frame.addLine("Successfully moved " + f.getName() + " to mods directory");
			}
		}
		frame.progressbar.setValue(100);
    	frame.progressbar.setString("Completed");
    	try {
			Thread.sleep(2500);
		} catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
    	if (!pause) System.exit(0);
    }
    
    static class FileFrame extends JFrame
    {
		private static final long serialVersionUID = 1L;
		
		public JProgressBar progressbar = new JProgressBar();
		public JTextArea log = new JTextArea();
		
    	public FileFrame()
    	{
    		super("XEliteZ File Utility");
    		
    		JPanel pane = new JPanel();
    		pane.setLayout(new GridBagLayout());
    		
    		GridBagConstraints c = new GridBagConstraints();
    		c.fill = GridBagConstraints.BOTH;
    		
    		progressbar.setPreferredSize(new Dimension(400, 25));
    		log.setPreferredSize(new Dimension(400, 125));
    		
    		c.insets = new Insets(10, 10, 10, 10);
    		
    		this.addToGrid(progressbar, 1, 1, 3, 1, pane, c);
    		
    		c.insets = new Insets(10, 10, 10, 10);
    		
    		this.addToGrid(log, 1, 3, 3, 1, pane, c);
    		
    		log.setEditable(false);
    		
    		this.add(pane);
    		
    		this.pack();
    		this.setLocationRelativeTo(null);
    		this.setVisible(true);
    	}
    	
    	public final void addToGrid(Component comp, int gridx, int gridy, int gridwidth, double weightx,
    			JPanel panel, GridBagConstraints c) {
    		c.gridx = gridx;
    		c.gridy = gridy;
    		c.gridwidth = gridwidth;
    		c.weightx = weightx;
    		panel.add(comp, c);
    	}
    	
    	public void addLine(String s)
    	{
    		log.append(s + "\r\n");
    	}
    }
    
    static class WindowListener extends WindowAdapter
    {
        public void windowClosing(WindowEvent par1WindowEvent)
        {
            System.err.println("Stopping!");
            System.exit(1);
        }
    }
}
