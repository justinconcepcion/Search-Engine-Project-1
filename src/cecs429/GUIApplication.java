package cecs429;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

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
import cecs429.text.EnglishTokenStream;
import cecs429.text.MultipleTokenProcessor;
import cecs429.text.PorterTokenProcessor;
import cecs429.text.TokenStream;

public class GUIApplication {

    private JFrame mJFrameMilestone1;
    private JFrame mJFrameMain;
    private String mExtension;
    private String mSelectedFilename;

    private JSplitPane mSplitPane;
    private JTree mTree;
    private JLabel mLablePath;
    private JLabel mLableInfo;
    private File mCorpusDirectory;
    private JTextField mTextFieldQuery;
    private JTextArea mTextAreaOutput;

    private DocumentCorpus mDocumentCorpus;
    private MultipleTokenProcessor mTokenProcessor;
    private Index index;

    private static final int MILESTONE_1 = 1;
    private static final int MILESTONE_2 = 2;
    private static final int MILESTONE_3 = 3;

    private GUIApplication() {

        initlizeMainContent();


    }

    private void initlizeMainContent() {

        mJFrameMain = new JFrame("Search Engine");
        mJFrameMain.setBounds(new Rectangle(0, 0, 900, 900));
        mJFrameMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel firstPanel = new JPanel();
        firstPanel.setLayout(new GridLayout(3, 1, 10, 10));
        firstPanel.setMaximumSize(new Dimension(600, 200));

        JButton btnMilestone1 = new JButton();
        btnMilestone1.setText("Milestone 1");
        btnMilestone1.add(Box.createRigidArea(new Dimension(5, 0)));
        firstPanel.add(btnMilestone1);

        btnMilestone1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                mJFrameMain.setVisible(false);
                initializeMilestone(MILESTONE_1);
            }
        });

        JButton btnMilestone2 = new JButton();
        btnMilestone2.setText("Milestone 2");
        btnMilestone2.add(Box.createRigidArea(new Dimension(5, 0)));
        firstPanel.add(btnMilestone2);

        btnMilestone2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                mJFrameMain.setVisible(false);
                initializeMilestone(MILESTONE_2);
            }
        });

        JButton btnMilestone3 = new JButton();
        btnMilestone3.setText("Milestone 3");
        btnMilestone3.add(Box.createRigidArea(new Dimension(5, 0)));
        firstPanel.add(btnMilestone3);
        mainPanel.add(firstPanel);

        mJFrameMain.setContentPane(mainPanel);

        mJFrameMain.setSize(520, 600);
        mJFrameMain.setMinimumSize(new Dimension(520, 600));
        mJFrameMain.setVisible(true);
    }

    private void initializeMilestone(int milestoneNumber) {
        mJFrameMilestone1 = new JFrame("Milestone 1");
        mJFrameMilestone1.setBounds(new Rectangle(0, 0, 900, 900));
        mJFrameMilestone1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panelMilestone1 = new JPanel();
        panelMilestone1.setBounds(20, 20, 800, 200);
        mJFrameMilestone1.getContentPane().add(panelMilestone1);
        panelMilestone1.setLayout(null);

        JLabel lblSelectCorpus = new JLabel("Select Corpus");
        lblSelectCorpus.setBounds(10, 8 + 50, 90, 14);
        panelMilestone1.add(lblSelectCorpus);

        mLablePath = new JLabel("current mDocumentCorpus");
        mLablePath.setBounds(110, 8 + 50, 603, 14);
        panelMilestone1.add(mLablePath);

        JButton buttonBrowse = new JButton("Browse");
        buttonBrowse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                chooseDirectory(mJFrameMilestone1);
            }
        });
        buttonBrowse.setBounds(699, 4 + 50, 126, 23);
        panelMilestone1.add(buttonBrowse);


        JButton btnBack = new JButton();
        try {
            Image newimg = ImageIO.read(getClass().getResource("resources/back.png")).getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH);
            btnBack.setIcon(new ImageIcon(newimg));
        } catch (Exception ex) {
            System.out.println(ex);
        }
        btnBack.setBounds(10, 10, 40, 40);
        panelMilestone1.add(btnBack);

        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mJFrameMilestone1.setVisible(false);
                initlizeMainContent();
            }
        });

        JLabel labelEnterQuery = new JLabel("Enter Query");
        labelEnterQuery.setBounds(10, 33 + 50, 80, 14);
        panelMilestone1.add(labelEnterQuery);

        mTextFieldQuery = new JTextField();
        mTextFieldQuery.setBounds(110, 30 + 50, 788, 25);
        panelMilestone1.add(mTextFieldQuery);
        mTextFieldQuery.setColumns(50);

        mLableInfo = new JLabel("Logs:\n");
        mLableInfo.setBounds(10, 700 + 50, 603, 14);
        panelMilestone1.add(mLableInfo);

        JButton buttonReset = new JButton("Reset");
        buttonReset.setBounds(335 + 265, 90 + 50, 239, 23);
        panelMilestone1.add(buttonReset);

        if (milestoneNumber == MILESTONE_1) {

            JButton buttonIndex = new JButton("Index");
            buttonIndex.setBounds(355, 90 + 50, 239, 23);
            buttonIndex.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    // After clicking index button, get current time before indexing and get time after indexing.
                    long startTime = System.currentTimeMillis();
                    if (mExtension.equalsIgnoreCase(".txt")) {
                        mDocumentCorpus = DirectoryCorpus.loadTextDirectory(Paths.get(mCorpusDirectory.getAbsolutePath()).toAbsolutePath(),
                                mExtension);
                    } else {
                        mDocumentCorpus = DirectoryCorpus.loadJsonDirectory(Paths.get(mCorpusDirectory.getAbsolutePath()).toAbsolutePath(),
                                mExtension);

                    }
                    mTokenProcessor = new PorterTokenProcessor();

                    index = indexCorpus(mDocumentCorpus, mTokenProcessor);
                    long stopTime = System.currentTimeMillis();

                    // Calculating total time taken for indexing.
                    long elapsedTime = stopTime - startTime;
                    mLableInfo.setText(mLableInfo.getText() + "\nDone Indexing." + "\n\nTime to index = " + TimeUnit.MILLISECONDS.toSeconds(elapsedTime) + " seconds");

                }
            });
            panelMilestone1.add(buttonIndex);

            mSplitPane = new JSplitPane();
            mSplitPane.setBounds(10, 123 + 50, 854, 527);
            panelMilestone1.add(mSplitPane);

            mTextAreaOutput = new JTextArea();
            mTextAreaOutput.setWrapStyleWord(true);
            mSplitPane.setRightComponent(new JScrollPane(mTextAreaOutput));
            mSplitPane.setLeftComponent(new JScrollPane(new JTextArea()));

            JButton buttonSearch = new JButton("Search");
            buttonSearch.setBounds(110, 90 + 50, 239, 23);
            buttonSearch.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    searchClick(MILESTONE_1);
                }
            });
            panelMilestone1.add(buttonSearch);

            JButton buttonVocab = new JButton("Show Vocabulary");
            buttonVocab.setBounds(110, 90 - 30 + 50, 239, 23);
            buttonVocab.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {


                    java.util.List<String> vocabList = index.getVocabulary();
                    mTextAreaOutput.setText("");
                    for (int i = 0; i < 1000; i++) {

                        mTextAreaOutput.setText(mTextAreaOutput.getText() + "\n" + vocabList.get(i));
                    }
//                    mTextAreaOutput.setText(vocabList.size()+"");
                }
            });
            panelMilestone1.add(buttonVocab);


            JButton buttonStem = new JButton("Stem");
            buttonStem.setBounds(355, 90 - 30 + 50, 239, 23);
            buttonStem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    mTextAreaOutput.setText("");
                    mTextAreaOutput.setText(mTokenProcessor.processToken(mTextFieldQuery.getText().toString()).get(0));
                }
            });
            panelMilestone1.add(buttonStem);

        } else if (milestoneNumber == MILESTONE_2) {

            JButton buttonDiskIndex = new JButton("DiskIndex");
            buttonDiskIndex.setBounds(335 + 265, 90 - 30 + 50, 239, 23);
            buttonDiskIndex.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    // After clicking index button, get current time before indexing and get time after indexing.
                    long startTime = System.currentTimeMillis();
                    if (mExtension.equalsIgnoreCase(".txt")) {
                        mDocumentCorpus = DirectoryCorpus.loadTextDirectory(Paths.get(mCorpusDirectory.getAbsolutePath()).toAbsolutePath(),
                                mExtension);
                    } else {
                        mDocumentCorpus = DirectoryCorpus.loadJsonDirectory(Paths.get(mCorpusDirectory.getAbsolutePath()).toAbsolutePath(),
                                mExtension);

                    }
                    mTokenProcessor = new PorterTokenProcessor();

                    index = indexCorpus(mDocumentCorpus, mTokenProcessor);
                    long stopTime = System.currentTimeMillis();

                    // Calculating total time taken for indexing.
                    long elapsedTime = stopTime - startTime;
                    mLableInfo.setText(mLableInfo.getText() + "\nDone Indexing." + "\n\nTime to index = " + TimeUnit.MILLISECONDS.toSeconds(elapsedTime) + " seconds");

                    // Start creating disk index.
                    new DiskIndexWriter(mLablePath.getText() + "\\index").writeIndex(index);
                }
            });
            panelMilestone1.add(buttonDiskIndex);


            mSplitPane = new JSplitPane();
            mSplitPane.setBounds(10, 123 + 50, 854, 527);
            panelMilestone1.add(mSplitPane);

            mTextAreaOutput = new JTextArea();
            mTextAreaOutput.setWrapStyleWord(true);
            mSplitPane.setRightComponent(new JScrollPane(mTextAreaOutput));
            mSplitPane.setLeftComponent(new JScrollPane(new JTextArea()));

            JButton btnSearchDiskIndex = new JButton("SearchFromDiskIndex");
            btnSearchDiskIndex.setBounds(335, 90 - 30 + 50, 239, 23);
            btnSearchDiskIndex.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    searchClick(MILESTONE_2);
                }
            });
            panelMilestone1.add(btnSearchDiskIndex);

        }

        mJFrameMilestone1.setVisible(true);
    }

    /**
     * Method to open directory selector and get all files in that folder.
     */
    private void chooseDirectory(JFrame jFrame) {
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
        }

    }

    /**
     * Start parsing the query using Boolean Query parser.
     */
    private void searchClick(int milestoneNumber) {

        DefaultMutableTreeNode top = new DefaultMutableTreeNode(mCorpusDirectory.getName());

        String query = mTextFieldQuery.getText();
        if(milestoneNumber == MILESTONE_1) {

            QueryComponent temp = new BooleanQueryParser(mTokenProcessor).parseQuery(query);
            for (Posting p : temp.getPostings(index)) {
                DefaultMutableTreeNode tempTreeNode;
                tempTreeNode = new DefaultMutableTreeNode(mDocumentCorpus.getDocument(p.getDocumentId()).getTitle());
                top.add(tempTreeNode);
            }
        } else if(milestoneNumber == MILESTONE_2){

            java.util.List<Posting> postings = new DiskPositionalIndex(mLablePath.getText() + "\\index").getPostingsWithPositions(mTextFieldQuery.getText());

            for (int i=0;i<postings.size();i++) {
                DefaultMutableTreeNode tempTreeNode;
                tempTreeNode = new DefaultMutableTreeNode(mDocumentCorpus.getDocument(postings.get(i).getDocumentId()).getTitle());
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

    }

    private Index indexCorpus(DocumentCorpus corpus, MultipleTokenProcessor processor) {

        TokenStream tokenStream;
        PositionalInvertedIndex index = new PositionalInvertedIndex();
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


                double ld = calculateLdForDoc(hashMapTFtd);
                new DiskIndexWriter(mLablePath.getText() + "\\index").writeLDToDocWeights(ld);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return index;
    }


    private double calculateLdForDoc(HashMap<String, Integer> hashMapTFtd) {

        double ld = 0;
        for (int tftd : hashMapTFtd.values()) {

            double wt = 1 + Math.log(tftd);
            ld += wt * wt;
        }
        return Math.sqrt(ld);

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

    public static void main(String[] args) {
        new GUIApplication();
    }
}
