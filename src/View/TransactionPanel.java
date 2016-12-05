package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import Model.Column;
import Model.ComboBoxConstants;
import Model.Constants;
import transactions.ReadTransaction;
import transactions.Transaction;
import transactions.WriteTransaction;
import transactions.ReadTransaction;;

public class TransactionPanel extends JPanel{
	
	private static final String SELECT_ALL = "Select All";
	private static int id = 1;
	private JPanel queriesHolder;
	private JTextArea queryDisplayer;
	private JPanel panelTemp;
	private JButton btnRemove;
	private JButton btnAdd;
	private JPanel bottomPanel;
	private JComboBox areaOptions;
	private JComboBox acOptions;
	private ButtonGroup group;
	private ArrayList<String> queriesList = Constants.ASIA_AFRICA_Q_TITLES;
	private JPanel resultsPane;
	private static final int CUST_HEIGHT = 110;
	
	public TransactionPanel(){
		this.setName("T" + id);
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(this.getSize().width, (int) (this.getSize().height*0.9) ));
		this.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		panelTemp = new JPanel();
		panelTemp.setLayout(new BorderLayout());
		panelTemp.setBackground(Color.lightGray);
		
		panelTemp.add(infoPanel(), BorderLayout.NORTH);
		panelTemp.add(createResultsPanel(), BorderLayout.CENTER);
		
