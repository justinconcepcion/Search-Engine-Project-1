package edu.csulb;

import java.nio.file.Paths;
import java.util.Scanner;

import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.index.Index;
import cecs429.index.InvertedTermDocumentIndex;
import cecs429.index.Posting;
import cecs429.text.BasicTokenProcessor;
import cecs429.text.EnglishTokenStream;
import cecs429.text.TokenStream;

public class BetterInvertedIndexer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DocumentCorpus corpus = DirectoryCorpus.loadTextDirectory(Paths.get("").toAbsolutePath(), ".txt");
		Index index = indexCorpus(corpus);
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

		BasicTokenProcessor processor = new BasicTokenProcessor();
		TokenStream tokenStream;
		InvertedTermDocumentIndex index = new InvertedTermDocumentIndex();

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
