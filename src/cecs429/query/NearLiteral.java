package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;
import cecs429.text.MultipleTokenProcessor;

import java.util.ArrayList;
import java.util.List;

public class NearLiteral implements QueryComponent {

    private MultipleTokenProcessor mProcessor;
    private String mFirstToken;
    private String mSecondToken;
    private int mNear;

    public NearLiteral(String firstToken, String secondToken, int near, MultipleTokenProcessor processor) {
        mFirstToken = firstToken;
        mSecondToken = secondToken;
        this.mNear = near;
        this.mProcessor = processor;
    }

    @Override
    public List<Posting> getPostings(Index index) {
        List<Posting> results = new ArrayList<>();

        results.addAll(index.getPostings(mProcessor.processToken(mFirstToken).get(0)));
        results = positionalIntersect(results, index.getPostings(mProcessor.processToken(mSecondToken).get(0)));
        return results;
    }

    private List<Posting> positionalIntersect(List<Posting> p1, List<Posting> p2) {
        List<Posting> results = new ArrayList<>();

        // i is counter for first postings List.
        // j is counter for second postings List.
        int i = 0;
        int j = 0;
        while (i < p1.size() && j < p2.size()) {

            if (p1.get(i).getDocumentId() == p2.get(j).getDocumentId()) {
                List<Integer> list = new ArrayList<>();

                List<Integer> position1 = p1.get(i).getPositions();
                List<Integer> position2 = p2.get(j).getPositions();

                // k is counter for positions of first postings.
                // l is counter for positions of second postings.
                int k = 0;
                while (k < position1.size()) {
                    int l = 0;
                    while (l < position2.size()) {

                        if (position2.get(l) - position1.get(k) <= mNear) {
                            list.add(position2.get(l));
                        } else if (position2.get(l) > position1.get(k)) {
                            break;
                        }
                        l++;
                    }
                    while (list.size() != 0 && Math.abs(list.get(0) - position1.get(k)) > mNear) {
                        list.remove(0);
                    }

                    if (list.size() > 0) {
                        Posting posting = new Posting(p1.get(i).getDocumentId());
                        posting.addPosition(list.get(0));
                        results.add(posting);
                    }
                    k++;
                }
                i++;
                j++;
            } else if (p1.get(i).getDocumentId() < p2.get(j).getDocumentId()) {
                i++;
            } else {
                j++;
            }
        }
        return results;

    }

    @Override
    public boolean getPositive() {
        return true;
    }
}
