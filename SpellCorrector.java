import java.util.ArrayList;
import java.util.Collections;
import java.io.*;
import java.io.FileNotFoundException; 
import java.lang.Math; 
import java.util.HashMap;


public class SpellCorrector {
  private WordDictionary dict;
  //  boolean reuter;
  
  
  
  
  
  
  public SpellCorrector(WordDictionary d) {
    this.dict=d;
   
  }
  
  
  
  public ArrayList<String> getSuggestions(String word, boolean reuters){
    //set min editdistance to compare 
   
    
    ArrayList<String> suggestions=new ArrayList<String>();
    
    ArrayList<String> dictionary;
    if (reuters)
      dictionary = new ArrayList<String>(dict.reutersDictionaryMap.keySet());
    else
      dictionary = new ArrayList<String>(dict.uottawaDictionaryMap.keySet());
    
    
    
    int initial = editDistDP(word, dictionary.get(0), word.length(), dictionary.get(0).length());;
    for (int i = 0; i < dictionary.size(); i++) {
      String w = dictionary.get(i);
        
      if (editDistDP(word, w,word.length(), w.length()) < initial) {
        suggestions.add(w);
        initial = editDistDP(word, w,word.length(), w.length());
      }
      else if(editDistDP(word, w, word.length(), w.length()) == initial) {
        suggestions.add(w);
      }
    }
    
    System.out.println("suggestions: " + suggestions.toString());
    return suggestions;
  }
  
  
  
  
  
  public boolean inWordDictionary(String word, boolean reuters){
    
    if (reuters) {
      if (dict.reutersDictionaryMap.containsKey(word))
        return true;
    }
    
    else { if (dict.uottawaDictionaryMap.containsKey(word))
      return true;
    }
    
    return false;
    
    
  
  }
  static int min(int x, int y, int z) 
  { 
    return Math.min(Math.min(x, y), z);
  } 
  
  
  
  //Taken from https://www.geeksforgeeks.org/edit-distance-dp-5/
  static int editDistDP(String str1, String str2, int m, int n) 
  { 
    // Create a table to store results of subproblems 
    int dp[][] = new int[m + 1][n + 1]; 
    
    // Fill d[][] in bottom up manner 
    for (int i = 0; i <= m; i++) { 
      for (int j = 0; j <= n; j++) { 
        // If first string is empty, only option is to 
        // insert all characters of second string 
        if (i == 0) 
          dp[i][j] = j; // Min. operations = j 
        
        // If second string is empty, only option is to 
        // remove all characters of second string 
        else if (j == 0) 
          dp[i][j] = i; // Min. operations = i 
        
        // If last characters are same, ignore last char 
        // and recur for remaining string 
        else if (str1.charAt(i - 1) == str2.charAt(j - 1)) 
          dp[i][j] = dp[i - 1][j - 1]; 
        
        // If the last character is different, consider all 
        // possibilities and find the minimum 
        else
          dp[i][j] = 1 + min(dp[i][j - 1], // Insert 
                             dp[i - 1][j], // Remove 
                             dp[i - 1][j - 1]); // Replace 
      } 
    } 
    
    return dp[m][n]; 
  } 
  
  
  
  
  
}
