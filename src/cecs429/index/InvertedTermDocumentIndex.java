package cecs429.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvertedTermDocumentIndex implements Index {
    private Map<String, List<Posting>> mAdjacencyList;

    public InvertedTermDocumentIndex() {
        super();
        this.mAdjacencyList = new HashMap<>();

    }

    @Override
    public List<Posting> getPostings(String term) {
        // TODO Auto-generated method stub
        List<Posting> temp = new ArrayList<>();
        if (mAdjacencyList.containsKey(term)) {
            return mAdjacencyList.get(term);
        } else
            return temp;
    }

    @Override
    public List<String> getVocabulary() {
        List<String> vocabulary = new ArrayList<>();
        vocabulary.addAll(mAdjacencyList.keySet());
        Collections.sort(vocabulary);
        return Collections.unmodifiableList(vocabulary);
    }

    public void addTerm(String term, int documentId) {
        Posting temp = new Posting(documentId);
        if (mAdjacencyList.containsKey(term)) {
            if (!mAdjacencyList.get(term).get(mAdjacencyList.get(term).size() - 1).equals(temp)) {
                mAdjacencyList.get(term).add(temp);
            }

        } else {
            List<Posting> tempList = new ArrayList<>();
            tempList.add(temp);
            mAdjacencyList.put(term, tempList);
        }
    }

}
