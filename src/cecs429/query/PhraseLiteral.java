package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;
import cecs429.text.MultipleTokenProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a phrase literal consisting of one or more terms that must occur in sequence.
 */
public class PhraseLiteral implements QueryComponent {
    // The list of individual terms in the phrase.
    private List<String> mTerms = new ArrayList<>();
    private MultipleTokenProcessor multipleTokenProcessor;

    /**
     * Constructs a PhraseLiteral with the given individual phrase terms.
     */
    public PhraseLiteral(List<String> terms) {
        mTerms.addAll(terms);
    }

    /**
     * Constructs a PhraseLiteral given a string with one or more individual terms separated by spaces.
     */
    public PhraseLiteral(String terms, MultipleTokenProcessor processor) {
        mTerms.addAll(Arrays.asList(terms.split(" ")));
        multipleTokenProcessor = processor;
    }

    @Override
    public List<Posting> getPostings(Index index) {
        List<Posting> results = new ArrayList<>();

        results = index.getPostingsWithPositions(multipleTokenProcessor.processToken(mTerms.get(0)).get(0));
        for (int i = 1; i < mTerms.size(); i++) {
            List<Posting> secondList = index.getPostingsWithPositions(multipleTokenProcessor.processToken(mTerms.get(i)).get(0));

            results = positionalIntersect(results, secondList);
        }

        return results;
        // TODO: program this method. Retrieve the postings for the individual terms in the phrase,

    }

    @Override
    public boolean getPositive() {
        return true;
    }

    private List<Posting> positionalIntersect(List<Posting> p1, List<Posting> p2) {
        List<Posting> results = new ArrayList<>();

        int i = 0;
        int j = 0;
        while (i < p1.size() && j < p2.size()) {
            if (p1.get(i).getDocumentId() == p2.get(j).getDocumentId()) {
                List<Integer> list = new ArrayList<>();

                List<Integer> position1 = p1.get(i).getPositions();
                List<Integer> position2 = p2.get(j).getPositions();

                int k = 0;
                while (k < position1.size()) {
                    int l = 0;
                    while (l < position2.size()) {

                        if (position2.get(l) - position1.get(k) == 1) {
                            list.add(position2.get(l));
                        } else if (position2.get(l) > position1.get(k)) {
                            break;
                        }
                        l++;
                    }
                    while (list.size() != 0 && Math.abs(list.get(0) - position1.get(k)) > 1) {
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
    public String toString() {
        return "\"" + String.join(" ", mTerms) + "\"";
    }
}
