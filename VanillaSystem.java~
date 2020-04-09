import java.util.ArrayList;
import java.util.Collections;
import java.io.*;
import java.io.FileNotFoundException; 
import java.lang.Math; 


class VanillaSystem {
  
  
  
  
  
  //  all documents
  static ProperDocument[] documents;  //  to be initialized after the construction and processing of raw documents
  
  
  
  static char[] punctuation = {'"', '\'', ',', '.', '-', '(', ')', '{', '}', '\\', '/', '=', '+', ';', ':', '|'};  //  used for punctuation removal
  
  
  
  /*
   Stemming rules:
   Every word to be stemmed is duplicated, meaning that, in addition to the base word, a new RawDocumentWord is saved to wordPostings with the same position
   -s -> -
   -es -> -e
   -ies -> -y
   -ing -> -
   -ing -> -e
   -tion -> -te
   -tion -> -e
   -ation -> -e
   -ers -> -e
   -ers -> -
   -er -> -e
   -er -> -
   -ly -> -
   -ed -> -e
   */
  static String[] stemmingRules = {"s", "", "es", "", "ies", "y", "ing", "", "ing", "e", "tion", "te", "tion", "e", "ation", "e", "ers", "e", "ers", "", "er", "e", "er", "", "ly", "", "ed", "e"};
  
  
  //  different trackers
  boolean caseFolding = true;
  boolean stemming = true;
  
  
  
  static Dictionary dictionary = new Dictionary(punctuation, stemmingRules);
  
  
  
  static BooleanQueryProcessing booleanQueryProcessing = new BooleanQueryProcessing(dictionary);
  static VectorQueryProcessing vectorQueryProcessing = new VectorQueryProcessing();
  
  
  
  
  public static void main(String args[]) {
    System.out.println("Program test");
    
    //  should print empty line
    System.out.println(("Program test").substring(0, 0));
    
    booleanQueryProcessing.processQuery("test AND (this OR that)");
    
    
    //  dictionary
    documents = dictionary.createDictionary(documents);
    
    
    //  testing time
    int[] results;
    results = booleanSearchWithQuery("effectively AND NOT baccalaureate");
    for (int i : results)
      System.out.println(i);
    results = booleanSearchWithQuery("effectively AND baccalaureate");
    for (int i : results)
      System.out.println(i);
    results = booleanSearchWithQuery("effectively");
    for (int i : results) {
      System.out.println(i);
      documents[i].displayDocument();
    }
    results = booleanSearchWithQuery("student AND NOT mat");
    for (int i : results)
      System.out.println(i);
    
    
    System.out.println(booleanSearchWithQuery("(query OR processing)").length);
    System.out.println(booleanSearchWithQuery("(query AND processing)").length);
  }
  
  
  void createDictionary() {
    dictionary = new Dictionary(punctuation, stemmingRules);
    documents = dictionary.createDictionary(documents);
  }
  
  
  
