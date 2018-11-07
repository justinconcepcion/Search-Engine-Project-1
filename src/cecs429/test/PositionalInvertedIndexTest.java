package cecs429.test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.index.PositionalInvertedIndex;
import cecs429.index.Posting;
import cecs429.query.BooleanQueryParser;
import cecs429.query.QueryComponent;
import cecs429.text.EnglishTokenStream;
import cecs429.text.MultipleTokenProcessor;
import cecs429.text.PorterTokenProcessor;
import cecs429.text.TokenStream;

public class PositionalInvertedIndexTest {

    // Path to corpus
	DocumentCorpus corpus = DirectoryCorpus.loadTextDirectory(
			Paths.get("C:\\Users\\test").toAbsolutePath(), ".txt");
	MultipleTokenProcessor processor = new PorterTokenProcessor();
	TokenStream tokenStream;
	PositionalInvertedIndex index = new PositionalInvertedIndex();
	PositionalInvertedIndex indexTest = new PositionalInvertedIndex();

	public PositionalInvertedIndexTest() {
		for (Document d : corpus.getDocuments()) {

			try {
				int position = 0;
				tokenStream = new EnglishTokenStream(d.getContent());
				Iterable<String> iterableOfTokens = tokenStream.getTokens();
				for (String str : iterableOfTokens) {
					// for(String str2: processor.processToken(str))
					for (String indexTerm : processor.processToken(str)) {
						index.addTerm(indexTerm, d.getId(), position);
					}
					position++;
				}
				tokenStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		indexTest.addTerm(processor.processToken("india").get(0), 1, 4);
		indexTest.addTerm(processor.processToken("india").get(0), 3, 4);

		indexTest.addTerm(processor.processToken("to").get(0), 0, 2);
		indexTest.addTerm(processor.processToken("to").get(0), 1, 3);
		indexTest.addTerm(processor.processToken("to").get(0), 1, 5);
		indexTest.addTerm(processor.processToken("to").get(0), 3, 3);
		indexTest.addTerm(processor.processToken("to").get(0), 4, 3);

		indexTest.addTerm(processor.processToken("play").get(0), 0, 3);
		indexTest.addTerm(processor.processToken("play").get(0), 1, 6);
		indexTest.addTerm(processor.processToken("play").get(0), 2, 2);

		indexTest.addTerm(processor.processToken("swimming---is---good---habit").get(0), 0, 5);

	}

	@Test
	public void testIndexWordOne() {

		Assert.assertArrayEquals(indexTest.getPostingsWithPositions("india").toArray(), index.getPostingsWithPositions("india").toArray());

	}

	public void testIndexWordTwo() {

		Assert.assertArrayEquals(indexTest.getPostingsWithPositions("to").toArray(), index.getPostingsWithPositions("to").toArray());

	}

	public void testIndexWordThree() {

		Assert.assertArrayEquals(indexTest.getPostingsWithPositions("play").toArray(), index.getPostingsWithPositions("play").toArray());

	}

	@Test
	public void testIndexOne() {

		String query = "india";
		ArrayList<Integer> expected = new ArrayList<>();
		ArrayList<Integer> actual = new ArrayList<>();
		expected.add(1);
		expected.add(3);

		QueryComponent queryComponent = new BooleanQueryParser(processor).parseQuery(query);
		List<Posting> actualPostingList = queryComponent.getPostings(index);
		for (Posting posting : actualPostingList) {
			actual.add(posting.getDocumentId());

		}

		Assert.assertArrayEquals(actual.toArray(), expected.toArray());

	}


	@Test
	public void testIndexTwo() {

		String query = "to";
		ArrayList<Integer> expected = new ArrayList<>();
		ArrayList<Integer> actual = new ArrayList<>();
		expected.add(0);
		expected.add(1);
		expected.add(3);
		expected.add(4);

		QueryComponent queryComponent = new BooleanQueryParser(processor).parseQuery(query);
		List<Posting> actualPostingList = queryComponent.getPostings(index);
		for (Posting posting : actualPostingList) {
			actual.add(posting.getDocumentId());

		}

		Assert.assertArrayEquals(actual.toArray(), expected.toArray());

	}

	@Test
	public void testIndexThree() {

		String query = "play";
		ArrayList<Integer> expected = new ArrayList<>();
		ArrayList<Integer> actual = new ArrayList<>();
		expected.add(0);
		expected.add(1);
		expected.add(2);

		QueryComponent queryComponent = new BooleanQueryParser(processor).parseQuery(query);
		List<Posting> actualPostingList = queryComponent.getPostings(index);
		for (Posting posting : actualPostingList) {
			actual.add(posting.getDocumentId());

		}

		Assert.assertArrayEquals(actual.toArray(), expected.toArray());

	}

	@Test
	public void testIndexAndQuery() {

		String query = "to play";
		ArrayList<Integer> expected = new ArrayList<>();
		ArrayList<Integer> actual = new ArrayList<>();
		expected.add(0);
		expected.add(1);

		QueryComponent queryComponent = new BooleanQueryParser(processor).parseQuery(query);
		List<Posting> actualPostingList = queryComponent.getPostings(index);
		for (Posting posting : actualPostingList) {
			actual.add(posting.getDocumentId());

		}

		Assert.assertArrayEquals(actual.toArray(), expected.toArray());

	}

	@Test
	public void testIndexOrQuery() {

		String query = "to+play";
		ArrayList<Integer> expected = new ArrayList<>();
		ArrayList<Integer> actual = new ArrayList<>();
		expected.add(0);
		expected.add(1);
		expected.add(2);
		expected.add(3);
		expected.add(4);

		QueryComponent queryComponent = new BooleanQueryParser(processor).parseQuery(query);
		List<Posting> actualPostingList = queryComponent.getPostings(index);
		for (Posting posting : actualPostingList) {
			actual.add(posting.getDocumentId());

		}

		Assert.assertArrayEquals(actual.toArray(), expected.toArray());

	}

	@Test
	public void testIndexNotQuery() {

		String query = "to -india";
		ArrayList<Integer> expected = new ArrayList<>();
		ArrayList<Integer> actual = new ArrayList<>();
		expected.add(0);
		expected.add(4);

		QueryComponent queryComponent = new BooleanQueryParser(processor).parseQuery(query);
		List<Posting> actualPostingList = queryComponent.getPostings(index);
		for (Posting posting : actualPostingList) {
			actual.add(posting.getDocumentId());

		}

		Assert.assertArrayEquals(actual.toArray(), expected.toArray());

	}

	@Test
	public void testIndexNotQueryTwo() {

		String query = "to -play";
		ArrayList<Integer> expected = new ArrayList<>();
		ArrayList<Integer> actual = new ArrayList<>();
		expected.add(3);
		expected.add(4);

		QueryComponent queryComponent = new BooleanQueryParser(processor).parseQuery(query);
		List<Posting> actualPostingList = queryComponent.getPostings(index);
		for (Posting posting : actualPostingList) {
			actual.add(posting.getDocumentId());

		}

		Assert.assertArrayEquals(actual.toArray(), expected.toArray());

	}

	@Test
	public void testIndexNotQueryThree() {

		String query = "to -\"to play\"";
		ArrayList<Integer> expected = new ArrayList<>();
		ArrayList<Integer> actual = new ArrayList<>();
		expected.add(3);
		expected.add(4);

		QueryComponent queryComponent = new BooleanQueryParser(processor).parseQuery(query);
		List<Posting> actualPostingList = queryComponent.getPostings(index);
		for (Posting posting : actualPostingList) {
			actual.add(posting.getDocumentId());

		}

		Assert.assertArrayEquals(actual.toArray(), expected.toArray());

	}

	@Test
	public void testIndexNotQueryFour() {

		String query = "to -[to NEAR/4 play]";
		ArrayList<Integer> expected = new ArrayList<>();
		ArrayList<Integer> actual = new ArrayList<>();
		expected.add(3);
		expected.add(4);

		QueryComponent queryComponent = new BooleanQueryParser(processor).parseQuery(query);
		List<Posting> actualPostingList = queryComponent.getPostings(index);
		for (Posting posting : actualPostingList) {
			actual.add(posting.getDocumentId());

		}

		Assert.assertArrayEquals(actual.toArray(), expected.toArray());

	}

	@Test
	public void testIndexPharseQuery() {

		String query = "\"to play\"";
		ArrayList<Integer> expected = new ArrayList<>();
		ArrayList<Integer> actual = new ArrayList<>();
		expected.add(0);
		expected.add(1);

		QueryComponent queryComponent = new BooleanQueryParser(processor).parseQuery(query);
		List<Posting> actualPostingList = queryComponent.getPostings(index);
		for (Posting posting : actualPostingList) {
			actual.add(posting.getDocumentId());

		}

		Assert.assertArrayEquals(actual.toArray(), expected.toArray());
	}

	@Test
	public void testIndexNearQuery() {

		String query = "[to NEAR/4 play]";
		ArrayList<Integer> expected = new ArrayList<>();
		ArrayList<Integer> actual = new ArrayList<>();
		expected.add(0);
		expected.add(1);

		QueryComponent queryComponent = new BooleanQueryParser(processor).parseQuery(query);
		List<Posting> actualPostingList = queryComponent.getPostings(index);
		for (Posting posting : actualPostingList) {
			actual.add(posting.getDocumentId());

		}

		Assert.assertArrayEquals(actual.toArray(), expected.toArray());
	}

	@Test
	public void testTokenProcessorOne() {

		String query = "swimming---is---good---habit";
		ArrayList<Integer> expected = new ArrayList<>();
		ArrayList<Integer> actual = new ArrayList<>();
		expected.add(0);

		QueryComponent queryComponent = new BooleanQueryParser(processor).parseQuery(query);
		List<Posting> actualPostingList = queryComponent.getPostings(index);
		for (Posting posting : actualPostingList) {
			actual.add(posting.getDocumentId());

		}

		Assert.assertArrayEquals(actual.toArray(), expected.toArray());
	}

	@Test
	public void testTokenProcessorOneOne() {

		String query = "swimming";
		ArrayList<Integer> expected = new ArrayList<>();
		ArrayList<Integer> actual = new ArrayList<>();
		expected.add(0);

		QueryComponent queryComponent = new BooleanQueryParser(processor).parseQuery(query);
		List<Posting> actualPostingList = queryComponent.getPostings(index);
		for (Posting posting : actualPostingList) {
			actual.add(posting.getDocumentId());

		}

		Assert.assertArrayEquals(actual.toArray(), expected.toArray());
	}

	@Test
	public void testTokenProcessorOneTwo() {

		String query = "is";
		ArrayList<Integer> expected = new ArrayList<>();
		ArrayList<Integer> actual = new ArrayList<>();
		expected.add(0);

		QueryComponent queryComponent = new BooleanQueryParser(processor).parseQuery(query);
		List<Posting> actualPostingList = queryComponent.getPostings(index);
		for (Posting posting : actualPostingList) {
			actual.add(posting.getDocumentId());

		}

		Assert.assertArrayEquals(actual.toArray(), expected.toArray());
	}

	@Test
	public void testTokenProcessorOneThree() {

		String query = "good";
		ArrayList<Integer> expected = new ArrayList<>();
		ArrayList<Integer> actual = new ArrayList<>();
		expected.add(0);

		QueryComponent queryComponent = new BooleanQueryParser(processor).parseQuery(query);
		List<Posting> actualPostingList = queryComponent.getPostings(index);
		for (Posting posting : actualPostingList) {
			actual.add(posting.getDocumentId());

		}

		Assert.assertArrayEquals(actual.toArray(), expected.toArray());
	}

	@Test
	public void testTokenProcessorOneFour() {

		String query = "habit";
		ArrayList<Integer> expected = new ArrayList<>();
		ArrayList<Integer> actual = new ArrayList<>();
		expected.add(0);

		QueryComponent queryComponent = new BooleanQueryParser(processor).parseQuery(query);
		List<Posting> actualPostingList = queryComponent.getPostings(index);
		for (Posting posting : actualPostingList) {
			actual.add(posting.getDocumentId());

		}

		Assert.assertArrayEquals(actual.toArray(), expected.toArray());
	}

	@Test
	public void testTokenProcessorTwo() {

		String query = "..!!swimming-is-good--habit";
		ArrayList<Integer> expected = new ArrayList<>();
		ArrayList<Integer> actual = new ArrayList<>();
		expected.add(0);

		QueryComponent queryComponent = new BooleanQueryParser(processor).parseQuery(query);
		List<Posting> actualPostingList = queryComponent.getPostings(index);
		for (Posting posting : actualPostingList) {
			actual.add(posting.getDocumentId());

		}

		Assert.assertArrayEquals(actual.toArray(), expected.toArray());
	}

	@Test
	public void testTokenProcessorThree() {

		String query = "...swimming---is---good---habit...!!!!";
		ArrayList<Integer> expected = new ArrayList<>();
		ArrayList<Integer> actual = new ArrayList<>();
		expected.add(0);

		QueryComponent queryComponent = new BooleanQueryParser(processor).parseQuery(query);
		List<Posting> actualPostingList = queryComponent.getPostings(index);
		for (Posting posting : actualPostingList) {
			actual.add(posting.getDocumentId());

		}

		Assert.assertArrayEquals(actual.toArray(), expected.toArray());
	}

	@Test
	public void testTokenProcessorFour() {

		String query = "...SWIMMING--\"-is-'--GOOD--'-habit...!!!!";
		ArrayList<Integer> expected = new ArrayList<>();
		ArrayList<Integer> actual = new ArrayList<>();
		expected.add(0);

		QueryComponent queryComponent = new BooleanQueryParser(processor).parseQuery(query);
		List<Posting> actualPostingList = queryComponent.getPostings(index);
		for (Posting posting : actualPostingList) {
			actual.add(posting.getDocumentId());

		}

		Assert.assertArrayEquals(actual.toArray(), expected.toArray());
	}


}
