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
      
      RawDocument currentDocument = null;
      
      for (int i = 0; i < 22; i++) {
        System.out.println("Parsing file: " + i);
        
        File classes;
        if (i < 10)
          classes = new File("Reuters21578/reut2-00" + i + ".sgm");
        else
          classes = new File("Reuters21578/reut2-0" + i + ".sgm");
        
        BufferedReader br = new BufferedReader(new FileReader(classes));
        String line = br.readLine();  //  get first line
        
        int lineCounter = 0;
        while (line != null) {
          
          //  casefold line
          String originalLine = line;  //  copy to preserve capitals
          line = line.toLowerCase();
          lineCounter++;
          
          //  if (i == 2)
          //  System.out.println(i + " " + lineCounter + " " + rawDocuments.size());
          //  System.out.println(line);
          //  System.out.println(originalLine);
          
          //  check for new beginning
          if (originalLine.length() > 0) {
            if (originalLine.length() > 8) {
              if (originalLine.substring(0, 8).equals("<REUTERS")) {
                //  get id
                String[] parts = originalLine.split("NEWID=");
                int id = Integer.parseInt(parts[1].substring(1, parts[1].length()-2));
                
                rawDocuments.add(new RawDocument(id));
                currentDocument = rawDocuments.get(rawDocuments.size()-1);
                
                //  System.out.println("document: " + id +" "+rawDocuments.size());
                //  System.out.println("correct length: " + (id == rawDocuments.size()));
              }
            }
            
            if (currentDocument != null) {
              //  process document
              //  System.out.println("Parsing document:" + " " + rawDocuments.size()-1);
              
              //  date
              if (originalLine.length() > 6) {
                if (originalLine.substring(0, 6).equals("<DATE>")) {
                  currentDocument.addDate(originalLine.substring(6, originalLine.length()-7));
                  currentDocument.addWords(RawDocumentWordsFromLine(line.substring(6, originalLine.length()-7)));
                }
              }
              
              //  topics
              if (originalLine.length() > 6) {
                if (originalLine.substring(0, 6).equals("<TOPIC")) {
                  currentDocument.addDate(originalLine.substring(8, originalLine.length()-9));
                  currentDocument.addWords(RawDocumentWordsFromLine(line.substring(8, originalLine.length()-9)));
                }
              }
              
              //  places
              if (originalLine.length() > 6) {
                if (originalLine.substring(0, 6).equals("<PLACE")) {
                  currentDocument.addDate(originalLine.substring(8, originalLine.length()-9));
                  currentDocument.addWords(RawDocumentWordsFromLine(line.substring(8, originalLine.length()-9)));
                }
              }
              
              //  people
              if (originalLine.length() > 6) {
                if (originalLine.substring(0, 6).equals("<PEOPL")) {
                  currentDocument.addDate(originalLine.substring(8, originalLine.length()-9));
                  currentDocument.addWords(RawDocumentWordsFromLine(line.substring(8, originalLine.length()-9)));
                }
              }
              
              //  orgs
              if (originalLine.length() > 6) {
                if (originalLine.substring(0, 5).equals("<ORGS>")) {
                  currentDocument.addDate(originalLine.substring(6, originalLine.length()-7));
                  currentDocument.addWords(RawDocumentWordsFromLine(line.substring(6, originalLine.length()-7)));
                }
              }
              
              //  exchanges
              if (originalLine.length() > 6) {
                if (originalLine.substring(0, 6).equals("<EXCHA")) {
                  currentDocument.addDate(originalLine.substring(11, originalLine.length()-12));
                  currentDocument.addWords(RawDocumentWordsFromLine(line.substring(11, originalLine.length()-12)));
                }
              }
              
              //  companies
              if (originalLine.length() > 6) {
                if (originalLine.substring(0, 6).equals("<COMPA")) {
                  currentDocument.addDate(originalLine.substring(11, originalLine.length()-12));
                  currentDocument.addWords(RawDocumentWordsFromLine(line.substring(11, originalLine.length()-12)));
                }
              }
              
              //  dateline (and body)
              if (originalLine.length() > 6) {
                if (originalLine.substring(0, 6).equals("<DATEL")) {
                  //  get dateline
                  int endOfDate = 6;
                  while (originalLine.length() > (endOfDate+9) && !originalLine.substring(endOfDate, (endOfDate+6)).equals("</DATE"))
                    endOfDate++;
                  
                  String dateLine;
                  //  if "</DATE" was NOT found"
                  if (originalLine.length() == (endOfDate+9)) {
                    dateLine = originalLine.substring(10, originalLine.length());
                    line = br.readLine();
                    originalLine = line;
                    line = line.toLowerCase();
                    lineCounter++;
                  }
                  //  if "</DATE" was found"
                  else {
                    dateLine = originalLine.substring(10, endOfDate);
                  }
                  
                  //  trim spaces
                  while(dateLine.charAt(0) == ' ') {
                    dateLine = dateLine.substring(1, dateLine.length());
                    if (dateLine.length() == 0)
                      break;
                  }
                  if (dateLine.length() > 0) {
                    while(dateLine.charAt(dateLine.length()-1) == ' ') {
                      dateLine = dateLine.substring(0, dateLine.length()-1);
                    }
                  }
                  currentDocument.addDateLine(dateLine);
                  currentDocument.addWords(RawDocumentWordsFromLine(dateLine));
                  
                  //  get text body
                  //  first line
                  String[] parts = originalLine.split("</DATELINE><BODY>");
                  currentDocument.addLine(parts[1]);
                  //  get next line
                  line = br.readLine();
                  originalLine = line;
                  line = line.toLowerCase();
                  lineCounter++;
                  
                  //  offset counter for word postings
                  int offset = 0;
                  while(!originalLine.equals("</REUTERS>")) {
                    if (!line.equals("")) {
                      currentDocument.addLine(originalLine);
                      ArrayList<RawDocumentWord> words = RawDocumentWordsFromLine(line, offset);
                      offset += words.size();
                      currentDocument.addWords(words);
                    }
                    line = br.readLine();
                    //  casefold line
                    originalLine = line;  //  copy to preserve capitals
                    line = line.toLowerCase();
                    lineCounter++;
                  }
                  //  at this point, "</REUTERS>" has been reached, ending the document
                  currentDocument = null;
                }
              }
            }
          }
          
          //  get next line
          line = br.readLine();
        }
      }
      
      
      
      System.out.println("Finished creating raw documents");
      
      
      //  print out dictionary
      System.out.println("Finished constructing dictionary");
      System.out.println();
      //  dictionary.printDictionary();
      
      
      
      ArrayList<String> wordList = new ArrayList<String>();
      
      documents = new ProperDocument[rawDocuments.size()];
      for (int i = 0; i < rawDocuments.size(); i++) {
        if (i%25 == 0 || i > 4590)
          System.out.println(i +" " + wordList.size() +" "+ words.size());
        documents[i] = new ProperDocument(rawDocuments.get(i));
        //  create dictionary compiling all proper documents
        
        addDocument(documents[i], wordList);
      }
      System.out.println("Finished creating proper documents");
      
      
      
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
      
      
      
      
      System.out.println("Did work");
    }
    
    
    
    catch (Exception e) {
      System.out.println("Did not work");
      System.out.println(e);
    }
    
    
    
    
    /*
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
     */
    
    //  return resulting documents
    return documents;
  }
  
  
  
  //  shortcut for non-body lines
  static ArrayList<RawDocumentWord> RawDocumentWordsFromLine(String line) {
    //  System.out.println("HERE 1");
    //  System.out.println(line);
    return RawDocumentWordsFromLine(line, 0, false);
  }
  //  shortcut for body lines
  static ArrayList<RawDocumentWord> RawDocumentWordsFromLine(String line, int offset) {
    //  System.out.println("HERE 2");
    //  System.out.println(line);
    return RawDocumentWordsFromLine(line, offset, true);
  }
  
  //  returns an array of RawDocumentWord from the input line
  static ArrayList<RawDocumentWord> RawDocumentWordsFromLine(String line, int offset, boolean body) {  //  if the line is from the body, save postings; otherwise, save as -1 (not that postings are actually ever really used)
    //  check if empty word
    if (line.equals(""))
      return null;
    //  System.out.println("HERE 3");
    String[] words = wordsFromLine(line);
    //  System.out.println("HERE 4");
    ArrayList<RawDocumentWord> rawWords = new ArrayList<RawDocumentWord>();
    for (int i = 0; i < words.length; i++) {
      if (!words[i].equals("") && !isNumber(words[i])) {
        if (body)
          rawWords.add(new RawDocumentWord(words[i], i+offset));
        else
          rawWords.add(new RawDocumentWord(words[i], -1));
      }
    }
    //  System.out.println("HERE 5");
    return rawWords;
  }
  
  
  //  takes a line of text and returns the words
  static String[] wordsFromLine(String line) {
    String[] words = line.split(" ");
    for (int i = 0; i < words.length; i++) {
      if (!words[i].equals("")) {
        words[i] = cleanPunctuation(words[i]);
      }
    }
    return words;
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
    char[] numbers = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.'};
    for (int i = 0; i < s.length(); i++) {
      Character c = new Character(s.charAt(i));
      boolean charIsNum = false;
      for (int n = 0; n < numbers.length; n++) {
        if (c == numbers[n])
          charIsNum = true;
      }
      if (!charIsNum)
        return false;
    }
    return true;
  }
  
  
  
  
  
  //  integrates the Proper document into the dictionary
  static void addDocument(ProperDocument doc, ArrayList<String> wordList) {
    //  scan all dictionary words in doc
    for (DictionaryWord docWord : doc.words) {
      //  check for matching word in dictionary
      if (wordList.contains(docWord)) {
        int position = wordList.indexOf(docWord);
        words.get(position).addPosting(docWord.postings.get(0));
      }
      //  if match could not be found, add (shallow copy of) word to dictionary
      else {
        DictionaryWord dw = new DictionaryWord(docWord.word);
        dw.addPosting(docWord.postings.get(0));  //  can do 0 as each document only has 1 posting (containing 1+ positions)
        words.add(dw);
        wordList.add(docWord.word);
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