		this.add(panelTemp, BorderLayout.CENTER);
		this.id++;
	}
	
	public JPanel createResultsPanel(){
		resultsPane = new JPanel();
		resultsPane.setLayout(new BorderLayout());
		resultsPane.setBorder(BorderFactory.createEmptyBorder(10,2,3,2));
		
		resultsPane.add(new JScrollPane(createJTable(null, null)), BorderLayout.CENTER);
		
		if (id > 2){
			btnRemove = new JButton("Remove");
			btnRemove.addActionListener(new ButtonListener());
			JPanel btnHolder = new JPanel();
			btnHolder.setLayout(new FlowLayout(FlowLayout.RIGHT));
			btnHolder.add(btnRemove);
			resultsPane.add(btnHolder, BorderLayout.SOUTH);
		}
		return resultsPane;
	}
	
	public JPanel infoPanel(){
		JPanel infoPanel = new JPanel();
		infoPanel.setPreferredSize(new Dimension(500,CUST_HEIGHT));
		infoPanel.setLayout(new RelativeLayout(RelativeLayout.X_AXIS));
		
		infoPanel.add(createLeftControl(), new Float(1));
		infoPanel.add(createRightControl(), new Float(2));
		return infoPanel;
	}
	
	public JPanel createLeftControl(){
		JPanel leftPanel = new JPanel();
		leftPanel.setPreferredSize(new Dimension (100,CUST_HEIGHT));
		leftPanel.setLayout(new BorderLayout());
		
		queriesHolder = new JPanel();
		queriesHolder.setLayout(new BoxLayout(queriesHolder, BoxLayout.Y_AXIS));
	    JScrollPane scroll = new JScrollPane(queriesHolder);
	    scroll.setPreferredSize(new Dimension(50,50));
	    
	    group = new ButtonGroup();
	    
	    leftPanel.add(scroll, BorderLayout.CENTER);
	    return leftPanel;
	}
	
	public void updateQueryList(){
		queriesHolder.removeAll();
		queriesHolder.revalidate();
		queriesHolder.repaint();
		
		for (String title : queriesList){
			addQueryChoice(title);
		}
	}
	
	public void addQueryChoice(String text) {
		JRadioButton button = new JRadioButton(text);
		group.add(button);
	//	cb.addItemListener(new checkBoxListener());
		button.setBorder(BorderFactory.createEmptyBorder(1,5,1,5));
		queriesHolder.add(button);
		queriesHolder.revalidate();
		queriesHolder.repaint();
	}
	
	public JTable createJTable(ResultSet rs, ResultSet rs2) {
		if ( rs != null ){
			JTable table = new JTable();
			DefaultTableModel dataModel = new DefaultTableModel();
			table.setModel(dataModel);
			
			try {
				ResultSetMetaData mdata = rs.getMetaData();
				int colCount = mdata.getColumnCount();		
				String[] colNames = getColumnNames(colCount, mdata);
				dataModel.setColumnIdentifiers(colNames);
				while (rs.next()) {
					String[] rowData = new String[colCount];
					for (int i = 1; i <= colCount; i++) {
						rowData[i - 1] = rs.getString(i);
					}
					dataModel.addRow(rowData);
				}
			} catch (SQLException e) {}
			
			if (rs2 != null){
				try {
					ResultSetMetaData mdata = rs2.getMetaData();
					int colCount = mdata.getColumnCount();		
					String[] colNames = getColumnNames(colCount, mdata);
					dataModel.setColumnIdentifiers(colNames);
					while (rs2.next()) {
						String[] rowData = new String[colCount];
						for (int i = 1; i <= colCount; i++) {
							rowData[i - 1] = rs2.getString(i);
						}
						dataModel.addRow(rowData);
					}
				} catch (SQLException e) {}
			}
			
			return table;
		}
		else {
			JTable table = new JTable();
			return table;
		}
	}
	
	public String[] getColumnNames(int colCount, ResultSetMetaData mdata) throws SQLException {
		String[] colNames = new String[colCount];
		for (int i = 1; i <= colCount; i++) {
			String col = mdata.getColumnName(i);
			colNames[i-1] = col;
		}
		return colNames;
	}
	
	private void updateRowHeights(JTable table) {
		try {
			for (int row = 0; row < table.getRowCount(); row++) {
				int rowHeight = table.getRowHeight();

				for (int column = 0; column < table.getColumnCount(); column++) {
					Component comp = table.prepareRenderer(table.getCellRenderer(row, column), row, column);
					rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
				}

				table.setRowHeight(row, rowHeight);
			}
		} catch (ClassCastException e) {}
	}
	
	public void updateTable(ResultSet rs, ResultSet rs2) {
		resultsPane.removeAll();
		JTable table = createJTable(rs, rs2);
		JScrollPane pane = new JScrollPane(table);
		updateRowHeights(table);
		resultsPane.add(pane, BorderLayout.CENTER);
		resultsPane.revalidate();
		resultsPane.repaint();
	}
	
	public void setQuery(String query){
		queryDisplayer.setText(queryDisplayer.getText() + "\n\n" + query);
	}
	
	public JPanel createRightControl() {
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(100, CUST_HEIGHT));
		panel.setLayout(new BorderLayout());
		bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
		JScrollPane scroll = new JScrollPane(bottomPanel);
	    scroll.setPreferredSize(new Dimension(50,50));
	    
		panel.add(scroll, BorderLayout.CENTER);
		
		JPanel addBtnContainer = new JPanel();
		addBtnContainer.setLayout(new RelativeLayout(RelativeLayout.X_AXIS));
		
		JLabel la = new JLabel("Area:   ");
		la.setHorizontalAlignment(SwingConstants.RIGHT);
		addBtnContainer.add(la, new Float(1));
		
		areaOptions = new JComboBox(new String[] {Constants.REGION_ASIA_AFRICA, Constants.REGION_EUROPE_AMERICA, Constants.REGION_BOTH});
		areaOptions.addActionListener(new ButtonListener());
		areaOptions.setSelectedIndex(0);
		addBtnContainer.add(areaOptions, new Float(1.5));
		
		JLabel lab = new JLabel("Abort or Commit:   ");
		lab.setHorizontalAlignment(SwingConstants.RIGHT);
		addBtnContainer.add(lab, new Float(2));
		
		acOptions = new JComboBox(new String[] {"Abort", "Commit"});
		acOptions.setSelectedIndex(0);
		addBtnContainer.add(acOptions, new Float(1));
		
		JLabel l = new JLabel("Filtering Options   ");
		l.setHorizontalAlignment(SwingConstants.RIGHT);
		addBtnContainer.add(l, new Float(2));
		btnAdd = new JButton("+");
		btnAdd.setPreferredSize(new Dimension(50,20));
		btnAdd.addActionListener(new ButtonListener());
		addBtnContainer.add(btnAdd, new Float(1));
		panel.add(addBtnContainer, BorderLayout.NORTH);
		
		return panel;
	}
	
	private void addFilteringOption(){
		ArrayList<Column> columns = ComboBoxConstants.OPTIONS_QUERY;
	    JPanel filterOption = new JPanel();
	    filterOption.setLayout(new RelativeLayout(RelativeLayout.X_AXIS));
	    if (bottomPanel.getComponents().length != 0){
	    	JComboBox opList = new JComboBox(new String[] {"AND" , "OR"});
		    opList.setSelectedIndex(0);
		    filterOption.add(opList, new Float(1));
	    }
	    
	    ArrayList<String> cols = new ArrayList<String>();
	    for (Column c: columns){
	    	cols.add(c.getName());
	    }
	    JComboBox colList = new JComboBox(cols.toArray());
	    colList.setSelectedIndex(0);
	    filterOption.add(colList, new Float(2));
	    
	    JComboBox funcList = new JComboBox(getFunctions());
	    colList.setSelectedIndex(0);
	    filterOption.add(funcList, new Float(1));
	    
	    JTextField text = new JTextField(10);
	    filterOption.add(text, new Float(2));
	    
	    JButton btnRemove = new JButton("-");
	    btnRemove.addActionListener(new ButtonListener());
	    filterOption.add(btnRemove, new Float (1));
	    
	    bottomPanel.add(filterOption);
	    bottomPanel.revalidate();
	    bottomPanel.repaint();
	}
	
	private void removeFilteringOption(JPanel panel){
		if (bottomPanel.getComponentZOrder(panel) == 0 && bottomPanel.getComponentCount() > 1){
			((JPanel)bottomPanel.getComponent(1)).remove(((JPanel)bottomPanel.getComponent(1)).getComponent(0));
		}
		bottomPanel.remove(panel);
		bottomPanel.revalidate();
		bottomPanel.repaint();
	}
	
	private String[] getFunctions(){
		ArrayList<String> funcList = new ArrayList<String>();
		funcList.add("=");
		funcList.add(">");
		funcList.add("<");
		funcList.add(">=");
		funcList.add("<=");
		funcList.add("IS");
		return funcList.stream().toArray(String[]::new);
	}
	
	public String getQuerySelected() {
        for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            
            if (this.queriesList.equals(Constants.EUROPE_AMERICA_Q_TITLES)){
	            if (button.isSelected()) {
	            	switch(button.getText()){
	            	case Constants.EUROPE_AMERICA_TITLE1: return Constants.EUROPE_AMERICA_QUERY1;
	            	case Constants.EAQUERY_TITLE2: return Constants.EAQUERY_2;                   
	            	case Constants.EAQUERY_TITLE3: return Constants.EAQUERY_3;                   
	            	case Constants.EAQUERY_TITLE4: return Constants.EAQUERY_4;                   
	            	case Constants.EAQUERY_TITLE5: return Constants.EAQUERY_5;    
	            	}
	            }
            }
            else if (this.queriesList.equals(Constants.ASIA_AFRICA_Q_TITLES)){
	            if (button.isSelected()) {
	            	switch(button.getText()){               
            		case Constants.ASIA_AFRICA_TITLE1: return Constants.ASIA_AFRICA_QUERY1;
            		case Constants.AAQUERY_TITLE2: return Constants.AAQUERY_2;
            		case Constants.AAQUERY_TITLE3: return Constants.AAQUERY_3;
            		case Constants.AAQUERY_TITLE4: return Constants.AAQUERY_4;
            		case Constants.AAQUERY_TITLE5: return Constants.AAQUERY_5;
	            	}
	            }
            }
            else if (this.queriesList.equals(Constants.BOTH_Q_TITLES)){
	            if (button.isSelected()) {
	            	switch(button.getText()){
	            		case Constants.BQUERY_TITLE1: return Constants.BQUERY_1;
	            		case Constants.BQUERY_TITLE2: return Constants.BQUERY_2;
	            	}
	            }
            }
        }
        return "";
	}
	
	public String getArea(){
		return this.areaOptions.getSelectedItem().toString();
	}
	
	public boolean getBooleanAbort(){
		if (this.acOptions.getSelectedItem().equals("Commit"))
			return true;
		return false;
	}
	
	public String getQuery(){
		String query = getQuerySelected();
		ArrayList<String> lowerChoices = new ArrayList<String>();
		for( Component comp : bottomPanel.getComponents() ) {
			String condition = "";
			for ( Component c : ((Container) comp).getComponents()){
				String text = "";
				if( c instanceof JComboBox){
					text = (String)((JComboBox)c).getSelectedItem();
				   	if (!(text.equals("AND") || text.equals("OR"))){
				   		try{
				   			text = ComboBoxConstants.findColumn(text).getColName();
				   		}
				   		catch (NullPointerException e){}
				   	}
				}
				else if (c instanceof JTextField){
					text = (String) ((JTextField)c).getText();
					if (!(text.equalsIgnoreCase("NULL") || text.equalsIgnoreCase("NOT NULL"))){
						text = "'" + (String) ((JTextField)c).getText() + "'";
					}
				}
				condition += text + " ";
			}
			if (!(condition.contains("AND") || condition.contains("OR"))){
				condition = "AND " + condition;
			}
			lowerChoices.add(condition);
		}
		
		String having = "";
		String where = "";
		
		if (!(lowerChoices.isEmpty())){
			Collections.sort(lowerChoices, new SecondWordComparator());
			boolean isSame = false, editedWhere = false, editedHaving = false;
			for (int i = 0; i < lowerChoices.size(); i++){
				String condition1 = lowerChoices.get(i);
				String[] temp1 = condition1.split(" ");
				if (condition1.contains("SUM")){
					if (!editedHaving){
						having = "HAVING (" + condition1.substring(4, condition1.length()) + " \n";
					}
					else{
						having += condition1 + " \n";
					}
					editedHaving = true;
				}
				else{
					if (!editedWhere){
						where += "WHERE (" + condition1.substring(4, condition1.length()) + " \n";
					}
					if (i!=0){
						if (isSame ){
							where += condition1 + " \n";
						}
						else{
							where += ") \n" + temp1[0] + "(" + condition1.substring(temp1[0].length(), condition1.length()) + " \n";
						}
					}
					editedWhere = true;
				}
				if (i == lowerChoices.size()-1){
					break;
				}
				String condition2 = lowerChoices.get(i+1);
				String[] temp2 = condition2.split(" ");
				isSame = temp1[1].equals(temp2[1]);
			}
			if (editedHaving)
				having += ") \n";
			if (editedWhere)
				where += ") \n";
		}
		MainFrame.log(query + where);
		return query + where;
	}
	
	public Transaction getTransactionDetails(){
		String query = getQuery();
		Transaction transaction = null;
		if (query.contains("UPDATE") || query.contains("DELETE")){
			transaction = new WriteTransaction(query, getArea(), getBooleanAbort(), MainFrame.getIsoLevel());
			transaction.setName(this.getName());
		}
		else if (query.contains("SELECT")){
			transaction = new ReadTransaction(query, getArea(), MainFrame.getIsoLevel() );
			transaction.setName(this.getName());
		}
		return transaction;
	}
	
	public class ButtonListener implements ActionListener{
	    @Override
		public void actionPerformed(ActionEvent e) {
	    	
	    	if (e.getSource() instanceof JButton){
		    	JButton button = (JButton) e.getSource();
		    	if (button.getText().equals("Remove")){
		    		MainFrame.deleteTransactionPanel((JPanel)button.getParent().getParent().getParent().getParent());
		    	}
		    	else if (button == btnAdd){
		    		addFilteringOption();
		    	}
		    	else {
		    		JPanel panel = (JPanel)button.getParent();
		    		removeFilteringOption(panel);
		    	}
	    	}
	    	else if (e.getSource() instanceof JComboBox){
	    		switch(((JComboBox)e.getSource()).getSelectedItem().toString()){
	    			case Constants.REGION_ASIA_AFRICA: 
	    				queriesList = Constants.ASIA_AFRICA_Q_TITLES; 
	    				break;
	    			case Constants.REGION_EUROPE_AMERICA: 	
	    				queriesList = Constants.EUROPE_AMERICA_Q_TITLES; 
	    				break;
	    			default: 
	    				queriesList = Constants.BOTH_Q_TITLES;
						break;
	    		}
	    		updateQueryList();
	    	}
		}
	}

}
