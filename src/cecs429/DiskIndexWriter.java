package cecs429;

import cecs429.index.Index;
import cecs429.index.PositionalInvertedIndex;
import cecs429.index.Posting;

import java.io.*;
import java.util.List;

public class DiskIndexWriter {

    private String mPath;

    public DiskIndexWriter(String path){
        mPath = path;
    }

    public void writeIndex(Index index) {

        // Create postings.bin and return a Data structure that will contain byte position(a long) of each term
        // in the vocabulary begins in postings file.

        List<String> vocabularyWords = index.getVocabulary();
        long[] postingsPositions = new long[vocabularyWords.size()];

        createPostingsBin(index, vocabularyWords, postingsPositions);

        long[] vocabularyPositions = new long[vocabularyWords.size()];
        // Create vocab.bin and should also return the byte position of each term.
        createVocabBin(vocabularyWords, vocabularyPositions);

        // Create vocabTable.bin, which will contain both byte positions of postings and term.
        createVocabTableBin(vocabularyPositions, postingsPositions);
    }

    private void createPostingsBin(Index index, List<String> vocabularyWords, long[] postingsPositions) {
        DataOutputStream dataOutputStream = null;
        try {
            File postingsBinFile = new File(mPath + File.separator + "postings.bin");
            postingsBinFile.getParentFile().mkdirs(); // Create folders if does not exist.

            FileOutputStream file = new FileOutputStream(postingsBinFile); // append : false.
            dataOutputStream = new DataOutputStream(file);
            int currentPostingPosition = 0;
            for(int i=0;i<vocabularyWords.size();i++) {

                List<Posting> termPostings = index.getPostings(vocabularyWords.get(i));

                postingsPositions[i] = dataOutputStream.size();
                // Write dft: doc frequency.
                dataOutputStream.writeInt(termPostings.size());

                for(int j=0;j<termPostings.size();j++) {
                    // Write doc id.
                    dataOutputStream.writeInt(termPostings.get(j).getDocumentId());

                    // Write term frequency.
                    dataOutputStream.writeInt(termPostings.get(j).getPositions().size());

                    // Write positions.
                    for(int k=0;k<termPostings.get(j).getPositions().size();k++) {
                        dataOutputStream.writeInt(termPostings.get(j).getPositions().get(k));
                    }
                }

//
//                if(i<5) {
//
//                    System.out.println("postings.bin: --------");
//                    System.out.println("Word - "+vocabularyWords.get(i)+" : position - "+postingsPositions[i]);
//                }
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

    private void closeDataOutputStream(DataOutputStream dataOutputStream) throws IOException {
        if (dataOutputStream != null) {
            dataOutputStream.flush();
            dataOutputStream.close();
        }
    }

    private void createVocabBin(List<String> vocabularyWords, long[] vocabularyPositions) {
        DataOutputStream dataOutputStream = null;
        try {
            File vocabBinFile = new File(mPath + File.separator + "vocab.bin");
            vocabBinFile.getParentFile().mkdirs(); // Create folders if does not exist.

            FileOutputStream file = new FileOutputStream(vocabBinFile); // append : false.
            dataOutputStream = new DataOutputStream(file);

            int currentVocabPosition = 0;
            for (int i = 0; i < vocabularyWords.size(); i++) {
                String currentWord = vocabularyWords.get(i);
                vocabularyPositions[i] = currentVocabPosition;
//                if(i<5) {
//
//                    System.out.println("vocab.bin: --------");
//                    System.out.println("Word - "+currentWord+" : position - "+currentVocabPosition);
//                }
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



    private void createVocabTableBin(long[] vocabularyPositions, long[] postingsPositions) {
        DataOutputStream dataOutputStream = null;
        try {
            File vocabBinFile = new File(mPath + File.separator + "vocabTable.bin");
            vocabBinFile.getParentFile().mkdirs(); // Create folders if does not exist.

            FileOutputStream file = new FileOutputStream(vocabBinFile); // append : false.
            dataOutputStream = new DataOutputStream(file);

            for(int i=0;i<vocabularyPositions.length;i++) {
                dataOutputStream.writeLong(vocabularyPositions[i]);
                dataOutputStream.writeLong(postingsPositions[i]);

//                if(i<5) {
//
//                    System.out.println("vocabTable.bin: --------");
//                    System.out.println("vocabularyPositions - "+vocabularyPositions[i]+" : postingsPositions - "+postingsPositions[i]);
//                }
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
