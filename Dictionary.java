import java.util.ArrayList;
import java.util.Collections;
import java.io.*;
import java.io.FileNotFoundException; 
import java.lang.Math; 



//  dictionary class
class Dictionary {
  
  
  
  
  static String[] stemmingRules;
  static char[] punctuation;
  
  
  static ArrayList<DictionaryWord> words = new ArrayList<DictionaryWord>();
  
  
  public static void main(String args[]) {
    ProperDocument[] documents = new ProperDocument[0];
    createDictionary(documents);
  }
  
  
  
  Dictionary(char[] p, String[] sr) {
    stemmingRules = sr;
    punctuation = p;
  }
  
  
  public static ProperDocument[] createDictionary(ProperDocument[] documents) {
    
    
    
    //  preprocessing
    try {
      //  create arrayList of raw documents (word bags) to be made into a dictionary later
      ArrayList<RawDocument> rawDocuments = new ArrayList<RawDocument>();
      boolean french = false;  //  turned on if french and ingores lines
      int positionCounter = 1;  //  required to keep postition of words between document lines (must be 1 as 1 is subtracted)
      //  read lines
      //  source: https://stackoverflow.com/questions/5868369/how-to-read-a-large-text-file-line-by-line-using-java
      File classes = new File("classes.txt");
      BufferedReader br = new BufferedReader(new FileReader(classes));
      String line = br.readLine();  //  get first line
      while (line != null) {
        //  casefold line
        String originalLine = line;  //  copy to preserve capitals
        line = line.toLowerCase();
        //  process line
        //  if it detects that the first 3 letters are "adm", "psy", or "mat", it creates a new document starting with that line
        //  System.out.println(line);
        if (line.length() > 8)  //  minimum length for course code
          if (line.substring(0, 3).equals("adm") || line.substring(0, 3).equals("psy") || line.substring(0, 3).equals("mat") || line.substring(0, 3).equals("csi")) {  //  check for course code beginning
          //  check if english course
          if (Character.getNumericValue(line.charAt(5)) < 5) {  //  english section
            rawDocuments.add(new RawDocument(rawDocuments.size(), line.substring(0, 8)));
            french = false;  //  set french flag to false
            positionCounter = 1;  //  reset positionCounter
          }
          //  course is french
          else {
            french = true;  //  turn french flag on
          }
        }
        
        //  check if currently english
        if (!french) {
          //  cut on spaces
          String[] words = line.split(" ");
          //  word/posting object
          ArrayList<RawDocumentWord> wordPostings = new ArrayList<RawDocumentWord>();
          //  cycle through words
          for (int i = 0; i < words.length; i++) {
            if (words[i].length() > 0) {
              //  System.out.println(words[i]);
              words[i] = cleanPunctuation(words[i]);  //  remove punctuation from a word
              //  System.out.println(words[i]);
              
              //  now that all punctuation has bben trimmed, save to wordPostings to preserve posting (as positions will be messed up during stemming)
              wordPostings.add(new RawDocumentWord(words[i], i+positionCounter-1));
              
              //  basic phrase recognition for course codes
              //  check if the word is a course code; if so, check if next is a number and add as phrase; otherwise, disregard
              boolean courseCode = false;
              if (words[i].length() == 3) {  //  minimum length for course code
                if (words[i].equals("adm") || words[i].equals("psy") || words[i].equals("mat") || words[i].equals("csi")) {  //  check for course code beginning
                  //  check if following word is there
                  if (words.length > i+1)
                    //  clean next word
                    words[i+1] = cleanPunctuation(words[i+1]);
                  //  check if next word is a number
                  if (isNumber(words[i+1])) {
                    courseCode = true;  //  set course code flag so as to know not to stem
                    //  add to current word for phrase treatment
                    words[i] += words[i+1];
                    //  wordPostings.get(i).word += words[i+1];  //  modify the word posting as well
                    wordPostings.add(new RawDocumentWord(words[i], i+positionCounter-1));  //  add course code to posting
                  }
                }
              }
              
              //  else, stem
              if (!courseCode) {
                
                //  stemming
                for (int j = 0; j < stemmingRules.length; j += 2) {
                  if (words[i].length() > stemmingRules[j].length())  //  check if within length
                    if (words[i].substring(words[i].length()-stemmingRules[j].length(), words[i].length()).equals(stemmingRules[j]))  //  check for matching ending
                    wordPostings.add(new RawDocumentWord(words[i].substring(0, words[i].length()-stemmingRules[j].length())+stemmingRules[j+1], i+positionCounter-1));  //  add new wordPosting with same posting position
                }
              }
              
            }
            //  otherwise, if a 0 length word was found (likely some double space or punction turned to nothing), then decrement positionCounter so that it will be skipped in the position counting for posting
            else {
              positionCounter--;
            }
          }
          //  send off list of words to raw document
          rawDocuments.get(rawDocuments.size()-1).addWords(wordPostings);
          
          //  add line to document
          rawDocuments.get(rawDocuments.size()-1).addLine(originalLine);
          
          //  add length to position counter
          positionCounter += words.length;
        }
        
        //  get next line
        line = br.readLine();
      }
      
      
      
      //  all documents have been scanned and stored as RawDocuments with RawDocumentWord word/postings
      //  now, create proper documents from raw documents (RawDocumentWord to DictionaryWord)
      documents = new ProperDocument[rawDocuments.size()];
      for (int i = 0; i < rawDocuments.size(); i++) {
        documents[i] = new ProperDocument(rawDocuments.get(i));
        //  create dictionary compiling all proper documents
        addDocument(documents[i]);
      }
      
      //  print out document 1 (0)
      //  documents[0].printDocument();
      
      System.out.println();
      System.out.println();
      
      //  print out dictionary
      System.out.println("Finished constructing dictionary");
      System.out.println();
      //  dictionary.printDictionary();
      
      
      
      
      
      //  set weights for all postings
      System.out.println("adding weights to dictionary");
      System.out.println();
      for (DictionaryWord word : words) {
        for (int i = 0; i < documents.length; i++) {
          if (word.posting(i) != null) {  //  check that there is the posting for the document with this as the id
            setWeight(word, i);
          }
        }
      }
      System.out.println("finished adding weights to dictionary");
      System.out.println();
      
      
    }
    catch (Exception e) {
      System.out.println("Did not work");
      System.out.println(e);
    }
    
    //  return resulting documents
    return documents;
  }
  
  
  //  cleans punctuation from a word
  static String cleanPunctuation(String s) {
    //  back
    while (punctation(s.charAt(s.length() - 1))) {
      s = s.substring(0, s.length()-1);
      //  exit if length is 0
      if (s.length() == 0)
        break;
    }
    
    //  check if word is empty (as in, it was only punctuation and has been all removed)
    //  (running further would cause crash)
    if (s.length() == 0)
      return s;
    
    //  front
    while (punctation(s.charAt(0)))
      s = s.substring(1, s.length());
    //  return result
    return s;
  }
  
  
  //  function to check if the input character is in the punctuation array
  static boolean punctation(char c) {
    for (int i = 0; i < punctuation.length; i++)
      if (new Character(c).equals(new Character(punctuation[i])))
      return true;
    return false;
  }
  
  
  //  function to check if the input string is a number
  static boolean isNumber(String s) {
    char[] numbers = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    for (int i = 0; i < s.length(); i++) {
      Character c = new Character(s.charAt(i));
      boolean charIsNum = false;
      for (int n = 0; n < numbers.length; n++) {
        if (!(c.equals(new Character(numbers[n]))))
          charIsNum = true;
      }
      if (!charIsNum)
        return false;
    }
    return true;
  }
  
  
  
  
  
