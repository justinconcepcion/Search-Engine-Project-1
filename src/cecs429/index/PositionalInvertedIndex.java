package cecs429.index;

import cecs429.variantmethods.OkapiVariant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements an Index using a Map of term and list of postings.
 */
public class PositionalInvertedIndex implements Index {

    private Map<String, List<Posting>> mAdjacencyList;

    public PositionalInvertedIndex() {
        super();
        this.mAdjacencyList = new HashMap<>();
    }

    @Override
    public List<Posting> getPostingsWithPositions(String term) {
        List<Posting> temp = new ArrayList<>();
        if (mAdjacencyList.containsKey(term)) {
            return mAdjacencyList.get(term);
        }
        return temp;
    }

    @Override
    public List<Posting> getPostingsWithoutPositions(String term) {
        return null;
    }

    @Override
    public List<String> getVocabulary() {
        List<String> mVocabulary = new ArrayList<>();
        mVocabulary.addAll(mAdjacencyList.keySet());
        Collections.sort(mVocabulary);
        return Collections.unmodifiableList(mVocabulary);
    }

    /**
     * Associates the given documentId with the given term with its position in the Posting class object in the index.
     */
    public void addTerm(String term, int documentId, int position) {

        if (mAdjacencyList.containsKey(term)) {

            // Get all current postings available for that term.
            List<Posting> currentPostings = mAdjacencyList.get(term);

            Posting newPosting = new Posting(documentId);
            // If the last posting do have the same documentId, then create a new Posting and add it to the list.
            if (!currentPostings.get(currentPostings.size() - 1).equals(newPosting)) {
                newPosting.addPosition(position);
                currentPostings.add(newPosting);
            } else {
                // Else add the position to the current posting's positions.
                currentPostings.get(currentPostings.size() - 1).getPositions().add(position);
            }

        } else {
            // First time insert.
            List<Posting> tempList = new ArrayList<>();
            Posting newPosting = new Posting(documentId);
            newPosting.addPosition(position);
            tempList.add(newPosting);
            mAdjacencyList.put(term, tempList);
        }

    }

    public void addWdt(String term, int docId, double[] wdts) {
        List<Posting> postings = mAdjacencyList.get(term);
        for(int i=0;i<postings.size();i++) {
            if (postings.get(i).getDocumentId() == docId) {
                postings.get(i).setmWdts(wdts);
                break;
            }
        }
    }

    /**
     * As Okapi variant requires Avg length of entire corpus, we need to call this method seperately.
     * @param hashmap consists of <docID, docLength>.
     * @param docAveLength Average length of docs in entire corpus.
     */
    public void addWdtForOkapi(HashMap<Integer, Integer> hashmap, double docAveLength){
        for(String term : mAdjacencyList.keySet()) {
            List<Posting> postingList = mAdjacencyList.get(term);
            for (Posting posting : postingList) {

                double wdt = new OkapiVariant(hashmap.get(posting.getDocumentId()), docAveLength).getWDT(posting.getPositions().size(), posting.getDocumentId());
                posting.getmWdts()[3] = wdt;
            }
        }
    }
}
