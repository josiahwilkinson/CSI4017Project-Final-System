import java.util.ArrayList;
import java.util.Collections;
import java.io.*;
import java.io.FileNotFoundException;
import java.lang.Math;
import java.util.HashMap;



//  dictionary class
class WordDictionary {
  
  
  
  
  static String[] stemmingRules;
  static char[] punctuation;
  
  
  static String[] stopWords = {"a", "about", "above", "after", "again", "against", "ain", "all", "am", "an", "and", "any", "are", "aren", "aren't", "as", "at", "be", "because", "been",
    "before", "being", "below", "between", "both", "but", "by", "can", "couldn", "couldn't", "d", "did", "didn", "didn't", "do", "does", "doesn", "doesn't",
    "doing", "don", "don't", "down", "during", "each", "few", "for", "from", "further", "had", "hadn", "hadn't", "has", "hasn", "hasn't", "have", "haven",
    "haven't", "having", "he", "her", "here", "hers", "herself", "him", "himself", "his", "how", "i", "if", "in", "into", "is", "isn", "isn't", "it", "it's",
    "its", "itself", "just", "ll", "m", "ma", "me", "mightn", "mightn't", "more", "most", "mustn", "mustn't", "my", "myself", "needn", "needn't", "no", "nor",
    "not", "now", "o", "of", "off", "on", "once", "only", "or", "other", "our", "ours", "ourselves", "out", "over", "own", "re", "s", "same", "shan", "shan't",
    "she", "she's", "should", "should've", "shouldn", "shouldn't", "so", "some", "such", "t", "than", "that", "that'll", "the", "their", "theirs", "them",
    "themselves", "then", "there", "these", "they", "this", "those", "through", "to", "too", "under", "until", "up", "ve", "very", "was", "wasn", "wasn't",
    "we", "were", "weren", "weren't", "what", "when", "where", "which", "while", "who", "whom", "why", "will", "with", "won", "won't", "wouldn", "wouldn't",
    "y", "you", "you'd", "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves", "could", "he'd", "he'll", "he's", "here's", "how's", "i'd",
    "i'll", "i'm", "i've", "let's", "ought", "she'd", "she'll", "that's", "there's", "they'd", "they'll", "they're", "they've", "we'd", "we'll", "we're", "we've",
    "what's", "when's", "where's", "who's", "why's", "would"};
  
  
  //  static ArrayList<DictionaryWord> words = new ArrayList<DictionaryWord>();
  
  static HashMap<String, DictionaryWord> reutersDictionaryMap = new HashMap<String, DictionaryWord>();
  static HashMap<String, DictionaryWord> uottawaDictionaryMap = new HashMap<String, DictionaryWord>();
  
  static ArrayList<ProperDocument> reutersDocumentList = new ArrayList<ProperDocument>();
  static ArrayList<ProperDocument> uottawaDocumentList = new ArrayList<ProperDocument>();
  
  public static void main(String args[]) {
    createWordDictionary();
  }
  
  
  
  WordDictionary(char[] p, String[] sr) {
    stemmingRules = sr;
    punctuation = p;
  }
  
  
  public static void createWordDictionary() {
    createReutersWordDictionary();
    createUottawaWordDictionary();
  }
  
