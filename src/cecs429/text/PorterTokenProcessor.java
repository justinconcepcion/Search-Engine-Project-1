package cecs429.text;

import java.util.ArrayList;
import java.util.List;

import cecs429.porterstemmer.SnowballStemmer;

public class PorterTokenProcessor implements MultipleTokenProcessor {

    public List<String> processToken(String term) {

        ArrayList<String> tokens = new ArrayList<String>();
        ArrayList<String> terms = new ArrayList<String>();
        String temp = "";
        temp = removeNonAlphaNumeric(term);
        temp = removeQuotes(temp);
        if (temp.contains("-")){
            // TODO To ask, if we need to remove hyphens while rank indexing
            tokens = (ArrayList<String>) removeHyphens(temp.toLowerCase());
        }
        else {
            tokens.add(temp.toLowerCase());
        }

        try {
            for (String token : tokens) {
                Class stemClass = Class.forName("cecs429.porterstemmer." + "EnglishStemmer");
                SnowballStemmer stemmer = (SnowballStemmer) stemClass.newInstance();
                stemmer.setCurrent(token);
                stemmer.stem();
                String stemmedTerm = stemmer.getCurrent();
                terms.add(stemmedTerm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return terms;
    }

    private static String removeNonAlphaNumeric(String term) {
        int i = 0;
        while (i < term.length() && (!(Character.isDigit(term.charAt(i)) || Character.isLetter(term.charAt(i))))) {
            i++;
        }
        term = term.substring(i);
        int j = term.length();
        while (j > 0 && !(Character.isDigit(term.charAt(j - 1)) || Character.isLetter(term.charAt(j - 1))))
            j--;
        term = term.substring(0, j);

        return term;
    }

    private static String removeQuotes(String term) {
        term = term.replace("\"", "");
        term = term.replace("\'", "");
        return term;
    }

    private static List<String> removeHyphens(String term) {

        List<String> hyphenWords = new ArrayList<String>();
        String[] splitString = term.split("-");
        term = term.replace("-", "");
        hyphenWords.add(term);
        for (String str : splitString) {
            if (str.trim().length() > 0) {
                hyphenWords.add(str);
            }
        }
        return hyphenWords;
    }

}
