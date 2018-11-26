package cecs429;

import cecs429.index.Index;
import cecs429.index.Posting;
import jdbm.PrimaryTreeMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;

import java.io.*;
import java.util.List;

public class DiskIndexWriter {

    private String mPath;

    public DiskIndexWriter(String path) {
        mPath = path;
    }

    public void writeIndex(Index index) {

        // Create postings.bin and return a Data structure that will contain byte position(a long) of each term
        // in the vocabulary begins in postings file.
        List<String> vocabularyWords = index.getVocabulary();

        // As we know the size of the array, we are selecting primitive types instead of ArrayList.
        long[] postingsPositions = new long[vocabularyWords.size()];
        createPostingsBin(index, vocabularyWords, postingsPositions);

        long[] vocabularyPositions = new long[vocabularyWords.size()];
        // Create vocab.bin and should also return the byte position of each term.
        createVocabBin(vocabularyWords, vocabularyPositions);

        // Create vocabTable.bin, which will contain both byte positions of postings and term.
        // createVocabTableBin(vocabularyPositions, postingsPositions);
        createBPlusTree(vocabularyWords, postingsPositions);
    }

    private void createBPlusTree(List<String> vocabularyWords, long[] postingsPositions) {

        // create (or open existing) database
        File bPlusTreeFile = new File(mPath + File.separator + "bplusttree" + File.separator + "SET_BPlusTree");
        bPlusTreeFile.getParentFile().mkdirs(); // Create folders if does not exist.
        String fileName = bPlusTreeFile.getPath();
        RecordManager recMan = null;
        try {
            recMan = RecordManagerFactory.createRecordManager(fileName);

            // Creates TreeMap which stores data in database.
            // Constructor method takes recordName (something like SQL table name)
            String recordName = "firstTreeMap";
            PrimaryTreeMap<String, Long> treeMap = recMan.treeMap(recordName);

            // add some stuff to map
            for (int i = 0; i < vocabularyWords.size(); i++) {
                treeMap.put(vocabularyWords.get(i), postingsPositions[i]);
            }

            System.out.println(treeMap.keySet());

            // Map changes are not persisted yet, commit them (save to disk) */
            recMan.commit();

            // close record manager
            recMan.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createPostingsBin(Index index, List<String> vocabularyWords, long[] postingsPositions) {
        DataOutputStream dataOutputStream = null;
        try {
            File postingsBinFile = new File(mPath + File.separator + "postings.bin");
            postingsBinFile.getParentFile().mkdirs(); // Create folders if does not exist.

            FileOutputStream file = new FileOutputStream(postingsBinFile); // append : false.
            dataOutputStream = new DataOutputStream(file);
            for (int i = 0; i < vocabularyWords.size(); i++) {

                List<Posting> termPostings = index.getPostingsWithPositions(vocabularyWords.get(i));
                postingsPositions[i] = dataOutputStream.size();

                // Write dft: doc frequency.
                dataOutputStream.writeInt(termPostings.size());

                for (int j = 0; j < termPostings.size(); j++) {

                    if (j == 0) {// Write doc id. (with gaps)

                        // If it is first doc, then insert as it is.
                        dataOutputStream.writeInt(termPostings.get(j).getDocumentId());
                    } else {// else insert gap.

                        dataOutputStream.writeInt(termPostings.get(j).getDocumentId() - termPostings.get(j - 1).getDocumentId());
                    }

                    // Write wdts dynamically. Dynamically means, any number of variants could be passed.
                    for (int z = 0; z < termPostings.get(j).getmWdts().length; z++) {
                        dataOutputStream.writeDouble(termPostings.get(j).getmWdts()[z]);
                    }

                    dataOutputStream.writeInt(termPostings.get(j).getPositions().size());// Write term frequency.

                    for (int k = 0; k < termPostings.get(j).getPositions().size(); k++) {// Write positions.
                        dataOutputStream.writeInt(termPostings.get(j).getPositions().get(k));
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                closeDataOutputStream(dataOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void createVocabBin(List<String> vocabularyWords, long[] vocabularyPositions) {
        DataOutputStream dataOutputStream = null;
        try {
            File vocabBinFile = new File(mPath + File.separator + "vocab.bin");
            vocabBinFile.getParentFile().mkdirs(); // Create folders if does not exist.

            FileOutputStream file = new FileOutputStream(vocabBinFile);
            dataOutputStream = new DataOutputStream(file);

            int currentVocabPosition = 0;
            for (int i = 0; i < vocabularyWords.size(); i++) {
                String currentWord = vocabularyWords.get(i);
                vocabularyPositions[i] = currentVocabPosition;
                dataOutputStream.writeBytes(currentWord);
                currentVocabPosition = currentVocabPosition + currentWord.length();

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                closeDataOutputStream(dataOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Write LD into 'docWeights.bin' file.
     *
     * @param ld List of Doubles, which has LDs of every document.
     */
    void writeLDToDocWeights(List<Double> ld) {

        DataOutputStream dataOutputStream = null;

        try {
            File postingsBinFile = new File(mPath + File.separator + "docWeights.bin");
            postingsBinFile.getParentFile().mkdirs(); // Create folders if does not exist.
            FileOutputStream file = null;
            file = new FileOutputStream(postingsBinFile, true);
            dataOutputStream = new DataOutputStream(file);

            for (Double aLd : ld) {

                dataOutputStream.writeDouble(aLd);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                closeDataOutputStream(dataOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeDataOutputStream(DataOutputStream dataOutputStream) throws IOException {
        if (dataOutputStream != null) {
            dataOutputStream.flush();
            dataOutputStream.close();
        }
    }

    /*
    This method is used to save Vocabulary of a corpus onto a disk. We are using it if we use Binary Search on the Vocab.
     */
    private void createVocabTableBin(long[] vocabularyPositions, long[] postingsPositions) {

        DataOutputStream dataOutputStream = null;
        try {
            File vocabBinFile = new File(mPath + File.separator + "vocabTable.bin");
            vocabBinFile.getParentFile().mkdirs(); // Create folders if does not exist.

            FileOutputStream file = new FileOutputStream(vocabBinFile); // append : false.
            dataOutputStream = new DataOutputStream(file);

            for (int i = 0; i < vocabularyPositions.length; i++) {
                dataOutputStream.writeLong(vocabularyPositions[i]);
                dataOutputStream.writeLong(postingsPositions[i]);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                closeDataOutputStream(dataOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
