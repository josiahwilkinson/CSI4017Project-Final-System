import java.util.ArrayList;
import java.io.*;

//  for query processing
class BooleanQueryProcessing {
  
  
  static Dictionary dictionary;
  
  
  BooleanQueryProcessing(Dictionary d) {
    dictionary = d;
  }
  
  
  
  //  used just for testing
  public static void main(String args[]) {
    /*
     processQuery("test AND (this OR that)");
     processQuery("  test     AND NOT (this   OR that )  ");
     processQuery("NOT (Thing OR Something) AND Here");
     processQuery("(Thing OR Something OR Other) AND (This OR That) AND Others");
     processQuery("(Thing OR Something OR Other) AND (This OR (That AND (True OR False)))");
     */
    printQueries(processQuery("A AND NOT (B AND NOT (C OR D))"));
    processQuery("A AND NOT (B AND NOT (C AND D))");
    processQuery("(Thing OR Something OR Other) AND (This OR (That AND (True OR False)))");
    processQuery("NOT (C AND D)");
    processQuery("A OR NOT (C AND D)");
    processQuery("effectively AND NOT baccalaureate");
    processQuery("(effectively AND NOT baccalaureate)");
    processQuery("(effectively AND NOT (baccalaureate))");
    processQuery("(NOT(effectively AND NOT baccalaureate))");
    System.out.println();
    System.out.println();
    System.out.println();
    printQueries(processQuery("NOT(effectively AND NOT baccalaureate)"));
    System.out.println();
    System.out.println();
    System.out.println();
    printQueries(processQuery("NOT(effectively OR NOT baccalaureate)"));
    System.out.println();
    System.out.println();
    System.out.println();
    printQueries(processQuery("NOT(test AND NOT(effectively OR NOT baccalaureate))"));
    System.out.println();
    System.out.println();
    System.out.println();
    printQueries(processQuery("test OR (effectively OR baccalaureate)"));
    System.out.println();
    System.out.println();
    printQueries(processQuery("(query AND processing)"));
  }
  
  //  returns a list of lists of strings, each list being the a list of Strings for the AND queries
  static ArrayList<ArrayList<String>> processQuery(String query) {
    
    System.out.println(query);
    
    //  remove _
    String[] underscore = query.split("_");
    query = underscore[0];
    for (int i = 1; i < underscore.length; i++)
      query += " " + underscore[i];
    
    
    //  checks that all NOTs are properly bracketed
    query = enforceNotBrackets(query);
    
    //  build query tree
    QueryNode head = new QueryNode(query);
    
    System.out.println(query);
    
    System.out.println();
    head.printQuery();
    
    //  wierdly enough, the NOT is not pushed if it is the head
    //  so do that manually
    if (head.parts.get(0).equals("NOT")) {
      head.children.get(0).not = true;
      head.parts.remove(0);
      head.printQuery();
    }
    
    
    //  extend nots
    extendNot(head);
    //  set not
    setNot(head);
    
    
    /*
     //  compress tree
     //  do head first
     while (head.type == -1 && head.children.size() == 1) {
     head = head.children.get(0);
     }
     */
    
    
    
    
    
    
    
    
    
    
    
    //  Check all words here for a * and replace them with an OR with all possibilities as under them
    wildcard(head);
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    ArrayList<ArrayList<String>> queries = new ArrayList<ArrayList<String>>();
    //  get queries
    queries = getQueries(head);
    /*
     System.out.println();
     printQueries(queries);
     System.out.println();
     System.out.println();
     System.out.println();
     System.out.println();
     */
    
    //  print out results
    printQueries(queries);
    
    return queries;
  }
  
