package View;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import Controller.Controller;
import transactions.Transaction;
import transactions.WriteTransaction;

public class MainFrame extends JFrame{
	private static JTabbedPane tabbedPane;
	private static JPanel transactionsHolder;
	private JButton addBtn;
	private JButton runBtn;
	private Controller c;
	private static ArrayList<TransactionPanel> transactionList = new ArrayList<TransactionPanel>();
	private static JComboBox isolvlOptions;
	private static JTextArea logger;
	
	public MainFrame(Controller c) {
		try
		{
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
			{
				if ("Nimbus".equals(info.getName()))
				{
					UIManager.setLookAndFeel(info.getClassName());
					LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
					UIDefaults defaults = lookAndFeel.getDefaults();
					defaults.put("ScrollBar.minimumThumbSize", new Dimension(30, 30));
					break;
				}
			}
		}
		catch (Exception e)
		{
			// If Nimbus is not available, you can set the GUI to another
			// look and feel.
		}
		
		this.c = c;
		
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(900, 650);
		setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		this.setTitle("ADVANDB MCO3 - ");
		getContentPane().setLayout(new BorderLayout());
		
		getContentPane().add(addTransactionsControl(), BorderLayout.NORTH);
		getContentPane().add(addTransactionsHolder(), BorderLayout.CENTER);
		getContentPane().add(addLogHolder(), BorderLayout.SOUTH);
		this.setVisible(true);
	}
	
