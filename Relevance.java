import java.io.*; 
import java.util.*; 

public class Relevance{
	String words;
	ArrayList<Integer>relevant;
	ArrayList<Integer>nonrelevant;




  public Relevance(String word, ArrayList<Integer>relevants,ArrayList<Integer>nonrelevants) {
    this.words=word;
    this.relevant=relevants;
    this.nonrelevant=nonrelevants;
   
  }



}