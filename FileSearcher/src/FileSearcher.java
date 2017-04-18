/**
 * FileSearcher.java
 *
 * @author Natraj
 *	@date Jun 8, 2010
 *
 */

import java.io.*;
import java.util.*;

import java.awt.*; 

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import javax.swing.GroupLayout.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import java.lang.Thread;
import java.text.DecimalFormat;

public class FileSearcher extends JFrame implements ActionListener, DocumentListener, MouseListener {
	
	public static int FILE_SIZE;
	
	/* File input and output related identifiers */
	public Searcher threadArray[];
	public Results newSet;
	
	/* Thread operation related identifiers */
	private int numberOfThreads = 0;
	private static int LINES_TO_SPAN = 300;
	private int begin = 0;
	private int end = 0;
	private boolean started = false;
	
	
	/* Swing related identifiers */
	public JTextArea contentArea;
	public Highlighter finder;
	private JMenuBar theMenuBar;
	private JMenu basicTaskMenu;
	private JMenuItem openTask;
	private JMenuItem saveTask;
	private JMenu infoMenu;
	private JMenuItem aboutTask;
	private JTextField searchInput;	
	private JTextField replaceInput;
	private JTextField previousSearchResult; 
	private JPanel resultsArea;
	private JScrollPane theArea;
	private JScrollPane theResults;
	private JLabel searchLabel;
	private JLabel replaceLabel;
	private JButton replaceButton;
	private GridBagConstraints forResults;
	
	private Highlighter.HighlightPainter linePaint;
	private static final Color searchResultHighlight = Color.GRAY;
	private static final Color searchResultFg = Color.black;
	private static final Color searchResultFgClicked = Color.white;
	private static final Color searchResultBg = new Color(230, 230, 230);
	
	/**
	 *	main
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FileSearcher newInstance = new FileSearcher();
	}
	
	public FileSearcher()
	{
		setLookAndFeel();
		setupUI();		
	}

	/**
	 * actionPerformed
	 * 
	 */
	public void actionPerformed(ActionEvent arg0) {
		
		if(arg0.getSource() == openTask)
		{
			FileOpen openNewFile = new FileOpen(contentArea);
			
			if(openNewFile.isFile() == true)
			{
				initEverything();
			}
		}
		else if(arg0.getSource() == aboutTask)
		{
			printIntro();
		}
		else
		{
			launchThreads();
			setResultPaneLayout();			
			newSet.displayResults(resultsArea, theResults, this, forResults);			
		}
		
	}

	public void launchThreads()
	{
		long currentTime = System.currentTimeMillis();
		String searchString = searchInput.getText();
		finder.removeAllHighlights();
		
		if(!searchString.equals(""))
		{
			if(started == true)
			{
				newSet.clearAll();
				resultsArea.removeAll();
				theResults.revalidate();
				begin = 0;
				end = 0;
			}
			if(started == false)
			{
				newSet = new Results();
				if(FILE_SIZE > 1000)
				{
					LINES_TO_SPAN = 1000;
				}
				numberOfThreads = FILE_SIZE/LINES_TO_SPAN;
				if( (FILE_SIZE		%LINES_TO_SPAN) != 0)
				{
					numberOfThreads++;
				}
				
				threadArray = new Searcher[numberOfThreads];
			}
			
			for(int i = 0; i < numberOfThreads; i++)
			{
				if(end + LINES_TO_SPAN < FILE_SIZE)
				{
					end = end + LINES_TO_SPAN;
				}
				else
				{
					end = end + (FILE_SIZE%LINES_TO_SPAN);
				}
				
				threadArray[i] = new Searcher(begin, end, searchString, newSet, contentArea, finder);
				threadArray[i].start();
				begin = end;
			}
			
			started = true;
			
			try {
				for (int i = 0; i < numberOfThreads; ++i)
					threadArray[i].join();
			} catch (InterruptedException ie) {
			}
		}
		System.out.println("Time executed: " + (System.currentTimeMillis() - currentTime)/1000.0);
	}

