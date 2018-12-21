package cecs429;

import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.documents.JsonFileDocument;
import cecs429.index.DiskPositionalIndex;
import cecs429.index.Index;
import cecs429.index.PositionalInvertedIndex;
import cecs429.index.Posting;
import cecs429.query.BooleanQueryParser;
import cecs429.query.QueryComponent;
import cecs429.query.RankedQueryParser;
import cecs429.text.EnglishTokenStream;
import cecs429.text.MultipleTokenProcessor;
import cecs429.text.PorterTokenProcessor;
import cecs429.text.TokenStream;
import cecs429.variantmethods.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static cecs429.GUIApplication.MILESTONE_1;
import static cecs429.GUIApplication.MILESTONE_2;

class MilestoneJFrame extends JFrame {

	private String mExtension;
	private String mSelectedFilename;

	private JFrame mFromJFrame;
	private JSplitPane mSplitPane;
	private JSplitPane mSplitPaneLeft;
	private JSplitPane mSplitPaneRight;
	private JTree mTree;
	private JTree mTree1;
	private JTree mTree2;
	private JTree mTree3;
	private JTree mTree4;
	private JLabel mLablePath;
	private JLabel mLableInfo;
	private File mCorpusDirectory;
	private JTextField mTextFieldQuery;
	private JTextArea mTextAreaOutput;
	private JButton mButtonIndex;
	private JButton mButtonSearch;
	private JButton mButtonDiskIndex;
	private JButton mButtonReset;
	private JButton mButtonVocab;
	private JButton mButtonStem;
	private JButton mBtnSearchDiskIndex;
	private JButton mBtnStatistics;
	private JComboBox mTfIdfVariants;
	private JRadioButton radioRankedRetrieval;
	private DocumentCorpus mDocumentCorpus;
	private MultipleTokenProcessor mTokenProcessor;
	private String[] variants = { "Default", "tf-idf", "Wacky", "Okapi BM25" };
	private Index index;
	private JPanel mJPanel;
	private JScrollPane mTextAreaMap;

	private List<Posting> resultPostings1;
	private List<Posting> resultPostings2;
	private List<Posting> resultPostings3;
	private List<Posting> resultPostings4;

	/**
	 * Constructor for Milestone JFrame.
	 *
	 * @param milestoneNumber Milestone differentiator.
	 * @param fromJFrame      From which this (current) framework has opened.
	 */
	MilestoneJFrame(int milestoneNumber, JFrame fromJFrame) {
		mFromJFrame = fromJFrame;
		initializeMilestone(milestoneNumber);
		resultPostings1 = new ArrayList<>();
		resultPostings2 = new ArrayList<>();
		resultPostings3 = new ArrayList<>();
		resultPostings4 = new ArrayList<>();
	}

