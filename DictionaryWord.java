import java.util.ArrayList;
import java.io.*;
import java.lang.Math; 


//  stores a word and postings
class DictionaryWord {
  
  String word;
  
  ArrayList<Posting> postings = new ArrayList<Posting>();
  
  DictionaryWord(String w) {
    word = w;
  }
  
  void addPosting(Posting p) {
    postings.add(p);
  }
  
  //  returns the total occurances of the word in the document
  int totalOccurances() {
    int total = 0;
    for (Posting p : postings) {
      total += p.postings.length;
    }
    return total;
  }
  
  //  returns the total documents of the word in the document
  int totalDocuments() {
    return postings.size();
  }
  
  float inverseDocumentFrequency() {
    return (float)(Math.log(totalDocuments()/postings.size()));
  }
  //  input is documents id
  float termFrequency(int id) {
    if (posting(id) == null)
      return 0;
    return posting(id).postings.length;
  }
  
  //  returns the posting for the document matching the input id
  Posting posting(int id) {
    int index = 0;
    while (index < postings.size()) {
      if (postings.get(index).docID != id)
        index++;
      else
        break;
    }
    if (index == postings.size())
      return null;
    return postings.get(index);
  }
  
  float termFrequency() {
    return totalOccurances();
  }
  
  //  sets the weight for the specified document
  void setWeight(float w, int id) {
    if (posting(id) != null) {
      posting(id).setWeight(w);
    }
  }
  
  //  returns the weight on the specified document
  float weight(int docID) {
    //  if the word has a posting for the specific document
    if (posting(docID) != null) {
      return posting(docID).weight;
    }
    //  otherwise, return 0
    return 0;
  }
}

//  class for storing postings
class Posting {
  
  int docID;
  int[] postings;
  
  float weight;
  
  Posting(int id, int[] p) {
    docID = id;
    postings = p;
  }
  
  void addPosting(int p) {
    int[] postings2 = new int[postings.length+1];
    for (int i = 0; i < postings.length; i++)
      postings2[i] = postings[i];
    postings2[postings.length] = p;
    postings = postings2;
  }
  
  void setWeight(float w) {
    weight = w;
  }
}