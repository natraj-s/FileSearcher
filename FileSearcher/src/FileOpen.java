/**
 * FileOpen.java
 *
 * @author Natraj
 *	@date Jun 20, 2010
 *
 */

import java.io.*;
import java.awt.*; 
import javax.swing.*;

public class FileOpen extends JPanel {
	
	public int choice;
	public JFileChooser newChooser;
	public File inputFile;
	public FileReader in;
	public boolean fileExists = false;
	public int shared;
	
	public FileOpen(JTextArea contentArea)
	{
		newChooser = new JFileChooser();
		choice = newChooser.showOpenDialog(FileOpen.this);
		inputFile = newChooser.getSelectedFile();
		
		if(choice ==JFileChooser.APPROVE_OPTION)
		{
			fileExists = true;
			try {
				in = new FileReader(inputFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			storeContent(contentArea);
		}
		else if(choice == JFileChooser.CANCEL_OPTION)
		{
			fileExists = false;
		}		
	}
	
	public void storeContent(JTextArea contentArea)
	{
		try {
			contentArea.read(in, inputFile.toString());
			contentArea.setFont(new Font("Tahoma", Font.PLAIN, 11));	
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isFile()
	{
		return fileExists;
	}

}
