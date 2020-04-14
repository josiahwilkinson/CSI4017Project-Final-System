import java.io.*; 
import java.util.*; 

public class Relevance{
  String words;
  ArrayList<ProperDocument>relevant;
  ArrayList<ProperDocument>nonrelevant;
  boolean reuter;
  WordDictionary dic;
  
  String[] expansionWords = new String[0];
  
  double lenience = 0.5;  //  ther % of documents a word must be in and not be in to be added to the expansionWords
  
  
  
  public Relevance(String ws, ArrayList<ProperDocument>relevants,ArrayList<ProperDocument>nonrelevants,boolean reuters,WordDictionary dict) {
    this.words=ws;
    this.relevant=relevants;
    this.nonrelevant=nonrelevants;
    this.reuter=reuters;
    this.dic=dict;
  }
    
  void expand() {
    
    System.out.println("start relevance feedback construction");
    
    //  check that there are relevant documents
    if (relevant.size() > 0) {
      
      
    System.out.println("HERE 1");
    
    
      ArrayList<String> expansionWordsList = new ArrayList<String>();
      
      //  get dictionary
      HashMap<String, DictionaryWord> dictionary;
      if (reuter)
        dictionary = dic.reutersDictionaryMap;
      else
        dictionary = dic.uottawaDictionaryMap;
      //  parse through for words that only belong to relevant documents
      
      
    System.out.println("HERE 2");
    
    
      for (DictionaryWord word : relevant.get(0).words) {
      
      if (word.word.equals("mat"))
    System.out.println("FOUND MATH");
    //  System.out.println("HERE 3");
    
    
        boolean contains;
        //  check each relevant document
        float total = 0;
        for (int i = 0; i < relevant.size(); i++) {
          
      if (word.word.equals("mat"))
    System.out.println(i + " " + (word.posting(relevant.get(i).id) != null));
      
          if (word.posting(relevant.get(i).id) != null) {
            total++;
          }
        }
        //  check if total is within lenience
        if (total >= (float)relevant.size()*lenience)
          contains = true;
        else
          contains = false;
        
        
      if (word.word.equals("mat"))
    System.out.println(total + " " + (float)relevant.size()*lenience + " " + (total >= (float)relevant.size()*lenience) + " " + contains);
      
      
          
        //  if word passes
        if (contains) {
          total = 0;
          //  check each nonrelevant document
          for (int i = 0; i < nonrelevant.size(); i++) {
            if (word.posting(nonrelevant.get(i).id) == null) {
              total++;
            }
          }
      
      
    //  System.out.println("HERE 4");
    
    
          //  check if total is within lenience
          if (total >= (float)nonrelevant.size()*lenience)
            contains = true;
          else
            contains = false;
        
        
      if (word.word.equals("mat"))
    System.out.println(total + " " + (float)nonrelevant.size()*lenience + " " + (total >= (float)nonrelevant.size()*lenience) + " " + contains);
      
      
          //  if passes
          if (contains) {
            //  add word to expansion words
            expansionWordsList.add(word.word);
          }
        }
      
      
    //  System.out.println("HERE 5");
    
    
      }
      
      
    System.out.println("HERE 6");
      
      
    System.out.println("result size: " + expansionWordsList.size());
    
    
    
    
      
      //  set array
      expansionWords = new String[expansionWordsList.size()];
      
      for (int i = 0; i < expansionWordsList.size(); i++)
        expansionWords[i] = expansionWordsList.get(i);
        
      
    System.out.println("result sorting");
    System.out.println("result size: " + expansionWordsList.size());
    System.out.println("result size: " + expansionWords.length);
      //  sort query
      Arrays.sort(expansionWords); 
    System.out.println("result size: " + expansionWords.length);
    }
    
    //  list of words built
    System.out.println("Relevance feedback built");
    System.out.println("Relevance size: " + relevant.size());
    System.out.println("Nonrelevance size: " + nonrelevant.size());
    System.out.println(expansionWords.length + " words stored");
  }
}