import java.io.*; 
import java.util.*; 

public class Relevance{
  String words;
  ArrayList<ProperDocument>relevant;
  ArrayList<ProperDocument>nonrelevant;
  boolean reuter;
  WordDictionary dic;
  
  String[] expansionWords = new String[0];
  
  
  
  public Relevance(String ws, ArrayList<ProperDocument>relevants,ArrayList<ProperDocument>nonrelevants,boolean reuters,WordDictionary dict) {
    this.words=ws;
    this.relevant=relevants;
    this.nonrelevant=nonrelevants;
    this.reuter=reuters;
    this.dic=dict;
    
    //  check that there are relevant documents
    if (relevants.size() > 0) {
      
      ArrayList<String> expansionWordsList = new ArrayList<String>();
      
      //  get dictionary
      HashMap<String, DictionaryWord> dictionary;
      if (reuters)
        dictionary = dict.reutersDictionaryMap;
      else
        dictionary = dict.uottawaDictionaryMap;
      //  parse through for words that only belong to relevant documents
      for (DictionaryWord word : relevants.get(0).words) {
        boolean contains = true;
        //  check each relevant document
        for (int i = 1; i < relevants.size(); i++) {
          if (word.posting(relevants.get(i).id) == null) {
            contains = false;  //  if it is not, set contains to false and break
            break;
          }
        }
        //  if word passes
        if (contains) {
          //  check each nonrelevant document
          for (int i = 1; i < relevants.size(); i++) {
            if (word.posting(nonrelevants.get(i).id) != null) {
              contains = false;
              break;
            }
          }
          //  if passes
          if (contains) {
            //  add word to expansion words
            expansionWordsList.add(word.word);
          }
        }
      }
      
      //  set array
      expansionWords = (String[])expansionWordsList.toArray();
      //  sort query
      Arrays.sort(expansionWords); 
    }
  }
}