  //  integrates the Proper document into the dictionary
  static void addDocument(ProperDocument doc) {
    //  scan all dictionary words in doc
    for (DictionaryWord docWord : doc.words) {
      //  check for matching word in dictionary
      boolean foundMatch = false;
      for (DictionaryWord dicWord : words) {
        if (docWord.word.compareTo(dicWord.word) == 0) {
          foundMatch = true;
          //  add posting
          dicWord.addPosting(docWord.postings.get(0));
        }
      }
      //  if match could not be found, add (shallow copy of) word to dictionary
      if (!foundMatch) {
        DictionaryWord dw = new DictionaryWord(docWord.word);
        dw.addPosting(docWord.postings.get(0));  //  can do 0 as each document only has 1 posting (containing 1+ positions)
        words.add(dw);
      }
    }
  }
  
  //  returns true if the dictionary contains the input word
  boolean hasWord(String w) {
    for (DictionaryWord word : words) {
      if (word.word.equals(w))
        return true;
    }
    return false;
  }
  
  //  returns the DictionaryWord matching the input word
  DictionaryWord getWord(String w) {
    for (DictionaryWord word : words) {
      if (word.word.equals(w))
        return word;
    }
    return null;
  }
  
  //  prints the dictionary to the console
  void printDictionary() {
    System.out.println("Dictionary:");
    for (DictionaryWord word : words) {
      System.out.println();
      System.out.print(word.word + " || ");
      for (Posting posting : word.postings) {
        System.out.print(posting.docID + ": {");
        System.out.print(posting.postings[0]);
        for (int i = 1; i<posting.postings.length; i++) {
          System.out.print(", " + posting.postings[i]);
        }
        System.out.print("} | ");
      }
    }
  }
  
  
  
  
  
  
  
  
  static float inverseDocumentFrequency(DictionaryWord word) {
    return (float)(Math.log(words.size()/word.totalDocuments()));
  }
  
  static float termFrequency(DictionaryWord word, int docID) {
    return (float)(Math.log(1 + word.termFrequency(docID)));
  }
  
  static void setWeight(DictionaryWord word, int docID) {
    float weight = inverseDocumentFrequency(word) * termFrequency(word, docID);
    word.setWeight(weight, docID);
  }
  
  //  get weights for input document
  static float weight(DictionaryWord word, int docID) {
    return word.weight(docID);
  }
  
  
}

//  DictionaryWord stored in main VanillaSystem 