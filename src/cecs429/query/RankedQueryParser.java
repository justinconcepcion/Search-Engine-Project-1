package cecs429.query;

import cecs429.variantmethods.VariantMethodsInterface;
import cecs429.index.Posting;

import java.util.*;

/**
 * Calculate the ranks for each postings list of a term.
 */
public class RankedQueryParser {

    private Map<Integer, Float> mAccum;
    private PriorityQueue<Map.Entry<Integer, Float>> mPriorityQueue;

    public RankedQueryParser(){
        mAccum = new HashMap<>();
        mPriorityQueue = new PriorityQueue<>((o1, o2) -> {
            if(o1.getValue() < o2.getValue()) {
                return 1;
            }
            if(o1.getValue() > o2.getValue()) {
                return -1;
            }
            return 0;
        });
    }

    public List<Posting> getRankedDocuments(HashMap<String, java.util.List<Posting>> hashMapTerms, int N, VariantMethodsInterface variant, String path, int methodNumber) {

        for(String term : hashMapTerms.keySet()) {
            java.util.List<Posting> postingList = hashMapTerms.get(term);

            float wqt = variant.getWQT(postingList.size(), N);

            for(Posting posting : postingList) {

                // Calculate wdt.
                // float wdt = variant.getWDT(posting.getTftd(), posting.getDocumentId());
                double[] wdts = posting.getmWdts();
                double wdt = wdts[methodNumber];

                if(mAccum.containsKey(posting.getDocumentId())) {
                    mAccum.put(posting.getDocumentId(), mAccum.get(posting.getDocumentId())+((float)wdt * wqt));
                } else {
                    mAccum.put(posting.getDocumentId(), (float)wdt * wqt);
                }
            }
        }

        // Create diskPositionalIndex object to retrieve LD values from this class. As this class can only read files.
        mAccum.forEach((docId, ad) -> {

                if(ad!=0) {
                    double ld = variant.getLD(path, docId);
                    ad = ad/((float)ld);
                    mAccum.put(docId, ad);

                }
            }
        );

        // insert in the queue
        for(Map.Entry<Integer, Float> entry : mAccum.entrySet()){
            mPriorityQueue.offer(entry);
        }

        // poll the top k
        List<Posting> resultPostings = new ArrayList<>();
        int k = mPriorityQueue.size();
        if(k>50) {
            k=50;        }
        for(int i = 0; i < k; i ++){
            Map.Entry<Integer, Float> entry = mPriorityQueue.remove();
            int docId = entry.getKey();
            resultPostings.add(new Posting(docId, (double)entry.getValue()));
        }

        return resultPostings;
    }
}
