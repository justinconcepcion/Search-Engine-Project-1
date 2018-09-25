package cecs429.documents;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
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

	public JsonFileDocument(int mDocumentId, Path mFilePath) {
		super();
		this.mDocumentId = mDocumentId;
		this.mFilePath = mFilePath;
	}

	@Override
	public int getId() {
		return mDocumentId;
	}

	@Override
	public Reader getContent() {
		JsonReader reader;
		JsonDoc doc = null;
		try {
			reader = new JsonReader(Files.newBufferedReader(mFilePath,Charset.forName("ISO-8859-9")));
			Gson gson = new Gson();
			Type type = new TypeToken<JsonDoc>() {}.getType();
			doc = gson.fromJson(reader, type);

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (doc != null) {
			return new BufferedReader(new StringReader(doc.getBody()));
		} else
			return null;

	}

	@Override
	public String getTitle() {
		JsonReader reader;
		JsonDoc doc = null;
		try {
			reader = new JsonReader(Files.newBufferedReader(mFilePath,Charset.forName("ISO-8859-9")));
			Gson gson = new Gson();
			Type type = new TypeToken<JsonDoc>() {}.getType();
			doc = gson.fromJson(reader, type);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (doc != null) {
			return doc.getTitle();
		} else
			return null;
	}

	@Override
	public Path getFilePath() {
		return mFilePath;
	}
	
	public static FileDocument loadJsonFileDocument(Path absolutePath, int documentId) {
		return new JsonFileDocument(documentId, absolutePath);
	}

}
