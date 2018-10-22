package cecs429.query;

import cecs429.index.Index;
import cecs429.index.Posting;
import cecs429.text.MultipleTokenProcessor;

import java.util.List;

/**
 * A TermLiteral represents a single term in a subquery.
 */
public class TermLiteral implements QueryComponent {
	private MultipleTokenProcessor processor;
	private String mTerm;
	
	public TermLiteral(String term, MultipleTokenProcessor processor) {
		mTerm = term;
		this.processor=processor;
	}
	
	public String getTerm() {
		return mTerm;
	}
	
	@Override
	public List<Posting> getPostings(Index index) {
		return index.getPostings(processor.processToken(mTerm).get(0));
	}
	
	@Override
	public String toString() {
		return mTerm;
	}

	@Override
	public boolean getPositive() {
		return true;
	}
}
