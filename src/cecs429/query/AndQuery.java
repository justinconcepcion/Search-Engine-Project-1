package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An AndQuery composes other QueryComponents and merges their postings in an
 * intersection-like operation.
 */
public class AndQuery implements QueryComponent {
    private List<QueryComponent> mComponents;

    public AndQuery(List<QueryComponent> components) {
        mComponents = components;
    }

    /**
     * Positive component will consist all components without '-' in fron of literal
     * Negative component will have only those with '-' at beginning Positive
     * component will me AND Negative component will be OR and final posting list
     * will AND MINUS OR
     */
    @Override
    public List<Posting> getPostings(Index index) {
        List<Posting> finalList;

        // Composed QueryComponents and intersecting the resulting postings.
        List<QueryComponent> mPositiveComponent = new ArrayList<>();
        List<QueryComponent> mNegativeComponent = new ArrayList<>();
        for (int i = 0; i < mComponents.size(); i++) {
            if (mComponents.get(i).getPositive()) {
                mPositiveComponent.add(mComponents.get(i));
            } else {
                mNegativeComponent.add(mComponents.get(i));
            }
        }
        List<Posting> firstPositiveList;
        List<Posting> secondPositiveList;

        QueryComponent firstPositiveComponent = mPositiveComponent.get(0);
        firstPositiveList = firstPositiveComponent.getPostings(index);
        for (int i = 1; i < mPositiveComponent.size(); i++) {
            QueryComponent secondPositiveComponent = mPositiveComponent.get(i);
            secondPositiveList = secondPositiveComponent.getPostings(index);
            firstPositiveList = merge(firstPositiveList, secondPositiveList);
        }
        List<Posting> firstNegativeList = new ArrayList<>();
        List<Posting> secondNegativeList = new ArrayList<>();
        if (!mNegativeComponent.isEmpty()) {
            secondNegativeList = new OrQuery(mNegativeComponent).getPostings(index);
            firstNegativeList = merge(firstNegativeList, secondNegativeList);
        }
        if (firstNegativeList.isEmpty())
            return firstPositiveList;
        else {
            finalList = mergeNegative(firstPositiveList, firstNegativeList);
        }

        return finalList;
    }

    private List<Posting> mergeNegative(List<Posting> firstPositiveList, List<Posting> firstNegativeList) {
        List<Posting> resultReturn = new ArrayList<>();

        int i = 0, j = 0;

        resultReturn.addAll(firstPositiveList);

        while (i < resultReturn.size() && j < firstNegativeList.size()) {

            if (resultReturn.get(i).getDocumentId() == firstNegativeList.get(j).getDocumentId()) {
                resultReturn.remove(i);
                j++;
            } else if(resultReturn.get(i).getDocumentId() < firstNegativeList.get(j).getDocumentId()) {
                i++;
            } else {
                j++;
            }

        }

        return resultReturn;
    }

    private List<Posting> merge(List<Posting> firstList, List<Posting> secondList) {
        List<Posting> resultReturn = new ArrayList<>();
        if (firstList.isEmpty()) {
            return secondList;
        }
        if (secondList.isEmpty()) {
            return firstList;
        }
        int i = 0, j = 0;
        while (i < firstList.size() && j < secondList.size()) {
            if (firstList.get(i).getDocumentId() == secondList.get(j).getDocumentId()) {

                resultReturn.add(firstList.get(i));
                i++;
                j++;
                continue;
            }
            if (firstList.get(i).getDocumentId() < secondList.get(j).getDocumentId()) {
                i++;
            } else if (firstList.get(i).getDocumentId() > secondList.get(j).getDocumentId()) {
                j++;
            }
        }

        return resultReturn;
    }

    @Override
    public String toString() {
        return String.join(" ", mComponents.stream().map(c -> c.toString()).collect(Collectors.toList()));
    }

    @Override
    public boolean getPositive() {
        return true;
    }
}