	private void initializeMilestone(int milestoneNumber) {

		mTokenProcessor = new PorterTokenProcessor();
		if (milestoneNumber == MILESTONE_1) {
			setTitle("Milestone 1");
		} else if (milestoneNumber == MILESTONE_2) {
			setTitle("Milestone 2");
		} else {
			setTitle("Milestone 3");
		}
		setBounds(new Rectangle(0, 0, 900, 900));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		mJPanel = new JPanel();
		mJPanel.setBounds(20, 20, 800, 200);
		getContentPane().add(mJPanel);
		mJPanel.setLayout(null);

		JLabel lblSelectCorpus = new JLabel("Select Corpus");
		lblSelectCorpus.setBounds(10, 8 + 50, 90, 14);
		mJPanel.add(lblSelectCorpus);

		mLablePath = new JLabel("current mDocumentCorpus");
		mLablePath.setBounds(110, 8 + 50, 603, 14);
		mJPanel.add(mLablePath);

		JButton buttonBrowse = new JButton("Browse");
		buttonBrowse.addActionListener(e -> chooseDirectory(MilestoneJFrame.this, milestoneNumber));
		buttonBrowse.setBounds(699, 4 + 50, 126, 23);
		mJPanel.add(buttonBrowse);

		JButton btnBack = new JButton();
		try {
			Image imageBack = ImageIO.read(getClass().getResource("resources/back.png")).getScaledInstance(40, 40,
					java.awt.Image.SCALE_SMOOTH);
			btnBack.setIcon(new ImageIcon(imageBack));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		btnBack.setBounds(10, 10, 40, 40);
		mJPanel.add(btnBack);

		btnBack.addActionListener(e -> {
			setVisible(false);
			mFromJFrame.setVisible(true);
		});

		JLabel labelEnterQuery = new JLabel("Enter Query");
		labelEnterQuery.setBounds(10, 33 + 50, 80, 14);
		mJPanel.add(labelEnterQuery);

		mTextFieldQuery = new JTextField();
		mTextFieldQuery.setBounds(110, 30 + 50, 788, 25);
		mJPanel.add(mTextFieldQuery);
		mTextFieldQuery.setColumns(50);

		mLableInfo = new JLabel("Logs:\n");
		mLableInfo.setBounds(10, 700 + 50, 603, 14);
		mJPanel.add(mLableInfo);

		mButtonReset = new JButton("Reset");
		mButtonReset.setBounds(335 + 265, 90 + 50, 239, 23);
		mButtonReset.setEnabled(false);
		mJPanel.add(mButtonReset);

		if (milestoneNumber == MILESTONE_1) {

			setMilestoneOneUI(milestoneNumber);

		} else if (milestoneNumber == MILESTONE_2) {

			setMilestoneTwoUI(milestoneNumber);

		} else {

			setMilestoneThreeUI(milestoneNumber);

		}
		setVisible(true);
	}

	/*
	 * Initialize UI elements required for Milestone One.
	 */
	private void setMilestoneOneUI(int milestoneNumber) {

		mButtonIndex = new JButton("Index");
		mButtonIndex.setBounds(335 + 265, 90 - 30 + 50, 239, 23);
		mButtonIndex.setEnabled(false);
		mButtonIndex.addActionListener(e -> {

			// After clicking index button, get current time before indexing and get time
			// after indexing.
			long startTime = System.currentTimeMillis();

			index = indexCorpus(mDocumentCorpus, mTokenProcessor, MILESTONE_1);
			long stopTime = System.currentTimeMillis();

			// Calculating total time taken for indexing.
			long elapsedTime = stopTime - startTime;
			mLableInfo.setText(mLableInfo.getText() + "\nDone Indexing." + "\n\nTime to index = "
					+ TimeUnit.MILLISECONDS.toSeconds(elapsedTime) + " seconds");
			enableButtons(milestoneNumber);
		});
		mJPanel.add(mButtonIndex);

		mSplitPane = new JSplitPane();
		mSplitPane.setBounds(10, 123 + 50, 854, 527);
		mJPanel.add(mSplitPane);

		mTextAreaOutput = new JTextArea();
		mTextAreaOutput.setWrapStyleWord(true);
		mSplitPane.setRightComponent(new JScrollPane(mTextAreaOutput));
		mSplitPane.setLeftComponent(new JScrollPane(new JTextArea()));

		mButtonSearch = new JButton("Search");
		mButtonSearch.setBounds(110, 90 - 30 + 50, 239, 23);
		mButtonSearch.setEnabled(false);
		mButtonSearch.addActionListener(e -> {
			if (mTextFieldQuery.getText().trim().isEmpty()) {
				JOptionPane.showMessageDialog(MilestoneJFrame.this, "Please enter a search query.");
			} else {
				searchClick(MILESTONE_1);
			}
		});
		mJPanel.add(mButtonSearch);

		mButtonVocab = new JButton("Show Vocabulary");
		mButtonVocab.setBounds(110, 90 + 50, 239, 23);
		mButtonVocab.setEnabled(false);
		mButtonVocab.addActionListener(e -> {

			java.util.List<String> vocabList = index.getVocabulary();
			mTextAreaOutput.setText("");
			for (int i = 0; i < 1000; i++) {

				mTextAreaOutput.setText(mTextAreaOutput.getText() + "\n" + vocabList.get(i));
			}
		});
		mJPanel.add(mButtonVocab);

		mButtonStem = new JButton("Stem");
		mButtonStem.setBounds(355, 90 - 30 + 50, 239, 23);
		mButtonStem.setEnabled(false);
		mButtonStem.addActionListener(e -> {
			mTextAreaOutput.setText("");
			mTextAreaOutput.setText(mTokenProcessor.processToken(mTextFieldQuery.getText()).get(0));
		});
		mJPanel.add(mButtonStem);

	}

	/*
	 * Initialize UI elements required for Milestone Two.
	 */
	private void setMilestoneTwoUI(int milestoneNumber) {
		mButtonDiskIndex = new JButton("DiskIndex");
		mButtonDiskIndex.setBounds(335 + 265, 90 - 30 + 50, 239, 23);
		mButtonDiskIndex.setEnabled(false);
		mButtonDiskIndex.addActionListener(e -> {

			// After clicking index button, get current time before indexing and get time
			// after indexing.
			long startTime = System.currentTimeMillis();
			index = indexCorpus(mDocumentCorpus, mTokenProcessor, MILESTONE_2);
			long stopTime = System.currentTimeMillis();

			// Calculating total time taken for indexing.
			long elapsedTime = stopTime - startTime;
			mLableInfo.setText(mLableInfo.getText() + "\nDone Indexing." + "\n\nTime to index = "
					+ TimeUnit.MILLISECONDS.toMillis(elapsedTime) + " seconds");

			// Start creating disk index.
			new DiskIndexWriter(mLablePath.getText() + "\\index").writeIndex(index);
			enableButtons(milestoneNumber);
		});
		mJPanel.add(mButtonDiskIndex);

		mSplitPane = new JSplitPane();
		mSplitPane.setBounds(10, 123 + 50, 854, 527);
		mJPanel.add(mSplitPane);

		mTextAreaOutput = new JTextArea();
		mTextAreaOutput.setWrapStyleWord(true);
		mSplitPane.setRightComponent(new JScrollPane(mTextAreaOutput));
		mSplitPane.setLeftComponent(new JScrollPane(new JTextArea()));

		mBtnSearchDiskIndex = new JButton("SearchFromDiskIndex");
		mBtnSearchDiskIndex.setBounds(110, 90 - 30 + 50, 239, 23);
		mBtnSearchDiskIndex.setEnabled(false);
		mBtnSearchDiskIndex.addActionListener(e -> {
			if (mTextFieldQuery.getText().trim().isEmpty()) {
				JOptionPane.showMessageDialog(MilestoneJFrame.this, "Please enter a search query.");
			} else {

				searchClick(MILESTONE_2);
			}
		});
		mJPanel.add(mBtnSearchDiskIndex);

		radioRankedRetrieval = new JRadioButton();
		radioRankedRetrieval.setMnemonic(KeyEvent.VK_B);
		radioRankedRetrieval.setActionCommand("Ranked");
		radioRankedRetrieval.setText("Ranked");
		radioRankedRetrieval.setBounds(10, 700, 100, 30);
		radioRankedRetrieval.setSelected(true);

		JRadioButton radioBooleanRetrieval = new JRadioButton();
		radioBooleanRetrieval.setMnemonic(KeyEvent.VK_B);
		radioBooleanRetrieval.setText("Boolean");
		radioBooleanRetrieval.setActionCommand("Boolean");
		radioBooleanRetrieval.setBounds(110, 700, 100, 30);

		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(radioRankedRetrieval);
		buttonGroup.add(radioBooleanRetrieval);
		mJPanel.add(radioRankedRetrieval);
		mJPanel.add(radioBooleanRetrieval);

		// Create the combo box, select item at index 4.
		// Indices start at 0, so 4 specifies the pig.
		mTfIdfVariants = new JComboBox(variants);
		mTfIdfVariants.setBounds(110, 90 + 50, 239, 23);
		mTfIdfVariants.setSelectedIndex(0);
		mTfIdfVariants.setEnabled(false);
		mJPanel.add(mTfIdfVariants);

	}

	/*
	 * Initilize UI elements for milestone three
	 */
	private void setMilestoneThreeUI(int milestoneNumber) {
		// TODO Auto-generated method stub

		mButtonDiskIndex = new JButton("DiskIndex");
		mButtonDiskIndex.setBounds(335 + 265, 90 - 30 + 50, 239, 23);
		mButtonDiskIndex.setEnabled(true);
		mButtonDiskIndex.addActionListener(e -> {

			// After clicking index button, get current time before indexing and get time
			// after indexing.
			long startTime = System.currentTimeMillis();
			index = indexCorpus(mDocumentCorpus, mTokenProcessor, milestoneNumber);
			long stopTime = System.currentTimeMillis();

			// Calculating total time taken for indexing.
			long elapsedTime = stopTime - startTime;
			mLableInfo.setText(mLableInfo.getText() + "\nDone Indexing." + "\n\nTime to index = "
					+ TimeUnit.MILLISECONDS.toMillis(elapsedTime) + " seconds");

			// Start creating disk index.
			new DiskIndexWriter(mLablePath.getText() + "\\index").writeIndex(index);
			enableButtons(milestoneNumber);
		});
		mJPanel.add(mButtonDiskIndex);

		mSplitPane = new JSplitPane();
		mSplitPane.setBounds(10, 123 + 50, 854, 527);

		// mTextAreaOutput = new JTextArea();
		// mTextAreaOutput.setWrapStyleWord(true);
		mSplitPaneLeft = new JSplitPane();
		mSplitPaneLeft.setLeftComponent(new JScrollPane(new TextArea()));
		mSplitPaneLeft.setRightComponent(new JScrollPane(new TextArea()));
		// mSplitPane.setBounds(10, 123 + 50, 854, 527);
		mSplitPaneRight = new JSplitPane();
		mSplitPaneRight.setLeftComponent(new JScrollPane(new TextArea()));
		mSplitPaneRight.setRightComponent(new JScrollPane(new TextArea()));
		mSplitPane.setRightComponent(mSplitPaneRight);
		mSplitPane.setLeftComponent(mSplitPaneLeft);
		mJPanel.add(mSplitPane);

		mBtnSearchDiskIndex = new JButton("SearchFromDiskIndex");
		mBtnSearchDiskIndex.setBounds(110, 90 - 30 + 50, 239, 23);
		mBtnSearchDiskIndex.setEnabled(true);
		mBtnSearchDiskIndex.addActionListener(e -> {
			if (mTextFieldQuery.getText().trim().isEmpty()) {
				JOptionPane.showMessageDialog(MilestoneJFrame.this, "Please enter a search query.");
			} else {

				searchClickThree(milestoneNumber);
			}
		});
		mJPanel.add(mBtnSearchDiskIndex);

		mBtnStatistics = new JButton("Statistics");
		mBtnStatistics.setBounds(110, 90 + 50, 239, 23);
		mBtnStatistics.setEnabled(true);
		mBtnStatistics.addActionListener(e -> {
			calculateStats();

		});
		mJPanel.add(mBtnStatistics);
		
		

		/*
		 * //Create the combo box, select item at index 4. //Indices start at 0, so 4
		 * specifies the pig. mTfIdfVariants = new JComboBox(variants);
		 * mTfIdfVariants.setBounds(110, 90 + 50, 239, 23);
		 * mTfIdfVariants.setSelectedIndex(0); mTfIdfVariants.setEnabled(false);
		 * mJPanel.add(mTfIdfVariants);
		 */

	}

	private void calculateStats() {

		try {
			Map<Integer, String> indexToQueryMap = new HashMap<>();
			Map<Integer, List<Integer>> indexToRelArray = new HashMap<>();

			BufferedReader queriesBr = new BufferedReader(
					new FileReader(mLablePath.getText() + "\\relevance\\queries"));
			BufferedReader qrelBr = new BufferedReader(new FileReader(mLablePath.getText() + "\\relevance\\qrel"));
			DiskPositionalIndex diskPositionalIndex = new DiskPositionalIndex(mLablePath.getText() + "\\index",
					variants.length);
			Map<Integer, HashMap<String,List<Posting>>> allQueriesMap=new HashMap<>();
			
			String query;
			String queryRelevance;
			int index = 0;

			HashMap<String,List<Posting>> hashMapUniqueAllTerms = new HashMap<>();
			while ((query = queriesBr.readLine()) != null && (queryRelevance = qrelBr.readLine()) != null ) {
				indexToQueryMap.put(index, query);
				String[] docids = queryRelevance.split("\\s+");

				List<Integer> temp = new ArrayList<>();
				for(int i=0; i<docids.length;i++) {

					temp.add(Integer.parseInt(docids[i]));
				}
				
				indexToRelArray.put(index, temp);
				
				String queryInput = query;
				String[] queryTerms = queryInput.split("\\s+");
				HashMap<String,List<Posting>> hashMapTerms = new HashMap<>();
				
				for (String term : queryTerms) {
					if (!term.equals("")) {
						if(!hashMapUniqueAllTerms.containsKey(term)) {
							
							List<Posting> postings = diskPositionalIndex
							.getPostingsWithoutPositions(mTokenProcessor.processToken(term).get(0));
							hashMapUniqueAllTerms.put(term, postings);
							hashMapTerms.put(term, postings);
						} else {
							hashMapTerms.put(term, hashMapUniqueAllTerms.get(term));
							
						}
					}
				}
//				System.out.println("index "+index);
				allQueriesMap.put(index, hashMapTerms);		
				index++;

			}

			mSplitPane.setVisible(false);
			mTextAreaOutput = new JTextArea();
			mTextAreaOutput.setText("  ");
			mTextAreaMap=new JScrollPane(mTextAreaOutput);
			mTextAreaMap.setBounds(10, 123 + 50, 854, 527);
			//mTextAreaOutput.setWrapStyleWord(true);
			mTextAreaMap.setVisible(true);
			mJPanel.add(mTextAreaMap);
			
			calcMeanAveragePrecision(index,indexToQueryMap,indexToRelArray,allQueriesMap, new DefaultVariant(),0);
			calcMeanAveragePrecision(index,indexToQueryMap,indexToRelArray,allQueriesMap, new TFIDFVariant(),1);
			calcMeanAveragePrecision(index,indexToQueryMap,indexToRelArray,allQueriesMap, new WackyVariant(),2);
			calcMeanAveragePrecision(index,indexToQueryMap,indexToRelArray,allQueriesMap, new OkapiVariant(),3);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void calcMeanAveragePrecision(int index2, Map<Integer, String> indexToQueryMap, Map<Integer, List<Integer>> indexToRelArray,
			Map<Integer, HashMap<String, List<Posting>>> allQueriesMap, VariantMethodsInterface defaultVariant, int i) {


		long startTime1 = System.currentTimeMillis();
		double meanAvgPrecision = 0.0;
		for (int k = 0; k < index2; k++) {
			List<Posting> resultPostings1 = new RankedQueryParser().getRankedDocuments(allQueriesMap.get(k),
					mDocumentCorpus.getCorpusSize(), defaultVariant, mLablePath.getText() + "\\index", i);
//			System.out.println(indexToQueryMap.get(k));
			
			double avgPrecision = 0.0;
			int relRetrived = 0;
			for (int p = 0; p < resultPostings1.size(); p++) {
				double precision=0.0;
				double recall=0.0;
				int docid=resultPostings1.get(p).getDocumentId();
				String docTitle=mDocumentCorpus.getDocument(docid).getTitle();

				if (indexToRelArray.get(k).contains(Integer.parseInt(docTitle.substring(0,docTitle.indexOf('.'))))) {

					relRetrived++;
					precision=( (relRetrived * 1.0) / ((p + 1) * 1.0));
					avgPrecision +=precision;						

					recall=( (relRetrived * 1.0) / (indexToRelArray.get(k).size() * 1.0));

				}
			}
		
				avgPrecision=avgPrecision/(indexToRelArray.get(k).size()*1.0);
				meanAvgPrecision += avgPrecision;
			
		}
		double stopTime1 = System.currentTimeMillis();
		double elapsedTime1 = stopTime1 - startTime1;
		
		meanAvgPrecision = ((meanAvgPrecision*1.0) / (index2 * 1.0));
		
		StringBuilder output = new StringBuilder();
		output.append(mTextAreaOutput.getText().toString()+"\n\n");
		output.append(variants[i]+"\n");
		output.append("Mean Average Precision:  " + meanAvgPrecision+"\n");
		double throughput = index2/(elapsedTime1);
		output.append("Mean Response Time:  "+1/throughput +" ms\n");
		output.append("Throughput:  " + throughput + " q/msec");
		mTextAreaOutput.setText(""+output);


	}

	private void searchClickThree(int milestoneNumber) {

		DefaultMutableTreeNode top1 = new DefaultMutableTreeNode("DefaultVariant");
		DefaultMutableTreeNode top2 = new DefaultMutableTreeNode("TFIDFVariant");
		DefaultMutableTreeNode top3 = new DefaultMutableTreeNode("WackyVariant");
		DefaultMutableTreeNode top4 = new DefaultMutableTreeNode("OkapiVariant");

		String query = mTextFieldQuery.getText();
		DiskPositionalIndex diskPositionalIndex = new DiskPositionalIndex(mLablePath.getText() + "\\index",
				variants.length);
		System.out.println("mLablePath.getText()" + "\\index");

		String[] queryTerms = mTextFieldQuery.getText().split(" ");

		HashMap<String, java.util.List<Posting>> hashMapTerms = new HashMap<>();

		for (String term : queryTerms) {
			java.util.List<Posting> postings = diskPositionalIndex
					.getPostingsWithoutPositions(mTokenProcessor.processToken(term).get(0));
			hashMapTerms.put(term, postings);
		}

		long startTime1 = System.currentTimeMillis();
		List<Posting> resultPostings1 = new RankedQueryParser().getRankedDocuments(hashMapTerms,
				mDocumentCorpus.getCorpusSize(), new DefaultVariant(), mLablePath.getText() + "\\index", 0);

		for (Posting posting : resultPostings1) {
			DefaultMutableTreeNode tempTreeNode;
			tempTreeNode = new DefaultMutableTreeNode(mDocumentCorpus.getDocument(posting.getDocumentId()).getTitle()
					+ " (ID " + posting.getDocumentId() + "): " + posting.getmAccumScore());
			top1.add(tempTreeNode);
		}
		long stopTime1 = System.currentTimeMillis();
		long elapsedTime1 = stopTime1 - startTime1;

		long startTime2 = System.currentTimeMillis();
		List<Posting> resultPostings2 = new RankedQueryParser().getRankedDocuments(hashMapTerms,
				mDocumentCorpus.getCorpusSize(), new TFIDFVariant(), mLablePath.getText() + "\\index", 1);

		for (Posting posting : resultPostings2) {
			DefaultMutableTreeNode tempTreeNode;
			tempTreeNode = new DefaultMutableTreeNode(mDocumentCorpus.getDocument(posting.getDocumentId()).getTitle()
					+ " (ID " + posting.getDocumentId() + "): " + posting.getmAccumScore());
			top2.add(tempTreeNode);
		}
		long stopTime2 = System.currentTimeMillis();
		long elapsedTime2 = stopTime2 - startTime2;

		long startTime3 = System.currentTimeMillis();
		List<Posting> resultPostings3 = new RankedQueryParser().getRankedDocuments(hashMapTerms,
				mDocumentCorpus.getCorpusSize(), new WackyVariant(), mLablePath.getText() + "\\index", 2);

		for (Posting posting : resultPostings3) {
			DefaultMutableTreeNode tempTreeNode;
			tempTreeNode = new DefaultMutableTreeNode(mDocumentCorpus.getDocument(posting.getDocumentId()).getTitle()
					+ " (ID " + posting.getDocumentId() + "): " + posting.getmAccumScore());
			top3.add(tempTreeNode);
		}
		long stopTime3 = System.currentTimeMillis();
		long elapsedTime3 = stopTime3 - startTime3;

		long startTime4 = System.currentTimeMillis();
		List<Posting> resultPostings4 = new RankedQueryParser().getRankedDocuments(hashMapTerms,
				mDocumentCorpus.getCorpusSize(), new OkapiVariant(), mLablePath.getText() + "\\index", 3);

		for (Posting posting : resultPostings4) {
			DefaultMutableTreeNode tempTreeNode;
			tempTreeNode = new DefaultMutableTreeNode(mDocumentCorpus.getDocument(posting.getDocumentId()).getTitle()
					+ " (ID " + posting.getDocumentId() + "): " + posting.getmAccumScore());
			top4.add(tempTreeNode);
		}
		long stopTime4 = System.currentTimeMillis();
		long elapsedTime4 = stopTime4 - startTime4;

		// Below is UI for setting up UI for showing the results.
		mLableInfo.setText("Logs: " + "\n\nTotal documents:" + top1.getChildCount());

		mTree1 = new JTree(top1);
		mTree2 = new JTree(top2);
		mTree3 = new JTree(top3);
		mTree4 = new JTree(top4);

		mSplitPaneLeft.setLeftComponent(new JScrollPane(mTree1));
		mSplitPaneLeft.setRightComponent(new JScrollPane(mTree2));
		mSplitPaneRight.setLeftComponent(new JScrollPane(mTree3));
		mSplitPaneRight.setRightComponent(new JScrollPane(mTree4));

		mLableInfo.setText(mLableInfo.getText() + "\nDone Indexing." + "\n\nTime to index = "
				+ TimeUnit.MILLISECONDS.toMillis(elapsedTime1) + " ms");

	}

	/**
	 * Method to open directory selector and get all files in that folder.
	 */
	private void chooseDirectory(JFrame jFrame, int milestoneNumber) {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(
				new File("D:\\SUBJECTS\\SEARCH_ENGINE\\SEARCH_ENGINE\\Milestone3\\relevance_cranfield"));
		chooser.setDialogTitle("Select Document Corpus");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		if (chooser.showSaveDialog(jFrame) == JFileChooser.APPROVE_OPTION) {
			mCorpusDirectory = chooser.getSelectedFile();

			File[] temp = mCorpusDirectory.listFiles();
			String filename = "";
			if (temp != null && temp.length > 0) {
				filename = temp[0].getAbsolutePath();
			}
			mExtension = filename.substring(filename.lastIndexOf("."), filename.length());
			mLablePath.setText(mCorpusDirectory.getAbsolutePath());

			if (milestoneNumber == MILESTONE_1) {
				mButtonIndex.setEnabled(true);
			} else if (milestoneNumber == MILESTONE_2) {
				mButtonDiskIndex.setEnabled(true);
				enableButtons(MILESTONE_2);
			}
			mButtonReset.setEnabled(true);

			if (mExtension.equalsIgnoreCase(".txt")) {
				mDocumentCorpus = DirectoryCorpus
						.loadTextDirectory(Paths.get(mCorpusDirectory.getAbsolutePath()).toAbsolutePath(), mExtension);
			} else {
				mDocumentCorpus = DirectoryCorpus
						.loadJsonDirectory(Paths.get(mCorpusDirectory.getAbsolutePath()).toAbsolutePath(), mExtension);

			}
		}
	}

	/**
	 * Enable buttons after a particular operation.
	 *
	 * @param milestoneNumber According to the milestone number provided, buttons
	 *                        will be enabled.
	 */
	private void enableButtons(int milestoneNumber) {
		if (milestoneNumber == MILESTONE_1) {
			mButtonStem.setEnabled(true);
			mButtonVocab.setEnabled(true);
			mButtonSearch.setEnabled(true);
		} else if (milestoneNumber == MILESTONE_2) {

			mBtnSearchDiskIndex.setEnabled(true);
			mTfIdfVariants.setEnabled(true);
		} else {
			mBtnSearchDiskIndex.setEnabled(true);
			mButtonDiskIndex.setEnabled(true);
		}
		mButtonReset.setEnabled(true);

	}

	/**
	 * Start parsing the query using Boolean Query parser.
	 */
	private void searchClick(int milestoneNumber) {

		long startTime = System.currentTimeMillis();
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(mCorpusDirectory.getName());

		String query = mTextFieldQuery.getText();
		if (milestoneNumber == MILESTONE_1) {

			QueryComponent temp = new BooleanQueryParser(mTokenProcessor).parseQuery(query);
			for (Posting p : temp.getPostings(index)) {
				DefaultMutableTreeNode tempTreeNode;
				tempTreeNode = new DefaultMutableTreeNode(mDocumentCorpus.getDocument(p.getDocumentId()).getTitle());
				top.add(tempTreeNode);
			}
		} else if (milestoneNumber == MILESTONE_2) {

			DiskPositionalIndex diskPositionalIndex = new DiskPositionalIndex(mLablePath.getText() + "\\index",
					variants.length);

			// Check if radio button for Ranked retrieval is on or for Boolean retrieval.
			if (radioRankedRetrieval.isSelected()) {

				String[] queryTerms = mTextFieldQuery.getText().split(" ");

				HashMap<String, java.util.List<Posting>> hashMapTerms = new HashMap<>();

				for (String term : queryTerms) {
					java.util.List<Posting> postings = diskPositionalIndex
							.getPostingsWithoutPositions(mTokenProcessor.processToken(term).get(0));
					hashMapTerms.put(term, postings);
				}

				VariantMethodsInterface variant = null;
				switch (mTfIdfVariants.getSelectedIndex()) {
				case 1:
					variant = new TFIDFVariant();
					break;
				case 2:
					variant = new WackyVariant();
					break;
				case 3:
					variant = new OkapiVariant();
					break;
				default:
					variant = new DefaultVariant();
					System.out.println("Default variant");
				}

				List<Posting> resultPostings = new RankedQueryParser().getRankedDocuments(hashMapTerms,
						mDocumentCorpus.getCorpusSize(), variant, mLablePath.getText() + "\\index",
						mTfIdfVariants.getSelectedIndex());

				for (Posting posting : resultPostings) {
					DefaultMutableTreeNode tempTreeNode;
					tempTreeNode = new DefaultMutableTreeNode(
							mDocumentCorpus.getDocument(posting.getDocumentId()).getTitle() + " (ID "
									+ posting.getDocumentId() + "): " + posting.getmAccumScore());
					top.add(tempTreeNode);
				}
			} else {

				QueryComponent temp = new BooleanQueryParser(mTokenProcessor).parseQuery(query);

				for (Posting p : temp.getPostings(diskPositionalIndex)) {
					DefaultMutableTreeNode tempTreeNode;
					tempTreeNode = new DefaultMutableTreeNode(
							mDocumentCorpus.getDocument(p.getDocumentId()).getTitle());
					top.add(tempTreeNode);
				}
			}

		}

		// Below is UI for setting up UI for showing the results.
		mLableInfo.setText("Logs: " + "\n\nTotal documents:" + top.getChildCount());

		mTree = new JTree(top);
		mSplitPane.setLeftComponent(new JScrollPane(mTree));
		mTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				TreePath pathForLocation = mTree.getPathForLocation(e.getPoint().x, e.getPoint().y);
				if (pathForLocation != null) {
					mSelectedFilename = pathForLocation.getLastPathComponent().toString();
				}
				try {
					BufferedReader br;
					if (mExtension.equalsIgnoreCase(".json")) {
						br = (BufferedReader) new JsonFileDocument(0,
								Paths.get(mCorpusDirectory.getAbsolutePath() + "\\" + mSelectedFilename)).getContent();
					} else {
						br = new BufferedReader(new FileReader(
								new File(mCorpusDirectory.getAbsolutePath() + "\\" + mSelectedFilename)));
					}
					mTextAreaOutput.read(br, null);
				} catch (Exception exp) {
					exp.printStackTrace();
				}
			}
		});
		long stopTime = System.currentTimeMillis();
		// Calculating total time taken for indexing.
		long elapsedTime = stopTime - startTime;
		mLableInfo.setText(mLableInfo.getText() + "\nDone Indexing." + "\n\nTime to index = "
				+ TimeUnit.MILLISECONDS.toMillis(elapsedTime) + " seconds");

	}