  public static void createReutersWordDictionary() {
    
    
    String originalLine = "";  //  copy to preserve capitals
    String line;  //  set to lowercase

    
    //  preprocessing
    try {
      
      for (int i = 0; i < 22; i++) {
        System.out.println("Parsing file: " + i);
        
        
        //  create arrayList of raw documents (word bags) to be made into a dictionary later
        ArrayList<RawDocument> rawDocuments = new ArrayList<RawDocument>();
        
        RawDocument currentDocument = null;
        
        
        File classes;
        if (i < 10)
          classes = new File("Reuters21578/reut2-00" + i + ".sgm");
        else
          classes = new File("Reuters21578/reut2-0" + i + ".sgm");
        
        BufferedReader br = new BufferedReader(new FileReader(classes));
        line = br.readLine();  //  get first line
        
        int lineCounter = 0;
        while (line != null) {
          
          //  casefold line
          originalLine = line;  //  copy to preserve capitals
          line = line.toLowerCase();
          lineCounter++;
          
          //  if (i == 2)
          //  System.out.println(i + " " + lineCounter + " " + rawDocuments.size());
          //  System.out.println(line);
          //  System.out.println(originalLine);
          
          //  check for new beginning
          if (originalLine.length() > 0) {
            
            //  trim * at beginning
            while(originalLine.length() > 0) {
              if (originalLine.charAt(0) == '*') {
                originalLine = originalLine.substring(1, originalLine.length()); 
                line = line.toLowerCase();
              }
              else
                break;
            }
            
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
                  currentDocument.addWords(wordsFromLine(line.substring(6, originalLine.length()-7)));
                }
              }
              
              //  title
              if (originalLine.length() > 6) {
                if (originalLine.substring(0, 6).equals("<TITLE")) {
                  
                  //  System.out.println("Adding title: " + originalLine);
                  
                  //  check for weird "&lt;CDIN>" like inturruption
                  for (int j = 0; j < originalLine.length()-10; j++) {  //  use a for loop as it can occur more than once
                    if (originalLine.charAt(j) == '&' && originalLine.charAt(j+3) == ';') {
                        originalLine = originalLine.substring(0, j) + originalLine.substring(j+4, originalLine.length());  //  end + 2 becuase there is a space on each side if the "&lt;CDIN>" statement
                        line = originalLine.toLowerCase();  //  overwrite line
                    }
                  }
                  
                  
                  currentDocument.addTitle(originalLine.substring(7, originalLine.length()-8));
                  currentDocument.addWords(wordsFromLine(line.substring(7, originalLine.length()-8)));
                  if (currentDocument.id == 440 || currentDocument.id == 455 || currentDocument.id == 459)
                    System.out.println("found title:" + " " + line.substring(7, originalLine.length()-8));
                  
                  //  System.out.println("Added title: " + originalLine);
                }
              }
              
              //  topics
              if (originalLine.length() > 6) {
                if (originalLine.substring(0, 6).equals("<TOPIC")) {
                  originalLine = trimD(originalLine);  //  remove <D> and </D>
                  line = originalLine.toLowerCase();  //  overwrite line
                  currentDocument.addTopics(originalLine.substring(8, originalLine.length()-9));
                  currentDocument.addWords(wordsFromLine(line.substring(8, originalLine.length()-9)));
                }
              }
              
              //  places
              if (originalLine.length() > 6) {
                if (originalLine.substring(0, 6).equals("<PLACE")) {
                  originalLine = trimD(originalLine);  //  remove <D> and </D>
                  line = originalLine.toLowerCase();  //  overwrite line
                  currentDocument.addPlaces(originalLine.substring(8, originalLine.length()-9));
                  currentDocument.addWords(wordsFromLine(line.substring(8, originalLine.length()-9)));
                }
              }
              
              //  people
              if (originalLine.length() > 6) {
                if (originalLine.substring(0, 6).equals("<PEOPL")) {
                  originalLine = trimD(originalLine);  //  remove <D> and </D>
                  line = originalLine.toLowerCase();  //  overwrite line
                  currentDocument.addPeople(originalLine.substring(8, originalLine.length()-9));
                  currentDocument.addWords(wordsFromLine(line.substring(8, originalLine.length()-9)));
                }
              }
              
              //  orgs
              if (originalLine.length() > 6) {
                if (originalLine.substring(0, 5).equals("<ORGS>")) {
                  originalLine = trimD(originalLine);  //  remove <D> and </D>
                  line = originalLine.toLowerCase();  //  overwrite line
                  currentDocument.addOrgs(originalLine.substring(6, originalLine.length()-7));
                  currentDocument.addWords(wordsFromLine(line.substring(6, originalLine.length()-7)));
                }
              }
              
              //  exchanges
              if (originalLine.length() > 6) {
                if (originalLine.substring(0, 6).equals("<EXCHA")) {
                  originalLine = trimD(originalLine);  //  remove <D> and </D>
                  line = originalLine.toLowerCase();  //  overwrite line
                  currentDocument.addExchanges(originalLine.substring(11, originalLine.length()-12));
                  currentDocument.addWords(wordsFromLine(line.substring(11, originalLine.length()-12)));
                }
              }
              
              //  companies
              if (originalLine.length() > 6) {
                if (originalLine.substring(0, 6).equals("<COMPA")) {
                  originalLine = trimD(originalLine);  //  remove <D> and </D>
                  line = originalLine.toLowerCase();  //  overwrite line
                  currentDocument.addCompanies(originalLine.substring(11, originalLine.length()-12));
                  currentDocument.addWords(wordsFromLine(line.substring(11, originalLine.length()-12)));
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
                  currentDocument.addWords(wordsFromLine(dateLine));
                  
                  
                  //  check for weird "&lt;CDIN>" like inturruption
                  for (int j = 0; j < originalLine.length()-10; j++) {  //  use a for loop as it can occur more than once
                    if (originalLine.charAt(j) == '&' && originalLine.charAt(j+3) == ';') {
                        originalLine = originalLine.substring(0, j) + originalLine.substring(j+4, originalLine.length());  //  end + 2 becuase there is a space on each side if the "&lt;CDIN>" statement
                        line = originalLine.toLowerCase();  //  overwrite line
                    }
                  }
                  
                  //  get text body
                  //  first line
                  String[] parts = originalLine.split("</DATELINE><BODY>");
                  currentDocument.addLine(parts[1]);
                  //  get next line
                  line = br.readLine();
                  originalLine = line;
                  line = line.toLowerCase();
                  lineCounter++;
                  
                  //  add words
                  while(!originalLine.equals("</REUTERS>")) {
                    if (!line.equals("") && !line.equals(" Reuter") && !line.equals("&#3;</BODY></TEXT>") && !line.equals("Reuter &#3;</BODY></TEXT>") && !line.equals("Reuter </BODY></TEXT>")) {  //  don't use the weird lines that are sometimes at the end
                      
                      
                      //  check for weird "&lt;CDIN>" like inturruption
                      for (int j = 0; j < originalLine.length()-10; j++) {  //  use a for loop as it can occur more than once
                        if (originalLine.charAt(j) == '&' && originalLine.charAt(j+3) == ';') {
                          originalLine = originalLine.substring(0, j) + originalLine.substring(j+4, originalLine.length());  //  end + 2 becuase there is a space on each side if the "&lt;CDIN>" statement
                          line = originalLine.toLowerCase();  //  overwrite line
                        }
                      }
                      
                      currentDocument.addLine(originalLine);
                      String[] words = wordsFromLine(line);
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
        
        
        
        System.out.println("checking for titles");
        for (int j = 0; j < rawDocuments.size(); j++) {
          //  System.out.println(j+": "+rawDocuments.get(j).title);
        }
        System.out.println(493+": "+rawDocuments.get(493).title);
        System.out.println(454+": "+rawDocuments.get(454).title);
        System.out.println(458+": "+rawDocuments.get(458).title);
        
        
        
        System.out.println("Finished creating raw documents");
        
        
        
        //  too slow, use hashmap
        //  ArrayList<String> wordList = new ArrayList<String>();
        
        //  documents = new ProperDocument[rawDocuments.size()];
        
        for (int j = 0; j < rawDocuments.size(); j++) {
          if (j%100 == 0)
            System.out.println(j + " " + reutersDocumentList.size() + " " + reutersDictionaryMap.size());
          //  documents[j] = new ProperDocument(rawDocuments.get(j));
          ProperDocument pd = new ProperDocument(rawDocuments.get(j));
          reutersDocumentList.add(pd);
          //  create dictionary compiling all proper documents
          addDocument(pd, reutersDictionaryMap);
        }
        System.out.println("Finished creating proper documents");
        
        
      }
      
      
      
      
      
      
      /*
      System.out.println("checking for titles");
      System.out.print("no titles: ");
      for (int j = 0; j < reutersDocumentList.size(); j++) {
        if (reutersDocumentList.get(j).title.equals("") || reutersDocumentList.get(j).title.equals(" ") || reutersDocumentList.get(j).title.equals("  "))
          System.out.print(reutersDocumentList.get(j).id + ", ");
      }
      */
      
      
      
      
      
      
      
      
      //  print out dictionary
      System.out.println("Finished constructing dictionary");
      System.out.println();
      //  printWordDictionary();
      
      
      //  set weights for all postings
      System.out.println("adding weights to dictionary");
      System.out.println();
      
      
      //  for (DictionaryWord word : new ArrayList<DictionaryWord>(reutersDictionaryMap.values())) {
      //  ArrayList<DictionaryWord> mapCopy = new ArrayList<DictionaryWord>(reutersDictionaryMap.values());
      ArrayList<String> mapKeyCopy = new ArrayList<String>(reutersDictionaryMap.keySet());
      for (int k = 0; k < mapKeyCopy.size(); k++) {
        DictionaryWord word = reutersDictionaryMap.get(mapKeyCopy.get(k));
        //  status update
        float total = 10;
        for (float p = 1; p < total+1; p++) {
          if ((int)((float)(p*mapKeyCopy.size())/total) == k+1)
            System.out.println((int)(p*total)+"%");
        }
        for (int j = 0; j < reutersDocumentList.size(); j++) {
          if (word.posting(j) != null) {  //  check that there is the posting for the document with this as the id
            setWeight(word, j, true);
          }
        }
      }
      
      
      System.out.println("finished adding weights to dictionary");
      System.out.println();
      
      
      //  remove stopwords
      System.out.println("removing stopwords from dictionary");
      System.out.println();
      for (String word : stopWords) {
        if (reutersDictionaryMap.containsKey(word))
          reutersDictionaryMap.remove(word);
      }
      System.out.println("finished removing stopwords");
      System.out.println();
      
      
      
      System.out.println("Did work");
    }
    
    
    
    catch (Exception e) {
      System.out.println("Did not work");
      System.out.println(e);
      
      System.out.println("current line: " + originalLine);
    }
  }
  
  
  
  
  public static void createUottawaWordDictionary() {
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
          ArrayList<String> wordPostings = new ArrayList<String>();
          //  cycle through words
          for (int i = 0; i < words.length; i++) {
            if (words[i].length() > 0) {
              //  System.out.println(words[i]);
              words[i] = cleanPunctuation(words[i]);  //  remove punctuation from a word
              //  System.out.println(words[i]);
              
              //  now that all punctuation has bben trimmed, save to wordPostings to preserve posting (as positions will be messed up during stemming)
              wordPostings.add(words[i]);
              
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
                    wordPostings.add(words[i]);  //  add course code to posting
                  }
                }
              }
              
              //  else, stem
              if (!courseCode) {
                
                //  stemming
                for (int j = 0; j < stemmingRules.length; j += 2) {
                  if (words[i].length() > stemmingRules[j].length()) {  //  check if within length
                    if (words[i].substring(words[i].length()-stemmingRules[j].length(), words[i].length()).equals(stemmingRules[j]))  //  check for matching ending
                      wordPostings.add(words[i].substring(0, words[i].length()-stemmingRules[j].length())+stemmingRules[j+1]);  //  add new wordPosting with same posting position
                  }
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
      for (int i = 0; i < rawDocuments.size(); i++) {
        uottawaDocumentList.add(new ProperDocument(rawDocuments.get(i)));
        //  create dictionary compiling all proper documents
        addDocument(uottawaDocumentList.get(uottawaDocumentList.size()-1), uottawaDictionaryMap);
      }
      
      //  print out document 1 (0)
      //  documents[0].printDocument();
      
      System.out.println();
      System.out.println();
      
      //  print out dictionary
      System.out.println("Finished constructing dictionary");
      System.out.println();
      //  dictionary.printWordDictionary();
      
      
      
      
      
      //  for (DictionaryWord word : new ArrayList<DictionaryWord>(reutersDictionaryMap.values())) {
      //  ArrayList<DictionaryWord> mapCopy = new ArrayList<DictionaryWord>(reutersDictionaryMap.values());
      ArrayList<String> mapKeyCopy = new ArrayList<String>(uottawaDictionaryMap.keySet());
      for (int k = 0; k < mapKeyCopy.size(); k++) {
        DictionaryWord word = uottawaDictionaryMap.get(mapKeyCopy.get(k));
        //  status update
        float total = 10;
        for (float p = 1; p < total+1; p++) {
          if ((int)((float)(p*mapKeyCopy.size())/total) == k+1)
            System.out.println((int)(p*total)+"%");
        }
        for (int j = 0; j < uottawaDocumentList.size(); j++) {
          if (word.posting(j) != null) {  //  check that there is the posting for the document with this as the id
            setWeight(word, j, false);
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
  }
  
  
  /*
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
   */
  
  //  takes a line of text and returns the words
  static String[] wordsFromLine(String line) {
    String[] words = line.split(" ");
    ArrayList<String> wardVariations = new ArrayList<String>();
    for (int i = 0; i < words.length; i++) {
      if (!words[i].equals("")) {
        //  clean word
        words[i] = cleanPunctuation(words[i]);
        //  stemming
        for (int j = 0; j < stemmingRules.length; j += 2) {
          if (words[i].length() > stemmingRules[j].length()) {  //  check if within length
            if (words[i].substring(words[i].length()-stemmingRules[j].length(), words[i].length()).equals(stemmingRules[j]))  //  check for matching ending
              wardVariations.add(words[i].substring(0, words[i].length()-stemmingRules[j].length())+stemmingRules[j+1]);  //  add new wordPosting with same posting position
          }
        }
      }
    }
    
    //  combine lists
    String[] newWords = new String[words.length+wardVariations.size()];
    for (int i = 0; i < words.length; i++)
      newWords[i] = words[i];
    for (int i = words.length; i < wardVariations.size(); i++)
      newWords[i] = wardVariations.get(i-words.length);
    
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
  
  
  //  removes <D> and </D> from the input string and returns it
  static String trimD(String line) {
    String[] parts = line.split("<D>");
    line = parts[0];
    for (int i = 1; i < parts.length; i++)
      line += " " + parts[i];
    
    parts = line.split("</D>");
    line = parts[0];
    for (int i = 1; i < parts.length; i++)
      line += " " + parts[i];
    
    return line;
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
  static void addDocument(ProperDocument doc, HashMap<String, DictionaryWord> dictionaryMap) {
    //  scan all dictionary words in doc
    //  for (DictionaryWord docWord : doc.words) {
    for (int i = 0; i < doc.words.size(); i++) {
      //  too slow, use hashmap
      /*
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
       */
      
      //  check for matching word in dictionary
      if (dictionaryMap.containsKey(doc.words.get(i).word)) {
        dictionaryMap.get(doc.words.get(i).word).addPosting(doc.words.get(i).postings.get(0));
        doc.words.add(i, dictionaryMap.get(doc.words.get(i).word));  //  add DictionaryWord object as reference so as to free up memory
        doc.words.remove((i+1));  //  remove old DictionaryWord object so as to free up memory
      }
      //  otherwise, add it
      else {
        DictionaryWord dw = new DictionaryWord(doc.words.get(i).word);
        dictionaryMap.put(doc.words.get(i).word, dw);
        dw.addPosting(doc.words.get(i).postings.get(0));
        doc.words.add(i, dw);  //  change DictionaryWord object into reference so as to free up memory
        doc.words.remove((i+1));  //  remove old DictionaryWord object so as to free up memory
        //  words.add(dw);
      }
    }
  }
  
  //  returns the DictionaryWord matching the input word
  DictionaryWord getWord(String w, HashMap<String, DictionaryWord> dictionaryMap) {
    if (dictionaryMap.containsKey(w))
      return dictionaryMap.get(w);
    return null;
  }
  
  //  prints the dictionary to the console
  static void printWordDictionary() {
    System.out.println("WordDictionary:");
    for (DictionaryWord word : new ArrayList<DictionaryWord>(reutersDictionaryMap.values())) {
      System.out.println();
      System.out.print(word.word + " || ");
      for (Posting posting : word.postings) {
        System.out.print(posting.docID + ": ");
        System.out.print(posting.postings);
        //  System.out.print(posting.postings[0]);
        //  for (int i = 1; i<posting.postings.length; i++) {
        //    System.out.print(", " + posting.postings[i]);
        //  }
        System.out.print(" | ");
      }
    }
  }
  
  
  
  
  
  
  
  
  static float inverseDocumentFrequency(DictionaryWord word, boolean reuters) {
    //  System.out.println(uottawaDictionaryMap.size()+" "+Math.log(uottawaDictionaryMap.size())+" "+word.totalDocuments());
    if(reuters)
      return (float)(Math.log(reutersDictionaryMap.size()/word.totalDocuments()));
    return (float)(Math.log(uottawaDictionaryMap.size()/word.totalDocuments()));
  }
  
  static float termFrequency(DictionaryWord word, int docID) {
    return (float)(Math.log(1 + word.termFrequency(docID)));
  }
  
  static void setWeight(DictionaryWord word, int docID, boolean reuters) {
    float weight = inverseDocumentFrequency(word, reuters) * termFrequency(word, docID);
    //  if(inverseDocumentFrequency(word, reuters) != 0 || termFrequency(word, docID) != 0)
      //  System.out.println(inverseDocumentFrequency(word, reuters) + " " + termFrequency(word, docID));
    word.setWeight(weight, docID);
  }
  
  //  get weights for input document
  static float weight(DictionaryWord word, int docID) {
    return word.weight(docID);
  }
  
  
}

//  DictionaryWord stored in main VanillaSystem 