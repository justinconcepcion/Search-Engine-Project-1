package cecs429.query;

import java.util.List;

import cecs429.index.Index;
import cecs429.index.Posting;

public class NotQuery implements QueryComponent {
	private QueryComponent mComponent;

	public NotQuery(QueryComponent mComponent) {
		this.mComponent = mComponent;
	}

	@Override
	public List<Posting> getPostings(Index index) {
		return mComponent.getPostings(index);
	}

	@Override
	public boolean getPositive() {
		// only component wrapped with this will have this method negative
		return false;
	}

}