	/*
	 * Create in memory positional index.
	 */
	private Index indexCorpus(DocumentCorpus corpus, MultipleTokenProcessor processor, int milestoneNumber) {

		TokenStream tokenStream;
		PositionalInvertedIndex index = new PositionalInvertedIndex();

		// We will calculate LD, docLength, Bytesize and ave(tftd) for all docs and
		// insert in a same list which will be passed
		// to the writer to write it to DocWeights.
		java.util.List<Double> listForDocWeightsFile = new ArrayList<>();

		HashMap<Integer, Integer> hashMapDocLengths = new HashMap<>();
		double avgDocsLength = 0;
		for (Document d : corpus.getDocuments()) {

			HashMap<String, Integer> hashMapTFtd = new HashMap<>();

			try {
				int position = 0;
				tokenStream = new EnglishTokenStream(d.getContent());
				Iterable<String> iterableOfTokens = tokenStream.getTokens();
				for (String str : iterableOfTokens) {

					for (String indexTerm : processor.processToken(str)) {

						if (!indexTerm.trim().equals("")) {
							index.addTerm(indexTerm, d.getId(), position);

							if (milestoneNumber == MILESTONE_2 || milestoneNumber == GUIApplication.MILESTONE_3) {
								// Add tftds to hashmap, so that we can calculate LDs later.
								addToHashMapTFtd(hashMapTFtd, indexTerm);
							}
						}
					}
					position++;
				}
				tokenStream.close();

				if (milestoneNumber == MILESTONE_2 || milestoneNumber == GUIApplication.MILESTONE_3) {

					double ldForDoc = calculateLdForDoc(hashMapTFtd);
					double docLengthd = (double) position - 1.0;
					double byteSized = (double) d.getSize();
					double avgTftd = docLengthd / (double) hashMapTFtd.size();

					listForDocWeightsFile.add(ldForDoc);
					listForDocWeightsFile.add(docLengthd);
					listForDocWeightsFile.add(byteSized);
					listForDocWeightsFile.add(avgTftd);
					// Variable for av
					avgDocsLength += docLengthd;

					// Add Default, tf-idf, wacky wdts. Add Okapi later.
					for (String term : hashMapTFtd.keySet()) {

						double[] wdts = new double[variants.length];
						wdts[0] = ((double) new DefaultVariant().getWDT(hashMapTFtd.get(term), d.getId()));
						wdts[1] = ((double) new TFIDFVariant().getWDT(hashMapTFtd.get(term), d.getId()));
						wdts[2] = ((double) new WackyVariant(avgTftd).getWDT(hashMapTFtd.get(term), d.getId()));
						index.addWdt(term, d.getId(), wdts);
					}

					// Store lengths of docs. This is only useful for inserting Okapi Variant, as it
					// required doc lens and avg doc len.
					hashMapDocLengths.put(d.getId(), position - 1);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (milestoneNumber == MILESTONE_2 || milestoneNumber == GUIApplication.MILESTONE_3) {

			avgDocsLength = avgDocsLength / corpus.getCorpusSize();

			// Add docLengthA, i.e. the average number of tokens on all the docs at last
			listForDocWeightsFile.add(avgDocsLength);
			index.addWdtForOkapi(hashMapDocLengths, avgDocsLength);

			new DiskIndexWriter(mLablePath.getText() + "\\index").writeLDToDocWeights(listForDocWeightsFile);

		}
		return index;
	}

	/*
	 * This method adds or updated TFtd hashmap, which will be used to calculate
	 * document weights.
	 */
	private void addToHashMapTFtd(HashMap<String, Integer> hashMapTFtd, String indexTerm) {
		if (hashMapTFtd.containsKey(indexTerm)) {
			hashMapTFtd.put(indexTerm, hashMapTFtd.get(indexTerm) + 1);
		} else {
			hashMapTFtd.put(indexTerm, 1);
		}
	}

	/*
	 * Calculate LD for a single document.
	 */
	private double calculateLdForDoc(HashMap<String, Integer> hashMapTFtd) {

		double ld = 0.0;
		for (int tftd : hashMapTFtd.values()) {

			double wt = 1.0 + Math.log((double) tftd);
			ld += wt * wt;
		}
		return Math.sqrt(ld);

	}

}
