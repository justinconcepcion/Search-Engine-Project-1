package cecs429.query;

import cecs429.index.DiskPositionalIndex;
import cecs429.index.Posting;

import java.util.*;

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

    public List<Posting> getRankedDocuments(HashMap<String, java.util.List<Posting>> hashMapTerms, int N, String path) {

        for(String term : hashMapTerms.keySet()) {
            java.util.List<Posting> postingList = hashMapTerms.get(term);

            float f = (float) N/postingList.size();
            System.out.println("f: " +f);
            float wqt = (float) Math.log(1.0f + f);
            System.out.println("wqt: " +wqt);

            for(Posting posting : postingList) {

                // Calculate wdt.
                float wdt = 1.0f + ((float)Math.log(posting.getTftd()));

                System.out.println("WDT (Doc" + posting.getDocumentId()+") : "+wdt);
                if(mAccum.containsKey(posting.getDocumentId())) {
                    mAccum.put(posting.getDocumentId(), mAccum.get(posting.getDocumentId())+(wdt * wqt));
                } else {
                    mAccum.put(posting.getDocumentId(), wdt * wqt);
                }
            }
        }

        // Create diskPositionalIndex object to retrieve LD values from this class. As this class can only read files.
        DiskPositionalIndex diskPositionalIndex = new DiskPositionalIndex(path);

        mAccum.forEach((docId, ad) -> {

                if(ad!=0) {
                    double ld = diskPositionalIndex.getLd(docId);
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
        if(k>10) {
            k=10;
        }
        for(int i = 0; i < k; i ++){
            Map.Entry<Integer, Float> entry = mPriorityQueue.remove();

            int docId = entry.getKey();
            System.out.println("AD (Doc" + docId+") : "+entry.getValue());
            resultPostings.add(new Posting(docId));
        }

        return resultPostings;
    }
}
