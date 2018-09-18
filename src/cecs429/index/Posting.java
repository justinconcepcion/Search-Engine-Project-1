package cecs429.index;

/**
 * A Posting encapulates a document ID associated with a search query component.
 */
public class Posting {
	private int mDocumentId;
	
	public Posting(int documentId) {
		mDocumentId = documentId;
	}
	
	public int getDocumentId() {
		return mDocumentId;
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
