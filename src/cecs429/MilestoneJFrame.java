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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static cecs429.GUIApplication.MILESTONE_1;
import static cecs429.GUIApplication.MILESTONE_2;

class MilestoneJFrame extends JFrame {

    private String mExtension;
    private String mSelectedFilename;

    private JFrame mFromJFrame;
    private JSplitPane mSplitPane;
    private JTree mTree;
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
    private JComboBox mTfIdfVariants;

    private DocumentCorpus mDocumentCorpus;
    private MultipleTokenProcessor mTokenProcessor;
    private Index index;

    /**
     * Constructor for Milestone JFrame.
     *
     * @param milestoneNumber Milestone differentiator.
     * @param fromJFrame      From which this (current) framework has opened.
     */
    MilestoneJFrame(int milestoneNumber, JFrame fromJFrame) {
        mFromJFrame = fromJFrame;
        initializeMilestone(milestoneNumber);
    }

    private void initializeMilestone(int milestoneNumber) {
        mTokenProcessor = new PorterTokenProcessor();
        if (milestoneNumber == MILESTONE_1) {
            setTitle("Milestone 1");
        } else if (milestoneNumber == MILESTONE_2) {
            setTitle("Milestone 2");
        }
        setBounds(new Rectangle(0, 0, 900, 900));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mJPanel = new JPanel();
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
            Image newimg = ImageIO.read(getClass().getResource("resources/back.png")).getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH);
            btnBack.setIcon(new ImageIcon(newimg));
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

