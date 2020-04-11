     import javax.swing.*;
     import javax.swing.event.ListSelectionEvent;
     import javax.swing.event.ListSelectionListener;
     import javax.swing.table.DefaultTableModel;
     import javax.swing.table.TableModel;
   
     import java.awt.*;
     import java.util.ArrayList;
     import java.awt.event.ActionListener;
     import java.awt.event.ActionEvent;
   
     public class UI extends JFrame {
    
       private JTextField search = new JTextField(30);
       private JButton VSMButton = new JButton("VSM Search");
       private JButton BooleanButton = new JButton("Boolean Search");

       private JTable result = new JTable();
       private JPanel panel = new JPanel();
       private JScrollPane scrollPane = new JScrollPane(result);
       private VanillaSystem Vanilla = new VanillaSystem();
       boolean reuters=false;
       private SpellCorrector correct= new SpellCorrector(Vanilla.dictionary,reuters);
       String[] choices = { "UOttawa","Reuters"};
       
       final JComboBox<String> collection = new JComboBox<String>(choices);
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
            
            index=Vanilla.booleanSearchWithQuery(info,reuters);
           
            for(int i:index) {
                if(reuters){
                  model.addRow(new Object [] {Vanilla.dictionary.reutersDocumentList.get(i).title});
                }else{
                  model.addRow(new Object [] {Vanilla.dictionary.uottawaDocumentList.get(i).title});

                }
                     
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
            index=Vanilla.vectorSearchWithQuery(info,reuters);
           
             for(int i:index) {
              if(reuters){
                  model.addRow(new Object [] {Vanilla.dictionary.reutersDocumentList.get(i).title});
                }else{
                  model.addRow(new Object [] {Vanilla.dictionary.uottawaDocumentList.get(i).title});

                }      
            }
              

       
          }}catch(Exception e) {
            System.out.println(e);
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
         
         setSize(650, 625);
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
             if(reuters){
                jta.setText("Title: "+Vanilla.dictionary.reutersDocumentList.get(index[result.getSelectedRow()]).title+"\n\n"+
                            "Date: " +Vanilla.dictionary.reutersDocumentList.get(index[result.getSelectedRow()]).date+"\n"+
                            "Topics: "+Vanilla.dictionary.reutersDocumentList.get(index[result.getSelectedRow()]).topics+"\n"+
                            "Places: "+Vanilla.dictionary.reutersDocumentList.get(index[result.getSelectedRow()]).places+"\n"+
                            "People: "+Vanilla.dictionary.reutersDocumentList.get(index[result.getSelectedRow()]).people+"\n"+
                            "Orgs: "+Vanilla.dictionary.reutersDocumentList.get(index[result.getSelectedRow()]).orgs+"\n"+
                            "Exchanges: "+ Vanilla.dictionary.reutersDocumentList.get(index[result.getSelectedRow()]).exchanges+"\n"+
                            "Companies: "+ Vanilla.dictionary.reutersDocumentList.get(index[result.getSelectedRow()]).companies+"\n"+
                            "Dateline: "+Vanilla.dictionary.reutersDocumentList.get(index[result.getSelectedRow()]).dateline+"\n"+
                            "Description:"+printdescription(Vanilla.dictionary.reutersDocumentList.get(index[result.getSelectedRow()]).description.split("  ")));
             }else{
                 jta.setText(Vanilla.dictionary.uottawaDocumentList.get(index[result.getSelectedRow()]).title+"\n\n"+
                         printdescription(Vanilla.dictionary.uottawaDocumentList.get(index[result.getSelectedRow()]).description.split("  ")));

             }
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
         panel.add(collection);

         add(panel);
         
         
       }
       
       public void Table() {
         VSMButton.addActionListener(e -> result.setModel(makeModel(search.getText(),0)));
         BooleanButton.addActionListener(f -> result.setModel(makeModel(search.getText(),1)));
         
          ActionListener cbActionListener = new ActionListener() {
           
            public void actionPerformed(ActionEvent e) {

                String s = (String) collection.getSelectedItem();//get the selected item
                if(s.equals("Reuters")){
                  reuters=true;
                }else{
                  reuters=false;
                }
     }
   };
   collection.addActionListener(cbActionListener);
 }
}

     
       
       

       
       
     