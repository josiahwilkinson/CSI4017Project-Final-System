import java.util.ArrayList;

public class SpellCorrector {
  private Dictionary dict;
  boolean reuter;
  ArrayList<String> suggestions = new ArrayList<String>();
  
  
  
  
  
  
  public SpellCorrector(Dictionary d,boolean reuters) {
    this.dict=d;
    this.reuter=reuters;
  }
  
  
  
  public ArrayList<String> getSuggestions(String word){
    //set min editdistance to compare 
    
    if(reuter){
      int initial=editDistDP(word,dict.reutersDictionaryMap.get(0).word,word.length(),dict.reutersDictionaryMap.get(0).word.length());
       for(DictionaryWord w: new ArrayList<DictionaryWord>(dict.reutersDictionaryMap.values())) {
      if(editDistDP(word,w.word,word.length(),w.word.length())<initial) {
        suggestions=new ArrayList<String>();
        suggestions.add(w.word);
        initial=editDistDP(word,w.word,word.length(),w.word.length());
        
        
        
        
      }else if(editDistDP(word,w.word,word.length(),w.word.length())==initial) {
        suggestions.add(w.word);
        
        
        
      }
      
      
      
    }
    }else{
      int initial=editDistDP(word,dict.uottawaDictionaryMap.get(0).word,word.length(),dict.uottawaDictionaryMap.get(0).word.length());
    
    //runs through dictionary to get suggestions
    for(DictionaryWord w: new ArrayList<DictionaryWord>(dict.uottawaDictionaryMap.values())){
      if(editDistDP(word,w.word,word.length(),w.word.length())<initial) {
        suggestions=new ArrayList<String>();
        suggestions.add(w.word);
        initial=editDistDP(word,w.word,word.length(),w.word.length());
        
        
        
        
      }else if(editDistDP(word,w.word,word.length(),w.word.length())==initial) {
        suggestions.add(w.word);
        
        
        
      }
      
      
      
    }}
    System.out.println(suggestions.toString());
    return suggestions; 
    
    
  }
  
  
  
  
  
  public boolean inDictionary(String word){
    if(this.reuter){
    for(DictionaryWord w: new ArrayList<DictionaryWord>(dict.reutersDictionaryMap.values())) {
      if(w.word.equals(word)){
        return true;
        
      }
      
    }
    return false;
    
  }else{
    for(DictionaryWord w: new ArrayList<DictionaryWord>(dict.uottawaDictionaryMap.values())) {
          if(w.word.equals(word)){
            return true;
            
          }
      
    }
    return false;


  }}
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
