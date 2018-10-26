package cecs429;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.documents.JsonFileDocument;
import cecs429.index.Index;
import cecs429.index.PositionalInvertedIndex;
import cecs429.index.Posting;
import cecs429.query.BooleanQueryParser;
import cecs429.query.QueryComponent;
import cecs429.text.EnglishTokenStream;
import cecs429.text.MultipleTokenProcessor;
import cecs429.text.PorterTokenProcessor;
import cecs429.text.TokenStream;

public class GUIApplication extends JFrame {

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

    private GUIApplication() {
        setTitle("Search Engine");
        setBounds(new Rectangle(0, 0, 900, 900));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBounds(20, 20, 800, 200);
        getContentPane().add(panel);
        panel.setLayout(null);

        JLabel lblSelectCorpus = new JLabel("Select Corpus");
        lblSelectCorpus.setBounds(10, 8, 90, 14);
        panel.add(lblSelectCorpus);

        mTextFieldQuery = new JTextField();
        mTextFieldQuery.setBounds(110, 30, 788, 25);
        panel.add(mTextFieldQuery);
        mTextFieldQuery.setColumns(50);

        JLabel labelEnterQuery = new JLabel("Enter Query");
        labelEnterQuery.setBounds(10, 33, 80, 14);
        panel.add(labelEnterQuery);

        mSplitPane = new JSplitPane();
        mSplitPane.setBounds(10, 123, 854, 527);
        panel.add(mSplitPane);

        mTextAreaOutput = new JTextArea();
        mTextAreaOutput.setWrapStyleWord(true);
        mSplitPane.setRightComponent(new JScrollPane(mTextAreaOutput));
        mSplitPane.setLeftComponent(new JScrollPane(new JTextArea()));

        mLablePath = new JLabel("current mDocumentCorpus");
        mLablePath.setBounds(110, 8, 603, 14);
        panel.add(mLablePath);
        setVisible(true);

        mLableInfo = new JLabel("Logs:\n");
        mLableInfo.setBounds(10, 700, 603, 14);
        panel.add(mLableInfo);
        setVisible(true);

        JButton buttonReset = new JButton("Reset");
        buttonReset.setBounds(335 + 265, 90, 239, 23);
        panel.add(buttonReset);

        JButton buttonBrowse = new JButton("Browse");
        buttonBrowse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                chooseDirectory();
            }
        });
        buttonBrowse.setBounds(699, 4, 126, 23);
        panel.add(buttonBrowse);

        JButton buttonSearch = new JButton("Search");
        buttonSearch.setBounds(110, 90, 239, 23);
        buttonSearch.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                searchClick();
            }
        });
        panel.add(buttonSearch);

        JButton buttonVocab = new JButton("Show Vocabulary");
        buttonVocab.setBounds(110, 90-30, 239, 23);
        buttonVocab.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {


                java.util.List<String> vocabList = index.getVocabulary();
                mTextAreaOutput.setText("");
                for (int i=0;i<1000;i++) {

                    mTextAreaOutput.setText(mTextAreaOutput.getText()+"\n"+vocabList.get(i));
                }
            }
        });
        panel.add(buttonVocab);


        JButton buttonStem = new JButton("Stem");
        buttonStem.setBounds(355, 90-30, 239, 23);
        buttonStem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                mTextAreaOutput.setText("");
                mTextAreaOutput.setText(mTokenProcessor.processToken(mTextFieldQuery.getText().toString()).get(0));
            }
        });
        panel.add(buttonStem);


        JButton buttonDiskIndex = new JButton("DiskIndex");
        buttonDiskIndex.setBounds(335 + 265, 90-30, 239, 23);
        buttonDiskIndex.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Start creating disk index.
                new DiskIndexWriter( mLablePath.getText()+"\\index").writeIndex(index);
            }
        });
        panel.add(buttonDiskIndex);

        JButton buttonIndex = new JButton("Index");
        buttonIndex.setBounds(355, 90, 239, 23);
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
        panel.add(buttonIndex);


    }

    /**
     * Method to open directory selector and get all files in that folder.
     */
    private void chooseDirectory() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("C:\\Users\\KARAN\\Desktop\\Study\\4th Sem\\SET\\Projects\\SearchEngineProject\\MobyDick10Chapters"));
        chooser.setDialogTitle("Select Document Corpus");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
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
    private void searchClick() {

        DefaultMutableTreeNode top = new DefaultMutableTreeNode(mCorpusDirectory.getName());

        String query = mTextFieldQuery.getText();
        QueryComponent temp = new BooleanQueryParser(mTokenProcessor).parseQuery(query);
        for (Posting p : temp.getPostings(index)) {
            DefaultMutableTreeNode tempTreeNode;
            tempTreeNode = new DefaultMutableTreeNode(mDocumentCorpus.getDocument(p.getDocumentId()).getTitle());
            top.add(tempTreeNode);

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

    private static Index indexCorpus(DocumentCorpus corpus, MultipleTokenProcessor processor) {

        TokenStream tokenStream;
        PositionalInvertedIndex index = new PositionalInvertedIndex();
        for (Document d : corpus.getDocuments()) {
            try {
                int position = 0;
                tokenStream = new EnglishTokenStream(d.getContent());
                Iterable<String> iterableOfTokens = tokenStream.getTokens();
                for (String str : iterableOfTokens) {

                    for (String indexTerm : processor.processToken(str)) {
                        if (!indexTerm.trim().equals("")) {
                            index.addTerm(indexTerm, d.getId(), position);
                        }
                    }
                    position++;
                }
                tokenStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return index;
    }

    public static void main(String[] args) {
        new GUIApplication();
    }
}