	public JPanel addLogHolder(){
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(10,120));
		panel.setLayout(new BorderLayout());
		logger = new JTextArea();
		logger.setLineWrap(true);
		logger.setEditable(false);
		logger.setOpaque(false);
		logger.setFont ( new Font ("monospaced", Font.PLAIN, 12));
	    JScrollPane scroll = new JScrollPane(logger);
	    scroll.setPreferredSize(new Dimension(50,50));
	    panel.add(new JLabel("  Logs"), BorderLayout.NORTH);
		panel.add(scroll, BorderLayout.CENTER);
		return panel;
	}
	
	public JPanel addTransactionsHolder(){
		JPanel transactionHolder = new JPanel();
		transactionHolder.setLayout(new BorderLayout());
		transactionHolder.setBorder(BorderFactory.createTitledBorder("Transaction Details"));
		tabbedPane = new JTabbedPane();
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
		transactionHolder.add(tabbedPane, BorderLayout.CENTER);
		this.addTransactionPanel(new TransactionPanel());
		this.addTransactionPanel(new TransactionPanel());
		return transactionHolder;
	}
	
	public JPanel addTransactionsControl(){
		JPanel controlHolder = new JPanel();
		Border border = BorderFactory.createTitledBorder("Transaction Controls");
		Border margin = BorderFactory.createEmptyBorder(10,10,10,10);
		controlHolder.setBorder(new CompoundBorder(border, margin));
		controlHolder.setPreferredSize(new Dimension(500,130));
		controlHolder.setLayout(new BorderLayout());
		
		transactionsHolder = new JPanel();
		transactionsHolder.setLayout(new BoxLayout(transactionsHolder, BoxLayout.Y_AXIS));
	    JScrollPane scroll = new JScrollPane(transactionsHolder);
	    scroll.setPreferredSize(new Dimension(50,50));
	    
	    controlHolder.add(addOptionsPanel(), BorderLayout.WEST);
	    controlHolder.add(scroll, BorderLayout.CENTER);
	    controlHolder.add(addTransactionOptions(), BorderLayout.EAST);
	    
		return controlHolder;
	}
	
	public JPanel addTransactionOptions(){
		JPanel btnPanel = new JPanel();
	    btnPanel.setLayout(new GridLayout(0,1));
	    
	    runBtn = new JButton("Run");
	    runBtn.addActionListener(new ButtonListener());
	    btnPanel.add(runBtn);
	    
	    addBtn = new JButton("+");
	    addBtn.addActionListener(new ButtonListener());
	    btnPanel.add(addBtn);
		
		return btnPanel;
	}
	
	public JPanel addOptionsPanel(){
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		JPanel optionsHolder = new JPanel();
		optionsHolder.setLayout(new GridLayout(0,1));
		
		JLabel l = new JLabel("Isolation Level:   ");
		l.setHorizontalAlignment(SwingConstants.RIGHT);
		//optionsHolder.add(l);
		
		isolvlOptions = new JComboBox(new String[] {WriteTransaction.ISO_READ_COMMITTED_LBL, WriteTransaction.ISO_READ_UNCOMMITTED_LBL, WriteTransaction.ISO_REPEATABLE_READ_LBL, WriteTransaction.ISO_SERIALIZABLE_LBL});
		isolvlOptions.setSelectedIndex(0);
		//optionsHolder.add(isolvlOptions);
		
		panel.add(optionsHolder, BorderLayout.SOUTH);
		return panel;
	}
	
	
	public void addTransactionPanel( TransactionPanel panel ){
		String name = panel.getName();
		JCheckBox cb = new JCheckBox(name);
		//cb.addItemListener(new checkBoxListener());
		cb.setBorder(BorderFactory.createEmptyBorder(1,5,1,5));
		transactionsHolder.add(cb);
		transactionsHolder.revalidate();
		transactionsHolder.repaint();
		
		transactionList.add(panel);
		tabbedPane.addTab(name, panel);
		tabbedPane.revalidate();
		tabbedPane.repaint();
	}
	
	public static void deleteTransactionPanel( JPanel panel ){
		transactionList.remove(panel);
		transactionsHolder.remove(findCheckBoxTransaction(panel.getName()));
		transactionsHolder.revalidate();
		transactionsHolder.repaint();
		tabbedPane.remove(panel);
		tabbedPane.revalidate();
		tabbedPane.repaint();
	}
	
	public static JCheckBox findCheckBoxTransaction( String text ){
		for( Component comp : transactionsHolder.getComponents() ) {
		   if( comp instanceof JCheckBox){
			   if (((JCheckBox)comp).getText().equals(text))
				   return (JCheckBox)comp;
		   }
		}
		return null;
	}
	
	public void runTransactions(){
		ArrayList<String> transNames = new ArrayList<String>();
		for( Component comp : transactionsHolder.getComponents() ) {
		   if( comp instanceof JCheckBox){
			   if (((JCheckBox)comp).isSelected())
				   transNames.add(((JCheckBox)comp).getText());
		   }
		}
		if (transNames.isEmpty()){
			log("Please select at least one transaction to run.");
		}
		else {
			// send needed info to controller
			ArrayList<Transaction> transactions = new ArrayList<Transaction>();
			for (String tablename : transNames ) {
				if ((findTransPanel(tablename)).getBooleanAbort())
					transactions.add((findTransPanel(tablename)).getTransactionDetails());
			}
			this.c.executeTransactions(transactions);
			//this.c.executeTransactions(query, scope, query2, scope2, isGlobal);
		}
	}
	
	public static int getIsoLevel(){
		switch(isolvlOptions.getSelectedItem().toString()){
			case WriteTransaction.ISO_READ_COMMITTED_LBL : 
				return WriteTransaction.ISO_READ_COMMITTED;
			case WriteTransaction.ISO_READ_UNCOMMITTED_LBL : 
				return WriteTransaction.ISO_READ_UNCOMMITTED;
			case WriteTransaction.ISO_REPEATABLE_READ_LBL : 
				return WriteTransaction.ISO_REPEATABLE_READ;
			case WriteTransaction.ISO_SERIALIZABLE_LBL : 
				return WriteTransaction.ISO_SERIALIZABLE;
			default:
				return -1;
		}
	}
	
	public TransactionPanel findTransPanel(String name){
		for (TransactionPanel panel : transactionList){
			if (panel.getName().equals(name))
				return panel;
		}
		return null;
	}
	
	public void updateTable( String tableName, ResultSet rs, ResultSet rs2){
		(findTransPanel(tableName)).updateTable(rs, rs2);
	}
	
	public static void log (String message) {
		if (!logger.getText().equals(""))
			logger.setText(logger.getText() + "\n\n" + message);
		else
			logger.setText( message);
	}
	
	public class ButtonListener implements ActionListener{
	    @Override
		public void actionPerformed(ActionEvent e) {
	    	JButton button = (JButton) e.getSource();
	    	if (button == addBtn){
	    		addTransactionPanel(new TransactionPanel());
	    	}
	    	else if (button == runBtn){
	    		runTransactions();
	    	}
	    	else {
	    		
	    	}
		}
	}
	
}
