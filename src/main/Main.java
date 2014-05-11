package main;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.DefaultCaret;

public class Main 
{
	public static final FileFrame frame = new FileFrame();
	
    public static void main(String[] args)
    {
    	Processes pro = new Processes();
    	Properties properties = new Properties();
    	frame.addWindowListener(new WindowListener());
    	
    	properties.parseOptionArguments(args);  	
    	
    	pro.loopCheckRunningProcess(properties.Closer, 20);
    	
    	boolean success = attemptRemove(5, properties.namesToLookFor, properties.baseFolder, frame);
    	
    	while (!success)
    	{
    		frame.retryButton.setEnabled(true);
    		frame.progressbar.setString("Auto-retry stopped, press retry button or remove manually");
    		frame.closeButton.setEnabled(true);
    		if(frame.retry)
    		{
    			frame.retry = false;
    			frame.retryButton.setEnabled(false);
    			frame.closeButton.setEnabled(false);
    			success = attemptRemove(1, properties.namesToLookFor, properties.baseFolder, frame);
    		}
    	}
    	frame.progressbar.setString("Moving file to mods folder");

    	attemptToMoveFiles(properties.filesToMove, properties.baseFolder);
    	
		frame.progressbar.setValue(100);
    	frame.progressbar.setString("Completed");
    	frame.closeButton.setEnabled(true);
    	try {
			Thread.sleep(2500);
		} catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
    	System.gc();
    	if (!properties.pause) System.exit(0);
    	
    }
    
    private static boolean attemptRemove(int attempts, List<String> namesToLookFor, File baseFolder, FileFrame frame)
    {
    	int tries = 1;
    	while(tries <= attempts)
    	{
    		frame.progressbar.setString("Removing files: attempt " + new Integer(tries).toString() + " of " + new Integer(attempts).toString());
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
								frame.addLine(Boolean.toString(f.canRead()));
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
					frame.addLine("Failed to copy... attempt " + new Integer(tries).toString() + " of  " + new Integer(attempts).toString());
					tries++;
					Thread.sleep(3000);
					continue;
				}
				else
				{
					return true;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
    	}
    	return false;
    }
    
    private static void attemptToMoveFiles(List<File> filesToMove, File baseFolder)
    {
		for(int fileNumber = 0;fileNumber < filesToMove.size();fileNumber++)
		{
			File f = filesToMove.get(fileNumber);
			frame.progressbar.setValue((fileNumber * 100) / filesToMove.size());
			File destinationFile = new File(baseFolder, "mods/" + f.getName());
			if(destinationFile.exists())
			{
				frame.addLine("Found another instance of " + f.getName() + ". Attempting to overwrite...");
				if(!destinationFile.delete())
				{
					frame.addLine("Failed to overwrite " + destinationFile.getName());
				}
			}
			if(f.renameTo(new File(baseFolder, "mods/" + f.getName())))
			{
				System.out.println("Successfully moved " + f.getName() + " to mods directory.");
				frame.addLine("Successfully moved " + f.getName() + " to mods directory");
			}
		}
    }
    
    static class FileFrame extends JFrame implements ActionListener
    {
		private static final long serialVersionUID = 1L;
		
		public JProgressBar progressbar = new JProgressBar();
		public JTextArea log = new JTextArea();
		public JButton retryButton = new JButton("retry");
		public JButton closeButton = new JButton("close");
		
		boolean retry = false;
		
    	public FileFrame()
    	{
    		super("XEliteZ File Utility");
    		
    		JPanel pane = new JPanel();
    		pane.setLayout(new GridBagLayout());
    		
    		GridBagConstraints c = new GridBagConstraints();
    		c.fill = GridBagConstraints.BOTH;
    		
    		progressbar.setPreferredSize(new Dimension(400, 25));
    		
    		JScrollPane scrollPane = new JScrollPane(log);
    		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    		scrollPane.setPreferredSize(new Dimension(400, 125));
    		DefaultCaret caret = (DefaultCaret)log.getCaret();
    		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    		
    		retryButton.setPreferredSize(new Dimension(175, 25));
    		retryButton.setActionCommand("retry");
    		retryButton.addActionListener(this);
    		retryButton.setEnabled(false);
    		closeButton.setPreferredSize(new Dimension(175, 25));
    		closeButton.setActionCommand("close");
    		closeButton.addActionListener(this);
    		closeButton.setEnabled(false);
    		
    		c.insets = new Insets(10, 10, 10, 10);
    		
    		this.addToGrid(progressbar, 1, 1, 3, 1, pane, c);
    		
    		c.insets = new Insets(10, 10, 10, 10);
    		
    		this.addToGrid(retryButton, 1, 5, 1, 1, pane, c);
    		
    		c.insets = new Insets(10, 10, 10, 10);
    		
    		this.addToGrid(closeButton, 3, 5, 1, 1, pane, c);
    		
    		c.insets = new Insets(10, 10, 10, 10);
    		
    		this.addToGrid(scrollPane, 1, 3, 3, 1, pane, c);
    		
    		log.setEditable(false);
    		
    		this.add(pane);
    		
    		this.pack();
    		this.setLocationRelativeTo(null);
    		this.setVisible(true);
    	}
    	
    	public void actionPerformed(ActionEvent e) 
    	{
    	    if ("retry".equals(e.getActionCommand())) 
    	    {
    	    	retry = true;
    	    }
    	    else if ("close".equals(e.getActionCommand()))
    	    {
    	    	System.exit(0);
    	    }
    	    
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
