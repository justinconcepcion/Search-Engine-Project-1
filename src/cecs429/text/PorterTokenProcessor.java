package cecs429.text;

import java.util.ArrayList;
import java.util.Arrays;

import Porter2Stemmer.SnowballStemmer;

public class PorterTokenProcessor implements MultipleTokenProcessor {
	public ArrayList<String> processToken(String term) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		
		ArrayList<String> tokens = new ArrayList<String>();
		ArrayList<String> terms = new ArrayList<String>();
		String temp = "";
		temp = (removeNonAlphaNumeric(term));
		temp = removeQuotes(temp);
		tokens = removeHyphens(temp);
		System.out.println(tokens);
		
		for (String token: tokens) {
			Class stemClass = Class.forName("Porter2Stemmer." + "englishStemmer");
	    	SnowballStemmer stemmer = (SnowballStemmer) stemClass.newInstance();
	    	stemmer.setCurrent(token);
	    	stemmer.stem();
	    	String stemmedTerm = stemmer.getCurrent();
	    	terms.add(stemmedTerm);
		}
		return terms;
	}
	private static String removeNonAlphaNumeric(String term) {
		String temp = "";
		if(!(Character.isDigit(term.charAt(0)) || Character.isLetter(term.charAt(0)))) {
			//do nothing
		}
		else {
			temp += term.charAt(0);
		}
		
		for (int i = 1; i < term.length()-1; i++) {
			temp += term.charAt(i);
		}
		
		if(!(Character.isDigit(term.charAt(term.length()-1)) || Character.isLetter(term.charAt(term.length()-1)))) {
			//do nothing
		}
		
		else {
			temp += term.charAt(term.length()-1);
		}

		return temp;
	}
	
	private static String removeQuotes(String term) {
		String temp = "";
		for (int i = 0; i < term.length(); i++) {
			if((term.charAt(i) == '"' || term.charAt(i) == '\'')) {
				//do nothing
			}
			else {
				temp +=  term.charAt(i);
			}
		}
		return temp;
	}
	
	private static ArrayList<String> removeHyphens(String term) {
		
		ArrayList<String> hyphenWords = new ArrayList<String>();
		String[] splitString = term.split("-");
		hyphenWords.addAll(Arrays.asList(splitString));
		String temp = "";
		for (int i = 0; i < splitString.length; i++) {
			temp += splitString[i];
		}
		hyphenWords.add(temp);
		return hyphenWords;
	}

}