	/**
	 *	printIntro
	 */
	public void printIntro()
	{
		JPanel pane = new JPanel();
		pane.setLayout(new GridLayout(3,0));		
		String intro = "";
		JLabel introMsg;
		// The Intro msg.
		intro = "     Welcome to the File Searcher Program! June 8, 2010\n	";
		introMsg = new JLabel(intro);
		introMsg.setFont(new Font("Helvetica", Font.PLAIN, 11));
		pane.add(introMsg);
		intro = "                            Final version for now	    \n";
		introMsg = new JLabel(intro);
		introMsg.setFont(new Font("Helvetica", Font.PLAIN, 11));
		pane.add(introMsg);
		intro = "                       Author: Natraj Subramanian	\n\n";
		introMsg = new JLabel(intro);
		introMsg.setFont(new Font("Helvetica", Font.PLAIN, 11));
		pane.add(introMsg);
		
		JOptionPane.showMessageDialog(null, pane, "Welcome to the File Searcher Program", JOptionPane.INFORMATION_MESSAGE);
	}

	public void initEverything()
	{	
		
		FILE_SIZE = contentArea.getLineCount();			
		
		finder =  new DefaultHighlighter();
		
		linePaint = new DefaultHighlighter.DefaultHighlightPainter(Color.gray);		
		contentArea.setHighlighter(finder);
		searchInput.addActionListener(this);
		searchInput.getDocument().addDocumentListener(this);
		searchInput.setEnabled(true);
		replaceInput.addActionListener(this);
		replaceInput.setEnabled(true);
		replaceButton.addActionListener(this);
		replaceButton.setEnabled(true);
	}
	
	/**
	 * 
	 *	setLookAndFeel
	 */
	public void setLookAndFeel()
	{
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
	}

	public void setResultPaneLayout()
	{
		resultsArea.setLayout(new GridBagLayout());
		forResults = new GridBagConstraints();
		forResults.fill = GridBagConstraints.HORIZONTAL;	
		forResults.anchor = GridBagConstraints.FIRST_LINE_START;
		forResults.weightx = 1.0;
		forResults.weighty = 1.0;
		forResults.gridwidth = GridBagConstraints.REMAINDER;
		forResults.gridx = 0;
		forResults.gridy =  GridBagConstraints.RELATIVE;
		theResults.revalidate();
	}

