package cecs429.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PositionalInvertedIndex implements Index {

	private Map<String, List<Posting>> mAdjacencyList;
	private List<String> mVocabulary;

	public PositionalInvertedIndex() {
		super();
		this.mAdjacencyList = new HashMap<>();
	}
// Testing for pull merge
	@Override
	public List<Posting> getPostings(String term) {
		List<Posting> temp = new ArrayList<>();
		if (mAdjacencyList.containsKey(term)) {
			return mAdjacencyList.get(term);
		} else
			return temp;
	}
	
	@Override
	public List<String> getVocabulary() {
		mVocabulary = new ArrayList<>();
		mVocabulary.addAll(mAdjacencyList.keySet());
		Collections.sort(mVocabulary);
		return Collections.unmodifiableList(mVocabulary);
	}
	
	public void addTerm(String term, int documentId, int position) {
		
		if (mAdjacencyList.containsKey(term)) {
			
			// Get all current postings available for that term.
			List<Posting> currentPostings = mAdjacencyList.get(term);

			Posting newPosting = new Posting(documentId);
			// If the last posting do have the same documentId, then create a new Posting and add it to the list.
			if (currentPostings.get(currentPostings.size()-1).equals(newPosting)) {
				newPosting.addPosition(position);
				currentPostings.add(newPosting);
			} else {
				// Else add the position to the current posting's positions.
				currentPostings.get(currentPostings.size()-1).getPositions().add(position);
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

}