  static ArrayList<ArrayList<String>> getQueries(QueryNode node) {
    ArrayList<ArrayList<String>> queries = new ArrayList<ArrayList<String>>();
    //  OR
    //  for the OR, add a new ArrayList<String> for each child
    if (node.type == 0) {
      for (QueryNode child : node.children) {
        ArrayList<ArrayList<String>> childQueries = getQueries(child);
        for (ArrayList<String> query : childQueries)
          queries.add(query);
      }
    }
    //  AND
    //  for the AND, cross the children together
    if (node.type == 1) {
      //  start with the base of 2 children and then add from there
      //  first 2 children
      queries = crossQueries(getQueries(node.children.get(0)), getQueries(node.children.get(1)));
      //  next children
      for (int i = 2; i < node.children.size(); i++)
        queries = crossQueries(queries, getQueries(node.children.get(i)));
    }
    //  word
    if (node.type == -1) {
      if (node.children.size() == 0) {
        queries.add(new ArrayList<String>());
        queries.get(queries.size()-1).add(node.parts.get(0));
      }
      else
        queries = getQueries(node.children.get(0));
    }
    
    //  print out results
    printQueries(queries);
    
    return queries;
  }
  
  
  //  checks (or makes sure) that all NOTs have proper bracket boundings (as they may not work propely otherwise)
  static String enforceNotBrackets(String query) {
    //  parse query for NOT
    for (int i = 0; i < query.length()-3; i++) {
      if (query.substring(i, i+3).equals("NOT")) {
        boolean addBracket = false;
        //  move i
        i = i+3;
        //  skip spaces
        while(query.charAt(i) == ' ')
          i++;
        //  check opening bracket
        if (query.charAt(i) != '(') {
          addBracket = true;
        }
        //  if brackets must be added
        if (addBracket) {
          int j = i;
          //  add opening bracket
          query = query.substring(0, i) + "(" + query.substring(i, query.length());
          //  skip spaces
          j++;
          while(query.charAt(j) == ' ') {
            j++;
            if (j == query.length())
              break;
          }
          //  find next space or closing bracket
          //  if (j < query.length()) {
          while (query.charAt(j) != ')' && query.charAt(j) != ' ') {
            j++;  //  using j so as to preserve i so that NOTs are not missed
            if (j == query.length())
              break;
          }
          //  }
          //  at next space or closing bracket
          //  add closing bracket
          query = query.substring(0, j) + ")" + query.substring(j, query.length());
        }
      }
    }
    return query;
  }
  
  
  
  //  extends NOTs in the input tree
  static void extendNot(QueryNode node) {
    //  call extendNot on children
    for (QueryNode child : node.children) {
      extendNot(child);
    }
    //  check if NOT and extend if so
    if (node.not) {
      //  if (node.type != -1 || (node.type == -1 && node.children.size() > 0)) {  //  check not the word itself
      //  swap type
      if (node.type != -1)
        node.type = 1-node.type;
      //  remove not
      if (node.children.size() > 0)
        node.not = !node.not;
      //  set all children to not and extend not
      for (QueryNode child : node.children) {
        child.not = !child.not;
        extendNot(child);
      }
      //    }
    }
  }
  
