import java.util.ArrayList;
import java.io.*;

//  for query processing
class VectorQueryProcessing {
  
  //  used just for testing
  public static void main(String args[]) {
  }
  
  //  returns an array of strings for queries
  static String[] processQuery(String query, String[] stemmingRules, UI ui) {
    
    System.out.println(query);
    System.out.println(stemmingRules == null);
    System.out.println(stemmingRules.length);
    System.out.println(ui == null);
    
    String[] queryWords = query.split(" ");
    
    for (int i = 0; i < queryWords.length; i++) {
      queryWords[i] = queryWords[i].toLowerCase();
    }
    
    //  new list for additions
    ArrayList<String> newQueryWords = new ArrayList<String>();
    for (int i = 0; i < queryWords.length; i++)
      newQueryWords.add(queryWords[i]);
    
    // stemming
    for (int i = 0; i < queryWords.length; i++) {
      for (int j = 0; j < stemmingRules.length; j += 2) {
        if (queryWords[i].length() > stemmingRules[j].length()) {  //  check if within length
          if (queryWords[i].substring(queryWords[i].length()-stemmingRules[j].length(), queryWords[i].length()).equals(stemmingRules[j]))  //  check for matching ending
            newQueryWords.add(queryWords[i].substring(0, queryWords[i].length()-stemmingRules[j].length())+stemmingRules[j+1]);  //  add new wordPosting with same posting position
        }
      }
    }
    
    //  adds synonyms
    for (int i = 0; i < queryWords.length; i++) {
      for (String word : synonym(queryWords[i], ui)) {
        newQueryWords.add(word);
      }
    }
    
    queryWords = new String[newQueryWords.size()];
    for (int i = 0; i < queryWords.length; i++)
      queryWords[i] = newQueryWords.get(i);
      
    return queryWords;
  }
  
  
  
  //  adds synonyms with the user's permission
  /*
   * NOTE: Due to issues with using WordNet, the synonym functionality is not fully implemented.
   * However, we thought it best to implement the rest of the algorithm to demonstrate that we understand the concept, even if we could not properly import the WordNet API
   * Some lines have portions commented out so that the program can run with the missing features
   */
  static ArrayList<String> synonym(String word, UI ui) {
    //  check if the word is in the WordNet
    
    ArrayList<String> synonyms = new ArrayList<String>();
    
    //  if (ui.wordNet.contains(word)) {
    
    //  ask user if they want to expand the word with the first 5 synonyms
    String[] words;  //  = ui.wordNet.synonyms(word)
    boolean[] response = new boolean[0];  //  = new boolean[Math.min(5, words.length)];
    for (int i = 0; i < response.length; i++) {
      response[i] = false; //  = ui.needexpansion(word, words[i]);
    }
    for (int i = 0; i < response.length; i++) {
      //  check if user confirmed this specific word
      if (response[i]) {
        //  synonyms.add(words[i]);  //  add synonym as word
      }
    }
    
    //  }
    return synonyms;  //  if the ui.wordNet doesn't contain the word, will just return an empty list
  }
}


class DocumentWeight implements Comparable<DocumentWeight> {
  
  int docID;
  float weight;
  
  DocumentWeight(int id, float w) {
    docID = id;
    weight = w;
  }
  
  //  to sort by weights
  @Override     
  public int compareTo(DocumentWeight other) {
    if (weight < other.weight)
      return -1;
    if (weight > other.weight)
      return 1;
    return 0;     
  }   
}