	public void setupUI()
	{
		searchInput = new JTextField();
		replaceInput = new JTextField();
		contentArea = new JTextArea();
		resultsArea = new JPanel();
		searchLabel = new JLabel();
		replaceLabel = new JLabel();
		replaceButton = new JButton("Replace");
		
		setTitle("Welcome to the File Searcher!");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		contentArea.setColumns(20);
		contentArea.setLineWrap(true);
		contentArea.setRows(5);
		contentArea.setWrapStyleWord(true);
		contentArea.setFont(new Font("Tahoma", Font.PLAIN, 11));
		contentArea.setEditable(false);
		theArea = new JScrollPane(contentArea);	
		
		resultsArea.setBackground(searchResultBg);
		theResults = new JScrollPane(resultsArea);
		theResults.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		searchLabel.setText("Search for:   ");
		replaceLabel.setText("Replace with: ");
		searchInput.setEnabled(false);
		replaceInput.setEnabled(false);
		replaceButton.setEnabled(false);
		
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		
		ParallelGroup hGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		
		SequentialGroup h1 = layout.createSequentialGroup();
		ParallelGroup h2 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
	
		h1.addContainerGap();
		h1.addComponent(theResults, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE);
		h2.addComponent(theArea, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE);
		
		SequentialGroup h3 = layout.createSequentialGroup();
		h3.addComponent(searchLabel);
		h3.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		h3.addComponent(searchInput, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE);
		h3.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		h2.addGroup(h3);	
		SequentialGroup h4 = layout.createSequentialGroup();
		h4.addComponent(replaceLabel);
		h4.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		h4.addComponent(replaceInput, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE);
		h4.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		h4.addComponent(replaceButton, GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE);
		h2.addGroup(h4);	
		SequentialGroup h5 = layout.createSequentialGroup();
		h5.addComponent(replaceButton, GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE);
		h5.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		
		h1.addGroup(h2);
		h1.addContainerGap();
		
		hGroup.addGroup(GroupLayout.Alignment.TRAILING, h1);
		layout.setHorizontalGroup(hGroup);
		
		ParallelGroup vGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		SequentialGroup v1 = layout.createSequentialGroup();
		v1.addContainerGap();
		ParallelGroup v2 = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
		v2.addComponent(searchLabel);
		v2.addComponent(searchInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		v1.addGroup(v2);
		v1.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
		ParallelGroup v4 = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
		v4.addComponent(replaceLabel);
		v4.addComponent(replaceInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		v4.addComponent(replaceButton, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE);
		v1.addGroup(v4);
		
		ParallelGroup v3 = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
		v3.addComponent(theResults);
		v3.addComponent(theArea, GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE);
		v1.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED);
		v1.addGroup(v3);
		v1.addContainerGap();
		
		vGroup.addGroup(v1);
		layout.setVerticalGroup(vGroup);
		
		// Set Menu Bar
		theMenuBar = new JMenuBar();
		basicTaskMenu = new JMenu("File");
		basicTaskMenu.getAccessibleContext().setAccessibleDescription("Menu that handles basic file tasks");
		basicTaskMenu.setMnemonic(KeyEvent.VK_F);
		theMenuBar.add(basicTaskMenu);
		
		openTask = new JMenuItem("Open                              ");
		openTask.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		openTask.addActionListener(this);
		basicTaskMenu.add(openTask);
		saveTask = new JMenuItem("Save                              ");
		saveTask.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		saveTask.addActionListener(this);
		basicTaskMenu.add(saveTask);
		
		infoMenu = new JMenu("Info");
		aboutTask = new JMenuItem("About                    ");
		aboutTask.addActionListener(this);
		infoMenu.add(aboutTask);
		theMenuBar.add(infoMenu);
		this.setJMenuBar(theMenuBar);
		
		pack();
		setVisible(true);		
	}
	
	public void changedUpdate(DocumentEvent arg0) {
		launchThreads();
		
	}

	public void insertUpdate(DocumentEvent arg0) {
		launchThreads();
	}

	public void removeUpdate(DocumentEvent arg0) {
		launchThreads();
	}

	public void mouseClicked(MouseEvent arg0) {	
		
		if(((Component)arg0.getSource()).getName().equals("Result Button"))
		{
			int startOffset = 0;
			int endOffset = 0;
			int searchIndex = 0;
			int lineNumber = 0;
			String resultLine;
			
			finder.removeAllHighlights();
			
			if(previousSearchResult != arg0.getSource() && previousSearchResult != null)
			{
				previousSearchResult.setForeground(searchResultFg);
				previousSearchResult.setBackground(searchResultBg);
			}
			
			( (JTextField)arg0.getSource() ).setForeground(searchResultFgClicked);
			( (JTextField)arg0.getSource() ).setBackground(searchResultHighlight);
			
			try {
				resultLine = ((JTextField)arg0.getSource()).getText();
				searchIndex = resultLine.indexOf(":");
				lineNumber = Integer.parseInt(resultLine.substring(0, searchIndex));
				startOffset = contentArea.getLineStartOffset(lineNumber);
				endOffset = contentArea.getLineEndOffset(lineNumber);
				finder.addHighlight(startOffset, endOffset, linePaint);
				contentArea.setCaretPosition(startOffset);
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			previousSearchResult = (JTextField)arg0.getSource();
			
		}		
		
	}
	
	public void mouseEntered(MouseEvent arg0) {
		if(((Component)arg0.getSource()).getName().equals("Result Button"))
		{
			( (JTextField)arg0.getSource() ).setBackground(searchResultHighlight);
		}
	}

	public void mouseExited(MouseEvent arg0) {
		if(((Component)arg0.getSource()).getName().equals("Result Button"))
		{
			if(previousSearchResult != arg0.getSource())
			{
				( (JTextField)arg0.getSource() ).setBackground(searchResultBg);
			}			
		}
	}

	public void mousePressed(MouseEvent arg0) {
		
	}


	public void mouseReleased(MouseEvent arg0) {
		
	}
}
