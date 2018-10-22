package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An OrQuery composes other QueryComponents and merges their postings with a
 * union-type operation.
 */
public class OrQuery implements QueryComponent {
    // The components of the Or query.
    private List<QueryComponent> mComponents;

    public OrQuery(List<QueryComponent> components) {
        mComponents = components;
    }

    /**
     * Method will return the merge posting list with union of the posting of
     * individual QueryComponent
     */

    @Override
    public List<Posting> getPostings(Index index) {
        List<Posting> firstList;
        List<Posting> secondList;
        QueryComponent firstComponent = mComponents.get(0);
        firstList = firstComponent.getPostings(index);

        for (int i = 1; i < mComponents.size(); i++) {
            QueryComponent secondComponent = mComponents.get(i);
            secondList = secondComponent.getPostings(index);
            firstList = merge(firstList, secondList);
        }
        return firstList;
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
                resultReturn.add(firstList.get(i));
                i++;
            } else if (firstList.get(i).getDocumentId() > secondList.get(j).getDocumentId()) {
                resultReturn.add(secondList.get(j));
                j++;
            }
        }
        if (i < firstList.size())
            resultReturn.addAll(firstList.subList(i, firstList.size()));
        if (j < secondList.size())
            resultReturn.addAll(secondList.subList(i, secondList.size()));

        return resultReturn;

    }

    @Override
    public String toString() {
        // Returns a string of the form "[SUBQUERY] + [SUBQUERY] + [SUBQUERY]"
        return "(" + String.join(" + ", mComponents.stream().map(c -> c.toString()).collect(Collectors.toList()))
                + " )";
    }

    @Override
    public boolean getPositive() {
        return true;
    }
}
