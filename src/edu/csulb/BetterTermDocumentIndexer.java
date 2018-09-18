package edu.csulb;

import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Scanner;

import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.index.Index;
import cecs429.index.Posting;
import cecs429.index.TermDocumentIndex;
import cecs429.text.BasicTokenProcessor;
import cecs429.text.EnglishTokenStream;
import cecs429.text.TokenStream;

public class BetterTermDocumentIndexer {
	public static void main(String[] args) {
		DocumentCorpus corpus = DirectoryCorpus.loadTextDirectory(Paths.get("").toAbsolutePath(), ".txt");
		Index index = indexCorpus(corpus);
		// We aren't ready to use a full query parser; for now, we'll only support
		// single-term queries.
		Scanner reader = new Scanner(System.in);
		String query = "";
		do {
			System.out.println("Enter the term to search:");
			query = reader.nextLine();
			for (Posting p : index.getPostings(query)) {
				System.out.println("Document " + corpus.getDocument(p.getDocumentId()).getTitle());
			}
			System.out.println("Continue ? (Y/N):");
			query = reader.nextLine();
		} while (!query.equalsIgnoreCase("N"));
		reader.close();
		System.out.println("Thank you!!");
	}

	private static Index indexCorpus(DocumentCorpus corpus) {

		HashSet<String> vocabulary = new HashSet<>();
		BasicTokenProcessor processor = new BasicTokenProcessor();
		TokenStream tokenStream;

		// First, build the vocabulary hash set.
		// TODO:
		// Get all the documents in the corpus by calling GetDocuments().
		// Iterate through the documents, and:
		// Tokenize the document's content by constructing an EnglishTokenStream around
		// the document's content.
		// Iterate through the tokens in the document, processing them using a
		// BasicTokenProcessor,
		// and adding them to the HashSet vocabulary.
		for (Document d : corpus.getDocuments()) {
			try {
				tokenStream = new EnglishTokenStream(d.getContent());
				Iterable<String> iterableOfTokens = tokenStream.getTokens();
				for (String str : iterableOfTokens) {
					vocabulary.add(processor.processToken(str));
				}
				tokenStream.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// TODO:
		// Constuct a TermDocumentMatrix once you know the size of the vocabulary.
		// THEN, do the loop again! But instead of inserting into the HashSet, add terms
		// to the index wi0th addPosting.
		TermDocumentIndex index = new TermDocumentIndex(vocabulary, corpus.getCorpusSize());
		for (Document d : corpus.getDocuments()) {
			try {
				tokenStream = new EnglishTokenStream(d.getContent());
				Iterable<String> iterableOfTokens = tokenStream.getTokens();
				for (String str : iterableOfTokens) {
					index.addTerm(processor.processToken(str), d.getId());
				}
				tokenStream.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return index;
	}
}
