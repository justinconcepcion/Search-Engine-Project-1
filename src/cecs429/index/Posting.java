package cecs429.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A Posting encapulates a document ID associated with a search query component.
 */
public class Posting {
	private int mDocumentId;
	private List<Integer> mPositions;
	
	public Posting(int documentId) {
		mDocumentId = documentId;
		mPositions = new ArrayList<>();
	}
	
	public int getDocumentId() {
		return mDocumentId;
	}
	
	public List<Integer> getPositions() {
		return mPositions;
	}
	
	public void addPosition(int position) {
		mPositions.add(position);
	}
	
	@Override
		public boolean equals(Object obj) {
			// TODO Auto-generated method stub
			Posting temp=(Posting)obj;
			return mDocumentId==temp.getDocumentId();
		}
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		
		return  mDocumentId*13;
	}
}
