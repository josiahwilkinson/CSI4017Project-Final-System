import java.util.ArrayList;

public class SpellCorrector {
  private Dictionary dict;
  ArrayList<String> suggestions = new ArrayList<String>();
  
  
  
  
  
  
  public SpellCorrector(Dictionary d) {
    this.dict=d;
    
  }
  
  
  
  public ArrayList<String> getSuggestions(String word){
    //set min editdistance to compare 
    
    int initial=editDistDP(word,dict.words.get(0).word,word.length(),dict.words.get(0).word.length());
    
    //runs through dictionary to get suggestions
    for(DictionaryWord w: dict.words) {
      if(editDistDP(word,w.word,word.length(),w.word.length())<initial) {
        suggestions=new ArrayList<String>();
        suggestions.add(w.word);
        initial=editDistDP(word,w.word,word.length(),w.word.length());
        
        
        
        
      }else if(editDistDP(word,w.word,word.length(),w.word.length())==initial) {
        suggestions.add(w.word);
        
        
        
      }
      
      
      
    }
    System.out.println(suggestions.toString());
    return suggestions; 
  }
  
  
  
  
  
  public boolean inDictionary(String word){
    for(DictionaryWord w: dict.words) {
      if(w.word.equals(word)){
         return true;
    
  }
     
    }
    return false;
  
  }
  static int min(int x, int y, int z) 
  { 
    if (x <= y && x <= z) 
      return x; 
    if (y <= x && y <= z) 
      return y; 
    else
      return z; 
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