  //  places a ! in front of all applicable words for not
  static void setNot(QueryNode node) {
    //  if it is the word, place a ! infront of it, then check for !! and remove if so
    if (node.type == -1 && node.children.size() == 0) {
      if (node.not) {
        ArrayList<String> newWord = new ArrayList<String>();
        newWord.add("!" + node.parts.get(0));
        node.parts = newWord;
        //  it's throwing wierd errors when I try to overwrite it directly
      }
    }
    //  otherwise, call on children
    else {
      for (QueryNode child : node.children) {
        setNot(child);
      }
    }
  }
  
  
  //  crosses the 2D arrayLists of the 2 input nodes
  //  {{A}, {B}}, {{C}, {D}} -> {{A, C}, {A, D}, {B, C}, {B, D}}
  static ArrayList<ArrayList<String>> crossQueries(ArrayList<ArrayList<String>> nodeA, ArrayList<ArrayList<String>> nodeB) {  //  2 children
    ArrayList<ArrayList<String>> queries = new ArrayList<ArrayList<String>>();
    //  for each in first, add it to each in second
    //  go through each of childA x childB
    for (int i = 0; i < nodeA.size(); i++) {
      for (int j = 0; j < nodeB.size(); j++) {
        //  create list containing each word for the select list of childA and childB
        ArrayList<String> query = new ArrayList<String>();
        for (String word : nodeA.get(i))
          query.add(word);
        for (String word : nodeB.get(j))
          query.add(word);
        //  add new query to queries
        queries.add(query);
      }
    }
    return queries;
  }
  
  
  //  print queries
  static void printQueries(ArrayList<ArrayList<String>> queries) {
    System.out.println();
    System.out.print("{");
    for (int i = 0; i < queries.size(); i++) {
      System.out.print("{");
      for (int j = 0; j < queries.get(i).size(); j++) {
        System.out.print(queries.get(i).get(j));
        if (j < queries.get(i).size()-1)
          System.out.print(", ");
      }
      System.out.print("}");
      if (i < queries.size()-1)
        System.out.print(", ");
    }
    System.out.print("}");
  }
  
  
  
  
  //  wildcard management
  static void wildcard(QueryNode node) {
    //  AND or OR node
    //  if (node.type != -1) {
      //  call function on children
      for (QueryNode child : node.children) {
        wildcard(child);
      }
    //  }
    //  otherwise, operate on word
    if (node.type == -1) {
      
      String word = node.parts.get(0);
      
      //  trim !
      boolean not = false;
      if (node.parts.get(0).charAt(0) == '!') {
        not = true;
        node.parts.add(word.substring(1, word.length()));
        node.parts.remove(0);
      }
      
      int place = 2;  //  place of * (-1 = beginning, 0 = middle, 1 = end, 2 = nowhere)
        int spot = 1;  //  spot for middle
      //  check for * at beginning
      if (word.charAt(0) == '*') {
        place = -1;
      }
      //  check for * at end
      else if (word.charAt(word.length()-1) == '*') {
        place = 1;
      }
      //  parse for * in middle
      else {
        for (spot = 1; spot < word.length()-1; spot++) {
          if (word.charAt(spot) == '*') {
            place = 0;
            break;
          }
        }
        
      }
      
      //  if no *
      if (place == 2) {
        //  add ! back on
        if (not) {
          node.parts.add("!" + node.parts.get(0));
          node.parts.remove(0);
        }
      }
      else {
//  switch to OR node
        node.type = 0;
        //  remove word
        node.parts.remove(0);
        //  * at beginning
        if (place == -1) {
          //  set word part
          word = word.substring(1, word.length());
//  search dictionary for ending and add as children
          for (DictionaryWord w : dictionary.words) {
            String dword = w.word;
            if (dword.length() > word.length()) {  //  check length
              if (dword.substring(dword.length()-word.length(), dword.length()).equals(word)) {
                if (not)
                  node.children.add(new QueryNode("!"+dword, not));
                else
                  node.children.add(new QueryNode(dword, not));
              }
            }
          }
        }
        //  * at end
        else if (place == 1) {
          //  set word part
          word = word.substring(0, word.length()-1);
//  search dictionary for ending and add as children
          for (DictionaryWord w : dictionary.words) {
            String dword = w.word;
            if (dword.length() > word.length()) {  //  check length
              if (dword.substring(0, word.length()).equals(word)) {
                if (not)
                  node.children.add(new QueryNode("!"+dword, not));
                else
                  node.children.add(new QueryNode(dword, not));
              }
            }
          }
        }
        //  * in middle (spot referenced again)
        else if (place == 0) {
          //  set word part
          String wordA = word.substring(0, spot);
          String wordB = word.substring(spot+1, word.length());
//  search dictionary for ending and add as children
          for (DictionaryWord w : dictionary.words) {
            String dword = w.word;
            if (dword.length() > wordA.length()+wordB.length()) {  //  check length
              if (dword.substring(0, wordA.length()).equals(wordA) && dword.substring(dword.length()-wordB.length(), dword.length()).equals(wordB)) {
                if (not)
                  node.children.add(new QueryNode("!"+dword, not));
                else
                  node.children.add(new QueryNode(dword, not));
              }
            }
          }
        }
      }
    }
  }
  
  
}

class QueryNode {
  
  ArrayList<QueryNode> children = new ArrayList<QueryNode>();
  
  ArrayList<String> parts = new ArrayList<String>();
  
  boolean not = false;
  
  int type = -1;  //  -1 = word, 0 = OR, 1 = AND
  
