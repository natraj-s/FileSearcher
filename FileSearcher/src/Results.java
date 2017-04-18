/**
 * Results.java
 *
 * @author Natraj
 *	@date Jun 20, 2010
 *
 */

import javax.swing.*;
import java.awt.*; 
import java.util.ArrayList;
import java.util.Collections;
import java.awt.event.MouseListener;

public class Results {
	
	public ArrayList<IndividualResult> results;
	private ArrayList<JTextField> resultSelection;	
	private static final Color searchResultFg = Color.black;
	private static final Color searchResultBg = new Color(230, 230, 230);
	private JTextField aResult;
	
	public Results()
	{
		results = new ArrayList<IndividualResult>();
	}
	
	public void clearAll()
	{
		results.clear();
	}
	
	public JTextField makeAField(String text)
	{
		aResult = new JTextField(text);
		aResult.setHorizontalAlignment(SwingConstants.LEFT);
		aResult.setForeground(searchResultFg);
		aResult.setBackground(searchResultBg);
		aResult.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		aResult.setEditable(false);
		aResult.setName("Result Button");
		return aResult;
	}
	
	public void displayResults(JPanel thePanel, JScrollPane theArea, MouseListener aListener, GridBagConstraints properties)
	{
		Collections.sort(results);			
		
		for(int i = 0; i < results.size(); i++)
		{
			JTextField temp = makeAField(results.get(i).toString());
			temp.addMouseListener(aListener);			
			thePanel.add(temp, properties);
			theArea.revalidate();
		}
	}
	
	public void addResult(int lineNum, String theLine)
	{
		results.add(new IndividualResult(lineNum, theLine));
	}
	
	public class IndividualResult implements Comparable<IndividualResult> {
		
		public int lineNumber;
		public String text;
		
		public int compareTo(IndividualResult anotherResult) {
			if(this.getLineNumber() > anotherResult.getLineNumber())
			{
				return 1;
			}
			else
			{
				return 0;
			}
		}
		
		public IndividualResult(int lineNum, String theLine)
		{
			lineNumber = lineNum;
			text = theLine;
		}
		
		public String toString()
		{
			return lineNumber + ": " + text;
		}
		
		public int getLineNumber()
		{
			return lineNumber;
		}
		
		public String getText()
		{
			return text;
		}
	}
	
	

}
