package cecs429.index;

import jdbm.PrimaryTreeMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DiskPositionalIndex implements Index {

    private String mPath;
    private RandomAccessFile mVocabTableFile;
    private RandomAccessFile mPostingsFile;
    private RandomAccessFile mVocabFile;

    // @TODO: Ask professor, if reading docWeights file here is ok?
    private RandomAccessFile mDocWeightsFile;

    public DiskPositionalIndex(String path) {

        mPath = path;
        try {

//            mVocabTableFile = new RandomAccessFile(new File(mPath + File.separator + "vocabTable.bin"), "r");
            mPostingsFile = new RandomAccessFile(new File(mPath + File.separator + "postings.bin"), "r");
            mVocabFile = new RandomAccessFile(new File(mPath + File.separator + "vocab.bin"), "r");
            mDocWeightsFile = new RandomAccessFile(new File(mPath + File.separator + "docWeights.bin"), "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<Posting> getPostingsWithPositions(String term) {

        long bytePostionOfPosting = getBytePositionFromVocabTable(term);

        System.out.println("position of a term : " + bytePostionOfPosting);
        // Get postings From ByteLocation.
        if (bytePostionOfPosting != -1) {

            return getPostingsFromByteLocation(bytePostionOfPosting, true);
        }

        return null;
    }

    @Override
    public List<Posting> getPostingsWithoutPositions(String term) {


        long bytePostionOfPosting = getBytePositionFromVocabTable(term);

        // Get postings From ByteLocation.
        if (bytePostionOfPosting != -1) {

            return getPostingsFromByteLocation(bytePostionOfPosting, false);
        }
        return null;
    }

    private List<Posting> getPostingsFromByteLocation(long bytePostionOfPosting, boolean withPositions) {
        List<Posting> postingList = null;
        try {
            postingList = new ArrayList<>();
            mPostingsFile.seek(bytePostionOfPosting);

            int dft = mPostingsFile.readInt();

            // @TODO: Remove Sysouts
            System.out.println("dft: " + dft);
            for (int i = 0; i < dft; i++) {

                int docId = mPostingsFile.readInt();
                // Add gaps to the doc ID if its not the first doc.
                if (i != 0) {
                    docId += postingList.get(postingList.size() - 1).getDocumentId();
                }
                Posting newPosting = new Posting(docId);
//                System.out.println("docId: "+docId);
                // Term frequency loop.
                int tftd = mPostingsFile.readInt();
                newPosting.setTftd(tftd);

//                System.out.println("tftd: "+tftd);
                if (withPositions) {

                    for (int j = 0; j < tftd; j++) {
                        int position = mPostingsFile.readInt();
                        newPosting.addPosition(position);
//                        System.out.println("position: "+position);
                    }
                } else {
                    // If its without postings then, seek directly to the next postings.
                    mPostingsFile.seek(mPostingsFile.getChannel().position() + (tftd * 4));
                }

                postingList.add(newPosting);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return postingList;
    }

    private long getBytePositionFromVocabTable(String term) {

        RecordManager recMan = null;
        try {
            recMan = RecordManagerFactory.createRecordManager(mPath + File.separator + "bplusttree" + File.separator + "SET_BPlusTree");

            /** Creates TreeMap which stores data in database.
             *  Constructor method takes recordName (something like SQL table name)*/
            String recordName = "firstTreeMap";
            PrimaryTreeMap<String, Long> treeMap = recMan.treeMap(recordName);

            System.out.println(treeMap.keySet());
            // > [1, 2, 3]

            long postingsLocation = treeMap.get(term);

            /** close record manager */
            recMan.close();

            return postingsLocation;
        } catch (IOException e) {
            e.printStackTrace();
        }


        // @TODO: Binary search. Above is the B Plus Tree implementation.

//         Binary Search vocabTable to locate postings.
//        try {
//
//            long l = 0;
//            // Get length considering -byte integers stored. Jump to middle one
//            long r = mVocabTableFile.length() / 16;
//
//            return binarySearch(l, r, term);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        return -1;
    }

    private long binarySearch(long l, long r, String term) {
        if (r >= l) {
            long mid = l + (r - l) / 2;

            // If the element is present at the
            // middle itself

            try {
                // Seek to middle.
                mVocabTableFile.seek(mid * 16);
                long bytePositionOfMiddleTerm = mVocabTableFile.readLong();
                long lengthOfMiddleTerm = -1;
                // Read next
                long nextTerm = (mid + 1) * 16;

                // If its not end of file
                if (mVocabTableFile.length() > nextTerm) {
                    // seek to next term and read position of next term to calculate length of middle term.
                    mVocabTableFile.seek(nextTerm);
                    lengthOfMiddleTerm = (mVocabTableFile.readLong()) - (bytePositionOfMiddleTerm);
                }

                // Get middle term at position and with length in vocan.bin
                String middleTerm = "";
                mVocabFile.seek(bytePositionOfMiddleTerm);
                if (lengthOfMiddleTerm == -1) {
                    // @TODO: change to string builder.


                    lengthOfMiddleTerm = mVocabFile.length() - bytePositionOfMiddleTerm;
                    byte[] buffer = new byte[(int) lengthOfMiddleTerm];
                    mVocabFile.read(buffer, 0, (int) lengthOfMiddleTerm);

                } else {
                    // if there is next term in the vocabTable.bin and vocab.bin
                    byte[] buffer = new byte[(int) lengthOfMiddleTerm];
                    mVocabFile.read(buffer, 0, (int) lengthOfMiddleTerm);
                    middleTerm = new String(buffer);
                }

                if (middleTerm.compareTo(term) == 0) {
                    // Return byte position of postings.
                    mVocabTableFile.seek((mid * 16) + 8);
                    return mVocabTableFile.readLong();
                }

                // If element is smaller than mid, then
                // it can only be present in left subarray
                if (middleTerm.compareTo(term) > 0) {
                    return binarySearch(l, mid - 1, term);
                }

                // Else the element can only be present
                // in right subarray
                return binarySearch(mid + 1, r, term);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // We reach here when element is not present
        //  in array
        return -1;
    }

    @Override
    public List<String> getVocabulary() {
        return null;
    }

    public void readAllLDs() {
        for(int i=0;i<40;i++) {

            try {
                mDocWeightsFile.seek((long)i*8);
                double ld = mDocWeightsFile.readDouble();
                System.out.println("doc: "+i+" : ld - "+ld);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public double getLd(int docId) {

        try {
            mDocWeightsFile.seek(docId * 32);
            return mDocWeightsFile.readDouble();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public double getDocLenD(int docId) {

        try {
            mDocWeightsFile.seek((docId * 32) + 8);
            return mDocWeightsFile.readDouble();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public double getDocByteSize(int docId) {

        try {
            mDocWeightsFile.seek((docId * 32) + 16);
            return mDocWeightsFile.readDouble();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public double getAvgTftd(int docId) {

        try {
            mDocWeightsFile.seek((docId * 32) + 24);
            return mDocWeightsFile.readDouble();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public double getAvgLenOfCorpus() {

        try {
            mDocWeightsFile.seek(mDocWeightsFile.length() - 8);
            return mDocWeightsFile.readDouble();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }
}
