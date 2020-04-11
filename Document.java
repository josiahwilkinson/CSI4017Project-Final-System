import java.util.ArrayList;
import java.io.*;


//  stores the bag of words/postings from each line of a document
class RawDocument {
  int id;
  String title = "";
  String date = "";
  String topics = "";
  String places = "";
  String people = "";
  String orgs = "";
  String exchanges = "";
  String companies = "";
  
  String dateline = "";
  
  //  ArrayList<RawDocumentWord> words = new ArrayList<RawDocumentWord>();
  ArrayList<String> words = new ArrayList<String>();
  String description = "";
  
  RawDocument(int i) {
    id = i;
  }
  RawDocument(int i, String t) {
    id = i;
    title = t;
  }
  
  void addTitle(String t) {
    title = t;
  }
  void addDate(String d) {
    date = d;
  }
  void addTopics(String t) {
    topics = t;
  }
  void addPlaces(String d) {
    places = d;
  }
  void addPeople(String t) {
    people = t;
  }
  void addOrgs(String d) {
    orgs = d;
  }
  void addExchanges(String t) {
    exchanges = t;
  }
  void addCompanies(String d) {
    companies = d;
  }
  void addDateLine(String d) {
    dateline = d;
  }
  
  /*
  void addWords(ArrayList<RawDocumentWord> w) {
    if (w != null) {
      for (RawDocumentWord word : w) {
        if (!word.word.equals(""))  //  check not an empty word
          words.add(word);
      }
    }
  }
  void addWords(RawDocumentWord[] w) {
    if (w != null) {
      for (RawDocumentWord word : w) {
        if (!word.word.equals(""))  //  check not an empty word
          words.add(word);
      }
    }
  }
  */
  
  void addWords(ArrayList<String> w) {
    if (w != null) {
      for (String word : w) {
        if (!word.equals(""))  //  check not an empty word
          words.add(word);
      }
    }
  }
  void addWords(String[] w) {
    if (w != null) {
      for (String word : w) {
        if (!word.equals(""))  //  check not an empty word
          words.add(word);
      }
    }
  }
  void addLine(String line) {
    description = description + " " + line;
  }
}

/*
//  simply stores the word and a posting
//  required as the stemming duplicates the word, so the position cannot be reliably used for posting position
class RawDocumentWord {
  
  String word;
  int post;
  
  RawDocumentWord(String w, int p) {
    word = w;
    post = p;
  }
}
*/

//  stores the bag of words/postings from each line of a document
class ProperDocument {
  int id;
  String title;
  String date;
  String topics;
  String places;
  String people;
  String orgs;
  String exchanges;
  String companies;
  
  String dateline;
  
  ArrayList<DictionaryWord> words = new ArrayList<DictionaryWord>();
  String description;
  
  ProperDocument(RawDocument doc) {
    //  set basic info
    id = doc.id;
    title = doc.title;
    date = doc.date;
    topics = doc.topics;
    places = doc.places;
    people = doc.people;
    orgs = doc.orgs;
    exchanges = doc.exchanges;
    companies = doc.companies;
    dateline = doc.dateline;
    
    description = doc.description;
    
    //  System.out.println("Creating proper course: " + title);
    //  convert RawDocumentWord to Posting
    //  create single of each
    for (String docWord : doc.words) {
      boolean found = false;
      for (DictionaryWord word : words) {
        if (word.word.equals(docWord)) {
          found = true;
          word.postings.get(0).postings++;
        }
      }
      if (!found) {
        words.add(new DictionaryWord(docWord));
        words.get(words.size()-1).addPosting(new Posting(id, 1));  //  initialize all postings to have 1 as each word here will be the first encounter
      }
    }
    /*
     //  compound duplicates
     for (int i = 0; i < words.size(); i++) {
     for (int j = 0; j < words.size(); j++) {
     if (i != j) {  //  don't do on same word
     if (words.get(i).word.compareTo(words.get(j).word) == 0)  {  //  words are identical
     //  add posting positing to former and remove later
     words.get(i).postings.get(0).addPosting(words.get(j).postings.get(0).postings);
     words.remove(j);
     }
     }
     }
     }
     */
  }
  
  //  prints the document to the console
  void printDocument() {
    System.out.println("Document " + id + ":");
    for (DictionaryWord word : words) {
      System.out.println();
      System.out.print(word.word + " || ");
      for (Posting posting : word.postings) {
        System.out.print(posting.docID + ": ");
        System.out.print(posting.postings);
        System.out.print(" | ");
      }
    }
  }
  
  //  prints the document as to be displayed
  void displayDocument() {
    System.out.println(title + ":");
    String[] lines = description.split("  ");
    for (String line : lines)
      System.out.println(line);
  }
}