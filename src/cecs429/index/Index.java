package cecs429.index;

import java.util.List;

/**
 * An Index can retrieve postings for a term from a data structure associating terms and the documents
 * that contain them.
 */
public interface Index {
    /**
     * Retrieves a list of Postings of documents that contain the given term with positions for the term.
     */
    List<Posting> getPostingsWithPositions(String term);

    /**
     * Retrieves a list of Postings of documents that contain the given term without positions for the term.
     */
    List<Posting> getPostingsWithoutPositions(String term);

    /**
     * A (sorted) list of all terms in the index vocabulary.
     */
    List<String> getVocabulary();
}
