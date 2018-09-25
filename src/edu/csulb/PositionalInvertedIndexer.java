package edu.csulb;

import java.nio.file.Paths;
import java.util.Scanner;

import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.index.Index;
import cecs429.index.PositionalInvertedIndex;
import cecs429.index.Posting;
import cecs429.text.BasicTokenProcessor;
import cecs429.text.EnglishTokenStream;
import cecs429.text.TokenStream;

/**
 * TO maintain one index for corpus, where postings lists consists of (documentID, [position1, position2, ...]) pairs.
 * @author KARAN
 *
 */
public class PositionalInvertedIndexer {

	public static void main(String[] args) {
		DocumentCorpus corpus = DirectoryCorpus.loadTextDirectory(Paths.get("C:\\Users\\KARAN\\Desktop\\Study\\4th Sem\\SET\\MobyDick10Chapters").toAbsolutePath(), ".txt");
		Index index = indexCorpus(corpus);
		Scanner reader = new Scanner(System.in);
		String query = "";
		
		// Logic to get input from the user.
		do {
			System.out.println("Enter the term to search:");
			query = reader.nextLine();
			for (Posting p : index.getPostings(query)) {
				System.out.print("Document " + corpus.getDocument(p.getDocumentId()).getTitle());
				System.out.print(", Postions: ");
				for(int i=0;i<p.getPositions().size();i++) {
					System.out.print(p.getPositions().get(i)+", ");
				}
				System.out.println("");
			}
			System.out.println("Continue ? (Y/N):");
			query = reader.nextLine();
		} while (!query.equalsIgnoreCase("N"));
		reader.close();
		System.out.println("Thank you!!");

	}

	/**
	 * index terms in the documents are indexed with the document IDs with positions also.
	 * @param corpus the documents in the corpus.
	 * @return object of index with terms indexed with document IDs.
	 */
	private static Index indexCorpus(DocumentCorpus corpus) {

		BasicTokenProcessor processor = new BasicTokenProcessor();
		TokenStream tokenStream;
		PositionalInvertedIndex index = new PositionalInvertedIndex();

		// For every document, get every term and pass token, document id and position to add term method of index class.
		for (Document d : corpus.getDocuments()) {
			
			try {
				int position = 0;
				tokenStream = new EnglishTokenStream(d.getContent());
				Iterable<String> iterableOfTokens = tokenStream.getTokens();
				for (String str : iterableOfTokens) {
					
					index.addTerm(processor.processToken(str), d.getId(), position);
					position++;
				}
				
				tokenStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return index;
	}

}
