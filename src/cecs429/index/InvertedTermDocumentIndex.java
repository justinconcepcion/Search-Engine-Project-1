package cecs429.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvertedTermDocumentIndex implements Index {
	private Map<String, List<Posting>> mAdjecesyList;
	private List<String> mVocabulary;

	public InvertedTermDocumentIndex() {
		super();
		this.mAdjecesyList = new HashMap<>();

	}

	@Override
	public List<Posting> getPostings(String term) {
		// TODO Auto-generated method stub
		List<Posting> temp = new ArrayList<>();
		if (mAdjecesyList.containsKey(term)) {
			return mAdjecesyList.get(term);
		} else
			return temp;
	}

	@Override
	public List<String> getVocabulary() {
		// TODO Auto-generated method stub
		mVocabulary = new ArrayList<>();
		mVocabulary.addAll(mAdjecesyList.keySet());
		Collections.sort(mVocabulary);
		return Collections.unmodifiableList(mVocabulary);
	}

	public void addTerm(String term, int documentId) {
		Posting temp = new Posting(documentId);
		if (mAdjecesyList.containsKey(term)) {
			if (!mAdjecesyList.get(term).get(mAdjecesyList.get(term).size()-1).equals(temp)) {
				mAdjecesyList.get(term).add(temp);
			}

		} else {
			List<Posting> tempList = new ArrayList<>();
			tempList.add(temp);
			mAdjecesyList.put(term, tempList);
		}
	}

}
