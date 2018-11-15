package cecs429.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A Posting encapulates a document ID associated with a search query component.
 */
public class Posting {
    private int mTftd;
    private int mDocumentId;
    private List<Integer> mPositions;
    private float mAccumScore;

    public Posting(int documentId) {
        mDocumentId = documentId;
        mPositions = new ArrayList<>();
    }

    public Posting(int documentId, float accumScore) {
        mDocumentId = documentId;
        mAccumScore = accumScore;
    }

    /**
     * Gets document ID of the current posting object.
     *
     * @return Document ID of the Posting.
     */
    public int getDocumentId() {
        return mDocumentId;
    }

    /**
     * Get list of positions, where the term occurs in the current posting.
     *
     * @return List of integers, i.e. positions of the current posting.
     */
    public List<Integer> getPositions() {
        return mPositions;
    }

    /**
     * Add position of the term in the current document, in the list of positions.
     *
     * @param position Integer to which the current term is.
     */
    public void addPosition(int position) {
        mPositions.add(position);
    }

    /**
     * Check if two Posting objects are equal or not.
     *
     * @param obj Posting object to compare.
     * @return Boolean value as true if document Ids of both the posting objects match.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Posting) {
            Posting temp = (Posting) obj;
            return mDocumentId == temp.getDocumentId();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return mDocumentId * 13;
    }

    public int getTftd() {
        return mTftd;
    }

    public void setTftd(int mTftd) {
        this.mTftd = mTftd;
    }

    public float getmAccumScore() {
        return mAccumScore;
    }

    public void setmAccumScore(float mAccumScore) {
        this.mAccumScore = mAccumScore;
    }
}
