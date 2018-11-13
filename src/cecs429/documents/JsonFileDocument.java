package cecs429.documents;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import cecs429.model.JsonDoc;

public class JsonFileDocument implements FileDocument {
    private int mDocumentId;
    private Path mFilePath;

    public JsonFileDocument(int documentId, Path filePath) {
        super();
        this.mDocumentId = documentId;
        this.mFilePath = filePath;
    }

    @Override
    public int getId() {
        return mDocumentId;
    }

    @Override
    public long getSize() {
        return new File(mFilePath.toString()).length();
    }

    @Override
    public Reader getContent() {
        JsonDoc doc = null;

        doc = getDocFromFilePath();

        if (doc != null) {
            return new BufferedReader(new StringReader(doc.getBody()));
        } else
            return null;

    }

    @Override
    public String getTitle() {
        JsonDoc doc = null;
        doc = getDocFromFilePath();
        if (doc != null) {
            return mFilePath.getFileName().toString();
        } else
            return null;
    }

    private JsonDoc getDocFromFilePath() {
        JsonReader reader;
        JsonDoc doc = null;
        try {
            reader = new JsonReader(Files.newBufferedReader(mFilePath, Charset.forName("ISO-8859-9")));
            Gson gson = new Gson();
            Type type = new TypeToken<JsonDoc>() {
            }.getType();
            doc = gson.fromJson(reader, type);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }

    @Override
    public Path getFilePath() {
        return mFilePath;
    }

    public static FileDocument loadJsonFileDocument(Path absolutePath, int documentId) {
        return new JsonFileDocument(documentId, absolutePath);
    }

}