            mButtonIndex = new JButton("Index");
            mButtonIndex.setBounds(335 + 265, 90 - 30 + 50, 239, 23);
            mButtonIndex.setEnabled(false);
            mButtonIndex.addActionListener(e -> {

                // After clicking index button, get current time before indexing and get time after indexing.
                long startTime = System.currentTimeMillis();

                index = indexCorpus(mDocumentCorpus, mTokenProcessor);
                long stopTime = System.currentTimeMillis();

                // Calculating total time taken for indexing.
                long elapsedTime = stopTime - startTime;
                mLableInfo.setText(mLableInfo.getText() + "\nDone Indexing." + "\n\nTime to index = " + TimeUnit.MILLISECONDS.toSeconds(elapsedTime) + " seconds");
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

        } else if (milestoneNumber == MILESTONE_2) {

            mButtonDiskIndex = new JButton("DiskIndex");
            mButtonDiskIndex.setBounds(335 + 265, 90 - 30 + 50, 239, 23);
            mButtonDiskIndex.setEnabled(false);
            mButtonDiskIndex.addActionListener(e -> {

                // After clicking index button, get current time before indexing and get time after indexing.
                long startTime = System.currentTimeMillis();
                index = indexCorpus(mDocumentCorpus, mTokenProcessor);
                long stopTime = System.currentTimeMillis();

                // Calculating total time taken for indexing.
                long elapsedTime = stopTime - startTime;
                mLableInfo.setText(mLableInfo.getText() + "\nDone Indexing." + "\n\nTime to index = " + TimeUnit.MILLISECONDS.toMillis(elapsedTime) + " seconds");

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

            JRadioButton radioRankedRetrieval = new JRadioButton();
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

            String[] variants = {"Default", "tf-idf", "Okapi BM25", "Wacky"};

            //Create the combo box, select item at index 4.
            //Indices start at 0, so 4 specifies the pig.
            mTfIdfVariants = new JComboBox(variants);
            mTfIdfVariants.setBounds(110, 90 + 50, 239, 23);
            mTfIdfVariants.setSelectedIndex(0);
            mTfIdfVariants.setEnabled(false);
            mJPanel.add(mTfIdfVariants);

        }
        setVisible(true);
    }

    /**
     * Method to open directory selector and get all files in that folder.
     */
    private void chooseDirectory(JFrame jFrame, int milestoneNumber) {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("C:\\Users\\KARAN\\Desktop\\Study\\4th Sem\\SET\\Projects\\SearchEngineProject\\MobyDick10Chapters"));
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
                mDocumentCorpus = DirectoryCorpus.loadTextDirectory(Paths.get(mCorpusDirectory.getAbsolutePath()).toAbsolutePath(),
                        mExtension);
            } else {
                mDocumentCorpus = DirectoryCorpus.loadJsonDirectory(Paths.get(mCorpusDirectory.getAbsolutePath()).toAbsolutePath(),
                        mExtension);

            }
        }
    }

    /**
     * Enable buttons after a particular operation.
     *
     * @param milestoneNumber According to the milestone number provided, buttons will be enabled.
     */
    private void enableButtons(int milestoneNumber) {
        if (milestoneNumber == MILESTONE_1) {
            mButtonStem.setEnabled(true);
            mButtonVocab.setEnabled(true);
            mButtonSearch.setEnabled(true);
        } else if (milestoneNumber == MILESTONE_2) {

            mBtnSearchDiskIndex.setEnabled(true);
            mTfIdfVariants.setEnabled(true);
//            new DiskPositionalIndex(mLablePath.getText()+"\\index").readAllLDs();
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


            // @TODO: Query with multiple terms.

            String[] queryTerms = mTextFieldQuery.getText().split(" ");

            HashMap<String, java.util.List<Posting>> hashMapTerms = new HashMap<>();

            for (String term : queryTerms) {
                java.util.List<Posting> postings = new ArrayList<>();
                postings = new DiskPositionalIndex(mLablePath.getText() + "\\index").getPostingsWithoutPositions(mTokenProcessor.processToken(term).get(0));
                hashMapTerms.put(term, postings);
            }

            VariantMethodsInterface variant = null;
            switch (mTfIdfVariants.getSelectedIndex()) {
                case 1:
                    variant = new TFIDFVariant(mLablePath.getText() + "\\index");
                    break;
                case 2:
                    variant = new OkapiVariant(mLablePath.getText() + "\\index");
                    break;
                case 3:
                    variant = new WackyVariant(mLablePath.getText() + "\\index");
                    break;
                default:
                    variant = new DefaultVariant(mLablePath.getText() + "\\index");
                    System.out.println("Default variant");

            }

            List<Posting> resultPostings = new RankedQueryParser().getRankedDocuments(hashMapTerms, mDocumentCorpus.getCorpusSize(), variant);

            for (Posting posting : resultPostings) {
                DefaultMutableTreeNode tempTreeNode;
                tempTreeNode = new DefaultMutableTreeNode(mDocumentCorpus.getDocument(posting.getDocumentId()).getTitle()+" (ID "+posting.getDocumentId()+"): "+posting.getmAccumScore());
                top.add(tempTreeNode);
            }
        }

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
                        br = new BufferedReader(
                                new FileReader(new File(mCorpusDirectory.getAbsolutePath() + "\\" + mSelectedFilename)));
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
        mLableInfo.setText(mLableInfo.getText() + "\nDone Indexing." + "\n\nTime to index = " + TimeUnit.MILLISECONDS.toMillis(elapsedTime) + " seconds");

    }

    private Index indexCorpus(DocumentCorpus corpus, MultipleTokenProcessor processor) {

        TokenStream tokenStream;
        PositionalInvertedIndex index = new PositionalInvertedIndex();

        // We will calculate LD, docLength, Bytesize and ave(tftd) for all docs and insert in a same list which will be passed
        // to the writer to write it to DocWeights.
        java.util.List<Double> listForDocWeightsFile = new ArrayList<>();

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
                            addToHashMapTFtd(hashMapTFtd, indexTerm);
                        }
                    }
                    position++;
                }
                tokenStream.close();

                Double ldForDoc = calculateLdForDoc(hashMapTFtd);
                Double docLengthd = (double) position - 1.0;
                Double byteSized = (double) d.getSize();
                Double avgTftd = docLengthd / (double) hashMapTFtd.size();

                listForDocWeightsFile.add(ldForDoc);
                listForDocWeightsFile.add(docLengthd);
                listForDocWeightsFile.add(byteSized);
                listForDocWeightsFile.add(avgTftd);
                // Variable for av
                avgDocsLength += docLengthd;

                System.out.println("LD (Doc " + d.getId() + ") : " + ldForDoc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        avgDocsLength = avgDocsLength / corpus.getCorpusSize();

        // Add docLengthA, i.e. the average number of tokens on all the docs at last
        listForDocWeightsFile.add(avgDocsLength);
        new DiskIndexWriter(mLablePath.getText() + "\\index").writeLDToDocWeights(listForDocWeightsFile);

        return index;
    }

    /*
    This method adds or updated TFtd hashmap, which will be used to calculate document weights.
     */
    private void addToHashMapTFtd(HashMap<String, Integer> hashMapTFtd, String indexTerm) {
        if (hashMapTFtd.containsKey(indexTerm)) {
            hashMapTFtd.put(indexTerm, hashMapTFtd.get(indexTerm) + 1);
        } else {
            hashMapTFtd.put(indexTerm, 1);
        }
    }

    /*
    Calculate LD for a single document.
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