  QueryNode(String query) {
    //  System.out.println(query);
    //  parse on brackets
    int openingBracket = -1;
    int closeBracket;
    int bracketCounter = 0;
    //  split brackets into children
    for (int i = 0; i < query.length(); i++) {
      if (query.charAt(i) == '(') {  //  find opening bracket
        if (openingBracket == -1)  //  if first, set at opening bracket
          openingBracket = i;
        else  //  otherwise, increment bracket counter
          bracketCounter++;
      }
      if (query.charAt(i) == ')') {  //  find closing bracket
        if (bracketCounter == 0) {  //  if matches opening bracket
          //  create child
          children.add(new QueryNode(query.substring(openingBracket+1, i)));
          //  reset query to replace child with %
          //  check if there is more to the query
          if (i < query.length()-1)
            query = query.substring(0, openingBracket) + " % " + query.substring(i+1, query.length());  //  need spaces or choas may ensue
          else
            query = query.substring(0, openingBracket) + " % ";  //  need spaces or choas may ensue
          //  System.out.println(query);
          //  reset trackers
          openingBracket = -1;
          i = 0;
        }
        else  //  otherwise, decrement bracket counter
          bracketCounter--;
      }
    }
    
    //  set type (now that only the query contains the single line of ORs or ANDs
    for (int i = 0; i < query.length()-3; i++) {
      if (query.substring(i, i+2).equals("OR")) {
        if (type == 1)  //  was previously AND
          error("Query type issue: OR");
        else
          type = 0;
      }
      if (query.substring(i, i+3).equals("AND")) {
        if (type == 0)  //  was previously AND
          error("Query type issue: AND");
        else
          type = 1;
      }
    }
    
    //  identify any single words by cutting at spaces
    String[] partsArray = query.split(" ");
    //  put in parts arrayList
    for (String s : partsArray)
      if (!s.equals(""))
      parts.add(s);
    //  only process further if length is > 1, as a length of 1 is just the pass through and would cause infinite recursion
    if (parts.size() > 1) {
      //  parse list for not AND, OR, NOT, or % and insert them into the children
      int childCounter = 0;
      for (int i = 0; i < parts.size(); i++) {
        String s = parts.get(i);
        if (s.equals("%"))
          childCounter++;
        if (!s.equals("AND") && !s.equals("OR") && !s.equals("NOT") && !s.equals("%")) {
          children.add(childCounter, new QueryNode(s));  //  add (insert) new child
          //  replace with symbol
          parts.remove(i);
          parts.add(i, "%");
        }
      }
    }
    //  parse list for NOT and add it to the child and remove it from the list
    int childCounter = 0;
    for (int i = 0; i < parts.size(); i++) {
      if (parts.get(i).equals("%")) {
        childCounter++;
      }
      if (parts.get(i).equals("NOT")) {
        children.get(childCounter).not = true;
        parts.remove(i);
      }
    }
    
    //  set to lowercase if just the word (casefold)
    if (type == -1) {
        parts.add(parts.get(0).toLowerCase());  //  add lower case word
        parts.remove(0);  //  remove old word
    }
  }
  
  
  //  used for adding in words for wildcard management
  QueryNode(String word, boolean n) {
    parts.add(word);
    not = n;
  }
  
//  prints out the query node structure
  void printQuery() {
    if (not)
      //  System.out.print("NOT ");
      System.out.print("!");
    if (type == 0)
      System.out.print("OR: {");
    else if (type == 1)
      System.out.print("AND: {");
    else  //  type = -1; just a word
      System.out.print("{");
    
    int childCounter = 0;
    for (int i = 0; i < parts.size(); i++) {
      String s = parts.get(i);
      System.out.println(s);
      if (s.equals("NOT")) {
        //    System.out.print("NOT ");
        //  System.out.print("!");
      }
      else if (s.equals("%")) {
        //  System.out.print("(");
        children.get(childCounter).printQuery();
        childCounter++;
        //  System.out.print(")");
        if (i < parts.size()-1)
          System.out.print(", ");
      }
      else if (!s.equals("AND") && !s.equals("OR")) {
        System.out.print(s);
        if (i < parts.size()-1)
          System.out.print(", ");
      }
    }
    
    System.out.print("}");
  }
  
  void error(String e) {
    System.out.println(e);
    System.exit(0);
  }
}



