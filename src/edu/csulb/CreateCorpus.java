package edu.csulb;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import cecs429.model.JsonDocList;

public class CreateCorpus {

	public static final String JSON_PATH = "D:\\SEARCH_ENGINE\\project\\all-nps-sites.json";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 
		try {
			JsonReader reader = new JsonReader(new FileReader(JSON_PATH));
			Gson gson=new Gson();
			Gson builder=new GsonBuilder().create();
			Type type = new TypeToken<JsonDocList>(){}.getType();
			JsonDocList docList=gson.fromJson(reader, type);
			
			for(int i=0;i<docList.getList().size();i++)
			{
				File file = new File("D:\\SEARCH_ENGINE\\project\\corpus\\article"+(i+1)+".json");
				Writer writer=new FileWriter(file);
				builder.toJson(docList.getList().get(i), writer);
				writer.close();
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
