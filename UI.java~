			  import javax.swing.*;
			  import javax.swing.event.ListSelectionEvent;
			  import javax.swing.event.ListSelectionListener;
			  import javax.swing.table.DefaultTableModel;
			  import javax.swing.table.TableModel;
			
			  import java.awt.*;
			  import java.util.ArrayList;
			
			  public class UI extends JFrame {
				
			    private JTextField search = new JTextField(30);
			    private JButton VSMButton = new JButton("VSM Search");
			    private JButton BooleanButton = new JButton("Boolean Search");
			    private JTable result = new JTable();
			    private JPanel panel = new JPanel();
			    private JScrollPane scrollPane = new JScrollPane(result);
			    private VanillaSystem Vanilla = new VanillaSystem();
			    private SpellCorrector correct= new SpellCorrector(Vanilla.dictionary);
			    int index[];
			    String listnew[];
			    int pick;
			    
			     

			    
			    public static void main(String[] args) {
			      new UI("UOttawa Course Finder");
			    }
			    
			    
			    
			    private DefaultTableModel makeModel(String info,int type) {
			    	
			      DefaultTableModel model = new DefaultTableModel();
			      model.addColumn("Course Codes");
			      try{
			      if(type==1){
			        
			        ArrayList<ArrayList<String>>list=Vanilla.booleanQueryProcessing.processQuery(info);
			      	listnew=Vanilla.condense(list);
			        for(String word:listnew){
			        	
			         String old;
			          if(word.charAt(0)=='!'){
			            word=word.substring(1,word.length());
			          }
			          	old=word;
			          if(!correct.inDictionary(word)){
			            //if not in dictionary popup with suggestions to replace. once clicked, replace word with suggestion. 
			        	 word=makeDialog(word,correct.getSuggestions(word));		           	
			        	 
			        	 info=info.replace(old,word);
			           			
			           
			          }
			          
			           
				}
			       	
			         search.setText(info);	
			         
			         index=VanillaSystem.booleanSearchWithQuery(info);
			        
			   for(int i:index) {
			            
			         model.addRow(new Object [] {VanillaSystem.documents[i].title});
			            
			   }
			     
			        
			      }else if(type==0){
			        String[] list=Vanilla.vectorQueryProcessing.processQuery(info);
			      	
			        for(String word:list){
			        	
			         String old;
			          if(word.charAt(0)=='!'){
			            word=word.substring(1,word.length());
			          }
			          	old=word;
			          if(!correct.inDictionary(word)){
			            //if not in dictionary popup with suggestions to replace. once clicked, replace word with suggestion. 
			        	 word=makeDialog(word,correct.getSuggestions(word));		           	
			        	 
			        	 info=info.replace(old,word);
			           			
			           
			          }
			          
			           
			  }
			         search.setText(info);	
			         index=VanillaSystem.vectorSearchWithQuery(info);
			        
			  	 for(int i:index) {
			         model.addRow(new Object [] {VanillaSystem.documents[i].title});
			            
			   }
			     

			    
			       }}catch(Exception e) {

			       	JOptionPane.showMessageDialog(null,"No results");

			       }
			          return model;
			       
			      
			      
			      
			  }  



			 
			    public String makeDialog(String outerword,ArrayList<String> choices) {
			    	
			    	DefaultTableModel model=new DefaultTableModel();
			    	JTable table=new JTable();
			    	
			    	model.addColumn("Possible Suggestions");
			    	
		        	
		        	
		        	
		        	 for(String correction: choices) {
		            	 model.addRow(new Object [] {correction});
		            	 
		            	
		            }

		        	table.setModel(model);
		        	table.setDefaultEditor(Object.class, null);
		        	table.getTableHeader().setReorderingAllowed(false);
		        	JScrollPane scroller = new JScrollPane(table);
		        
		        	JOptionPane.showMessageDialog(null,scroller,"What did you mean instead of "+outerword+"?",JOptionPane.PLAIN_MESSAGE);
		        	
		        	
		        	table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
				        public void valueChanged(ListSelectionEvent event) {
				       		

				        
				      
				        }});
		        	return choices.get(table.getSelectedRow());
		        	
			    	
		        	
			    }
			      
			    
			
			    
			    private String printdescription(String []lines ) {
			      String line="";
			      
			      for(String i:lines) {
			        line+="\n"+i;
			      }
			      
			      
			      return line;
			    }
			    
			    
			    private UI(String title) throws HeadlessException {
			      super(title);
			      Vanilla.createDictionary();
			      
			      setSize(650, 600);
			      setResizable(false);
			      scrollPane.setPreferredSize(new Dimension (600,500)); 
			      addComponents();
			      Table();
			      
			      //used from https://stackoverflow.com/questions/10128064/jtable-selected-row-click-event
			      result.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			        public void valueChanged(ListSelectionEvent event) {
			          if(result.getSelectedRow()>=0){
			          
			          //used from https://www.tutorialspoint.com/how-can-we-implement-a-long-text-of-the-joptionpane-message-dialog-in-java
			          JTextArea jta = new JTextArea(20, 50);
			          jta.setText(Vanilla.documents[index[result.getSelectedRow()]].title+"\n\n"+
			                      printdescription(Vanilla.documents[index[result.getSelectedRow()]].description.split("  ")));
			          
			          jta.setEditable(false);
			          jta.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			          JScrollPane jsp = new JScrollPane(jta);
			          jsp.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			          
			          
			          JOptionPane.showMessageDialog(null, jsp);
			          
			          
			          
			        }}
			      });
			      
			      
			      
			      
			      
			      
			      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			      result.setFillsViewportHeight( true );
			      result.getTableHeader().setReorderingAllowed(false);
			      
			      revalidate();
			      repaint();
			      
			      
			      
			      
			      setVisible(true);
			      
			    }
			    
			    
			    
			    
			    private void addComponents() {
			      panel.add(search,BorderLayout.EAST);
			      panel.add(VSMButton,BorderLayout.CENTER);
			      panel.add(BooleanButton,BorderLayout.WEST);
			      panel.add(scrollPane);
			      add(panel);
			      
			      
			    }
			    
			    private void Table() {
			      VSMButton.addActionListener(e -> result.setModel(makeModel(search.getText(),0)));
			      BooleanButton.addActionListener(f -> result.setModel(makeModel(search.getText(),1)));
			      
			      
			      
			    }
			  }
			    
			    
			    
			    
			  