  //  an entered query is turned into queries and then queried on the dictionary to turn back relevent pages (array of the document IDs)
  static int[] booleanSearchWithQuery(String query) {
    //  get queries
    ArrayList<ArrayList<String>> queries = booleanQueryProcessing.processQuery(query);
    
    //  resulting docs
    ArrayList<Integer> docs = new ArrayList<Integer>();
    
    //  search with each query
    for (ArrayList<String> q : queries) {
      //  sort words into contain and not
      ArrayList<String> contains = new ArrayList<String>();
      ArrayList<String> not = new ArrayList<String>();
      for (String word : q) {
        if (word.charAt(0) == '!')
          not.add(word.substring(1, word.length()));
        else
          contains.add(word);
      }
      //  get DictionaryWords
      ArrayList<DictionaryWord> includedWords = new ArrayList<DictionaryWord>();
      for (String word : contains) {
        includedWords.add(dictionary.getWord(word));
        if (includedWords.get(includedWords.size()-1) == null)
          System.out.println("Null found: " + word);
      }
      ArrayList<DictionaryWord> removedWords = new ArrayList<DictionaryWord>();
      for (String word : not) {
        removedWords.add(dictionary.getWord(word));
        if (removedWords.get(removedWords.size()-1) == null)
          System.out.println("Null found: " + word);
      }
      
      //  search through dictionary words
      //  to do this, have a tracker for each DictionaryWord in both lists. If all of the docs in includeWords match, and all the docs in removedWords are higher, add the doc to the list
      int[] dictionaryTrackers = new int[includedWords.size()+removedWords.size()];
      for (int i : dictionaryTrackers)
        i = 0;
      
      boolean finished = false;
      while (!finished) {
        //  check all docs
        boolean matchingIncluded = true;
        int docID = includedWords.get(0).postings.get(dictionaryTrackers[0]).docID;
        for (int i = 1; i<includedWords.size(); i++) {
          //  if docID is different
          if (docID != includedWords.get(i).postings.get(dictionaryTrackers[i]).docID) {
            matchingIncluded = false;
            break;
          }
        }
        //  if docIDs matched, increase all removedWords while they are less than docID
        if (matchingIncluded) {
          for (int i = 0; i<removedWords.size(); i++) {
            //  System.out.println(i);
            while(removedWords.get(i).postings.get(dictionaryTrackers[includedWords.size()+i]).docID < docID && dictionaryTrackers[includedWords.size()+i] < removedWords.get(i).postings.size()-2) {
              dictionaryTrackers[includedWords.size()+i]++;
              //  System.out.println(dictionaryTrackers[includedWords.size()+i]);
            }
            //  test if any of the removedWords match docID
            if (removedWords.get(i).postings.get(dictionaryTrackers[includedWords.size()+i]).docID == docID) {
              matchingIncluded = false;
              break;
            }
          }
        }
        
        //  check if matchingIncluded is (still) true, and if so, add the docID to the list
        if (matchingIncluded) {
          //  System.out.println("adding document: " + docID);
          docs.add(docID);
        }
        
        //  increase includedWord with the lowest docID
        docID = includedWords.get(0).postings.get(dictionaryTrackers[0]).docID;
        int increase = 0;
        for (int i = 1; i < includedWords.size(); i++) {
          if (dictionaryTrackers[i] < includedWords.get(i).postings.size()) {
            if (docID > includedWords.get(i).postings.get(dictionaryTrackers[i]).docID) {
              docID = includedWords.get(i).postings.get(dictionaryTrackers[i]).docID;
              increase = i;
            }
          }
        }
        dictionaryTrackers[increase]++;
        
        //  check if finished (any of the dictionary trackers are out of bounds; would only happen to the most recently increased one)
        if (dictionaryTrackers[increase] == includedWords.get(increase).postings.size())
          finished = true;
      }
    }
    
    //  at this point, all matching documents have been added to the docID arrayList, so sort, remove duplicates, and return the list of document IDs
    Collections.sort(docs);
    for (int i = 1; i < docs.size(); i++) {
      if (docs.get(i) == docs.get(i-1)) {
        docs.remove(i);
        i--;
      }
    }
    //  convert arrayList to array
    int[] documents = new int[docs.size()];
    for (int i = 0; i < docs.size(); i++) {
      documents[i] = docs.get(i);
    }
    return documents;
  }
  
  
  
  //  an entered query is turned into queries and then queried on the dictionary to turn back relevent pages (array of the document IDs)
  static int[] vectorSearchWithQuery(String query) {
    //  get queries
    String[] queries = vectorQueryProcessing.processQuery(query);
    
    //  set base weight for compare against
    float[] baseVector = new float[queries.length];
    for (int i = 0; i < queries.length; i++) {
      baseVector[i] = 1;
    }
    
    
    //  document weights
    float[][] weightVectors = new float[documents.length][];
    for (int i = 0; i < documents.length; i++) {
      weightVectors[i] = new float[queries.length];
    }
    
    //  resulting weights
    ArrayList<DocumentWeight> weights = new ArrayList<DocumentWeight>();
    
    //  set the weights for each document for all words
    for (int i = 0; i < documents.length; i++) {
      for (int j = 0; j < queries.length; j++) {
        weightVectors[i][j] = dictionary.weight(dictionary.getWord(queries[j]), i);
      }
      //  normalize vectors
      //  get total
      double totalLength = 0;
      for (int j = 0; j < queries.length; j++) {
        totalLength += weightVectors[i][j]*weightVectors[i][j];
      }
      totalLength = Math.sqrt(totalLength);
      //  divide by total length
      for (int j = 0; j < queries.length; j++) {
         weightVectors[i][j] = weightVectors[i][j]/(float)totalLength;
      }
      
      //  get final weight (dot product)
      //  as the weights of the base are all 1, the dot product is simply the addition of its wieghts
      //  overwrite the weightVectors[i][0] for convienence
      for (int j = 1; j < queries.length; j++) {
        weightVectors[i][0] += weightVectors[i][j];
      }
      //  only add to weights if the weight is greater than 0
      if (weightVectors[i][0] > 0)
        weights.add(new DocumentWeight(i, weightVectors[i][0]));
    }
    
    Collections.sort(weights);
    
    int[] results = new int[Math.min(weights.size(), 15)];
    for (int i = 0; i < results.length; i++) {
      results[i] = weights.get(i).docID;
    }
    
    return results;
  }
  
  
  
  
  
  String[] condense (ArrayList<ArrayList<String>> lists) {
    ArrayList<String> l = new ArrayList<String>();
    for (ArrayList<String> list : lists) {
      for (String s : list) {
        l.add(s);
      }
    }
    Collections.sort(l);
    for (int i = 0; i < l.size()-1; i++) {
      if (l.get(i).equals(l.get(i+1))) {
        l.remove(i);
        i--;
      }
    }
    String[] list = new String[l.size()];
    for (int i = 0; i < l.size(); i++) {
      list[i] = l.get(i);
    }
    return list;
  }
}


