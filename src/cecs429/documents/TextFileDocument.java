package cecs429.documents;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Represents a document that is saved as a simple text file in the local file system.
 */
public class TextFileDocument implements FileDocument {
    private int mDocumentId;
    private Path mFilePath;

    /**
     * Constructs a TextFileDocument with the given document ID representing the file at the given
     * absolute file path.
     */
    private TextFileDocument(int documentId, Path absoluteFilePath) {
        mDocumentId = documentId;
        mFilePath = absoluteFilePath;
    }

    /**
     * The absolute path to the document's file.
     */
    @Override
    public Path getFilePath() {
        return mFilePath;
    }

    /**
     * The ID used by the index to represent the document.
     */
    @Override
    public int getId() {
        return mDocumentId;
    }

    /**
     * Gets a stream over the content of the document.
     */
    @Override
    public Reader getContent() {
        try {
            return Files.newBufferedReader(mFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getSize() {
        return new File(mFilePath.toString()).length();
    }
    /**
     * The title of the document, for displaying to the user.
     */
    @Override
    public String getTitle() {
        return mFilePath.getFileName().toString();
    }

    protected static FileDocument loadTextFileDocument(Path absolutePath, int documentId) {
        return new TextFileDocument(documentId, absolutePath);
    }
}
