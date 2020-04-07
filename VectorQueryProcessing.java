import java.util.ArrayList;
import java.io.*;

//  for query processing
class VectorQueryProcessing {
  
  //  used just for testing
  public static void main(String args[]) {
  }
  
  //  returns an array of strings for queries
  static String[] processQuery(String query) {
    
    System.out.println(query);
    
    String[] queryWords = query.split(" ");
    
    for (int i = 0; i < queryWords.length; i++) {
      queryWords[i] = queryWords[i].toLowerCase();
    }
    
    return queryWords;
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