/**
 * Searcher.java
 *
 * @author Natraj
 *	@date Jun 20, 2010
 *
 */

import javax.swing.*;

import java.awt.Color;
import java.lang.Thread;
import java.text.DecimalFormat;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

public class Searcher extends Thread{
	
	public int numFound = 0;
	private int startLine = 0;
	public JTextArea contentArea;
	private int endLine = 0;
	private String theString = "";
	public Highlighter finder;
	private Highlighter.HighlightPainter painter;
	
	public Searcher(int startLine, int endLine, String theString, Results aSet, JTextArea contentArea, Highlighter finder)
	{
		painter = new DefaultHighlighter.DefaultHighlightPainter(Color.ORANGE);
		run(startLine, endLine, theString, aSet, contentArea,finder);
	}
	
	synchronized public void run(int startLine, int endLine, String theString, Results aSet, JTextArea contentArea, Highlighter finder)
	{
		String line;
		int index = 0;
		int startOffset = 0;
		int endOffset = 0;
		int end = 0;
		DecimalFormat sortFormat = new DecimalFormat("00");
		
		for(int i = startLine; i < endLine; i++)
		{
			try {
				startOffset = contentArea.getLineStartOffset(i);
				endOffset = contentArea.getLineEndOffset(i);
				line = contentArea.getText(startOffset, endOffset - startOffset);
				
				while((index = (line.toLowerCase()).indexOf(theString, end)) != -1)
				{
					end = index + theString.length();
					finder.addHighlight(startOffset + index, startOffset + end, painter);
					aSet.addResult(i, line);
				}
				
				index = 0;
				end = 0;
				
			} catch(BadLocationException e) {
				e.printStackTrace();
			}
			
		}
	}

}
