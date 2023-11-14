package utils;

import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import transactionController.DBclass;

@SuppressWarnings("serial")
public class ManageTransactions extends JFrame {
    static ArrayList<JFrame> frameList = new ArrayList<>();
    
    // connection with DB
	//static Database db = new Database();
	static Connection con;// = getConnection();
	
	// Table UI
	static JTable table;
	static JComboBox<String> comboBoxInOrOut;
	static JComboBox<String> comboBoxBranchPath;
	static JTextField textStartDate;
	static JTextField textEndDate;
	int total_in = 0; int total_out = 0;

	// Query
	class Query {
    	public String beginDate = "0001-01-01";
    	public String endDate = "9999-12-31";
    	public String branch = "Root";
    	public String in_out = "In&Out";
    }
	Query mainQuery = new Query();

	// DB info
	static ArrayList<ArrayList<String>> fields_info;
	static ArrayList<String> idCollection = new ArrayList<>();
    static HashMap<String, String> typeDictionary = new HashMap<>();
    static List<String> paths;
    static List<String> extra_fields = new ArrayList<>();
    
    private DefaultTableModel tableModel;

    public ManageTransactions() {
    	con = DBclass.getConnection();
    	fields_info = DBclass.collectFields(con);
    	System.out.println(fields_info);
        paths = TreePersistence.collectPaths();
        // Connect to Database
        
    	setTitle("Manage Transactions");
        setSize(800, 600); // 조정 가능한 크기
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // UI components
        JLabel labelStartDate = new JLabel("Begin date(yyyy-mm-dd):");
        JLabel labelEndDate = new JLabel("End date(yyyy-mm-dd):");
        textStartDate = new JTextField(mainQuery.beginDate);
        textEndDate = new JTextField(mainQuery.endDate);
        String[] branches = new String[paths.size()];
        for(int i = 0; i < paths.size(); i++) {
           branches[i] = paths.get(i);
        }
        
        comboBoxBranchPath = new JComboBox<>(branches);
        String[] transactionTypes = {"In&Out","In", "Out"};
        comboBoxInOrOut = new JComboBox<>(transactionTypes);

        JButton buttonInquire = new JButton("Inquire");
        JButton buttonDataViewer = new JButton("Data Viewer");
        JButton buttonAdd = new JButton("Add");
        JButton buttonDelete = new JButton("Delete");
        JButton buttonQuerySetting = new JButton("Query Setting");
        JButton buttonExit = new JButton("Exit");
        
        // Layout setup
        setLayout(new BorderLayout());

        // Top Panel with GridBagLayout for inputs and buttons
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4); // margin around components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add start date label and text field
        gbc.gridx = 0;	gbc.gridy = 0;
        topPanel.add(labelStartDate, gbc);
        gbc.gridx = 1;	gbc.gridy = 0;	gbc.gridwidth = 3;
        topPanel.add(textStartDate, gbc);

        // Add end date label and text field
        gbc.gridx = 0;	gbc.gridy = 1;	gbc.gridwidth = 1;
        topPanel.add(labelEndDate, gbc);
        gbc.gridx = 1;	gbc.gridy = 1;	gbc.gridwidth = 3;
        topPanel.add(textEndDate, gbc);

        // Add branch path combobox
        gbc.gridx = 0;	gbc.gridy = 2;	gbc.gridwidth = 1;
        topPanel.add(new JLabel("Branch path:"), gbc);
        gbc.gridx = 1;	gbc.gridy = 2;	gbc.gridwidth = 3;
        topPanel.add(comboBoxBranchPath, gbc);

        // Add transaction type combobox
        gbc.gridx = 0;	gbc.gridy = 3;	gbc.gridwidth = 1;
        topPanel.add(new JLabel("In or Out:"), gbc);
        gbc.gridx = 1;	gbc.gridy = 3;	gbc.gridwidth = 3;
        topPanel.add(comboBoxInOrOut, gbc);

        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(buttonInquire);
        buttonPanel.add(buttonQuerySetting);
        buttonPanel.add(buttonAdd);
        buttonPanel.add(buttonDelete);
        buttonPanel.add(buttonDataViewer);
        
        gbc.gridx = 0;	gbc.gridy = 4;	gbc.gridwidth = 4;
        topPanel.add(buttonPanel, gbc);

        // Add table to the center        
        tablesHeaders();

        //String sqlQuery = makeQuerySql();
        String sql = "SELECT * FROM transactions ORDER BY transaction_date, updateTime";
        tableSetting(sql);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        
        table.setFillsViewportHeight(true);

        // Disable table row reordering and resizing
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        
        // Add top panel and table to the frame
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Exit button at the bottom
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(buttonExit);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Event listeners and other logic can be added as needed
        buttonDataViewer.addActionListener(e->{
        	System.out.println(total_in);
        	System.out.println(total_out);
        });
        
        buttonExit.addActionListener(e->{
            for(int i = 0; i < frameList.size(); i++){
                frameList.get(i).dispose();
            }
            frameList = new ArrayList<>();
            this.dispose();
        });
        
        
        buttonAdd.addActionListener(e->{
            String selectedValue = (String) comboBoxInOrOut.getSelectedItem();
            System.out.println(selectedValue);
            addition();
        });
        buttonDelete.addActionListener(e->{
        	deletion();
        });
        buttonInquire.addActionListener(e->{
           inquireTable();
        });
    }
    
    private void tablesHeaders() {
        //Object[] fields = new Object[fields_info.size()];
        Object[] header = new Object[fields_info.size() + 1];
        header[0] = "transaction_date";
        header[1] = "branch";
        header[2] = "in";
        header[3] = "out";
        header[4] = "balance";
        header[5] = "updateTime";

        for(int i = 0; i < fields_info.size(); i++) {
        	typeDictionary.put(fields_info.get(i).get(0), fields_info.get(i).get(1));
        	String field_name = fields_info.get(i).get(0);
        	if(DBclass.default_fields.contains(field_name) == false) {
        		extra_fields.add(field_name);
        	}
        }

        int j = 0;
        for(int i = 6; i < fields_info.size() + 1; i++) {
           header[i] = extra_fields.get(j++);
        }
        
        tableModel = new DefaultTableModel(header, 0); // 0 for row count
    }
    
    private void tableSetting(String query) {
    	idCollection.clear();
        for (int i = tableModel.getRowCount() - 1; i >= 0; i--) {
     	   tableModel.removeRow(i);
        }

        ArrayList<ArrayList<String>> tableData = DBclass.fetchDataFromDatabase(con, query);
        int balance = 0;
        
        total_in = 0; total_out = 0;
        for(int i = 0; i < tableData.size(); i++) {
        	System.out.println(tableData.get(i));
           
            Object[] row = new Object[fields_info.size() + 1];
            idCollection.add(tableData.get(i).get(0));
            row[0] = tableData.get(i).get(1); // transaction_date
            row[1] = tableData.get(i).get(3); // branch
            row[2] = "0"; row[3] = "0";
            int cashFlow = Integer.parseInt(tableData.get(i).get(2));
            if(cashFlow > 0) { // in
                row[2] = String.valueOf(cashFlow);
                total_in += cashFlow;
            }else{ // out
                row[3] = String.valueOf(-cashFlow);
                total_out += (-cashFlow);
            }
           
            balance += cashFlow;
            row[4] = String.valueOf(balance);
            row[5] = tableData.get(i).get(4); // updateTime
           for(int j = 6; j < fields_info.size() + 1; j++) { // extra attributes
              row[j] = tableData.get(i).get(j-1);
           }
            tableModel.addRow(row);
        }
        System.out.println(idCollection);        
    }
    
    private void deletion() {
    	try {
            int row = table.getSelectedRow();
            String id = idCollection.get(row);
            DBclass.deleteData(con, id);
            String sql = makeQuerySql();
            tableSetting(sql);
    	}catch(Exception e) {
            System.out.println(e);    		
    	}
    }
    
    private String makeQuerySql(){
        //Date Query
    	String sqlQuery = "SELECT * FROM transactions";
        sqlQuery += " WHERE '" + mainQuery.beginDate + "' <= transaction_date AND transaction_date <='" + mainQuery.endDate + "'";
        
        // In Out
        switch(mainQuery.in_out) {
            case "In":
                sqlQuery += "And cashFlow > 0";
                break;
            case "Out":
                sqlQuery += "And cashFlow < 0";
                break;
        }
        
        //Order
        sqlQuery += "And(branch=" + "'" + mainQuery.branch + "'";
        sqlQuery += " or branch LIKE" + " '" + mainQuery.branch + "/%')";
        sqlQuery += "ORDER BY transaction_date, updateTime;";
        
        return sqlQuery;
    }    
    
    private void inquireTable() {
    	String sqlQuery = "SELECT * FROM transactions";    	
    	String beginDate = (String) textStartDate.getText();
    	String endDate = (String) textEndDate.getText();
        String selectedInOrOut = (String) comboBoxInOrOut.getSelectedItem();
        String selectedBranch = (String) comboBoxBranchPath.getSelectedItem();
       
       if(DataHandler.isValidDate(beginDate)==false)return;
       if(DataHandler.isValidDate(endDate)==false)return;
       if(DataHandler.compareDates(beginDate, endDate)==false)return;
              
       mainQuery.branch = selectedBranch;
       mainQuery.beginDate = beginDate;
       mainQuery.endDate = endDate;
       mainQuery.in_out = selectedInOrOut;
       String sql = makeQuerySql();
       tableSetting(sql);
    }

    private void addition() {
        String[] branches = new String[paths.size()];
        for(int i = 0; i < paths.size(); i++) {
           branches[i] = paths.get(i);
        }

        // 기본 프레임 생성
        JFrame frame = new JFrame("Input Form");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 레이아웃 설정
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // 지점 선택 콤보박스
        JPanel branchPanel = new JPanel();
        branchPanel.add(new JLabel("Branch:"));
        JComboBox<String> branchComboBox = new JComboBox<>(branches);
        branchComboBox.setSelectedItem(mainQuery.branch);
        branchPanel.add(branchComboBox);
        mainPanel.add(branchPanel);

        // 트랜잭션 날짜 입력
        JPanel datePanel = new JPanel();
        // 트랜잭션 날짜 입력
        datePanel.add(new JLabel("YY(date):"));
        JTextField inputYear = new JTextField(8);
        datePanel.add(inputYear);

        datePanel.add(new JLabel("MM(date):"));
        JTextField inputMonth = new JTextField(4);
        datePanel.add(inputMonth);

        datePanel.add(new JLabel("DD(date):"));
        JTextField inputDay = new JTextField(4);
        datePanel.add(inputDay);

        mainPanel.add(datePanel);

        // In/Out 입력
        JPanel inPanel = new JPanel();
        inPanel.add(new JLabel("In:"));
        JTextField inInput = new JTextField(15);
        inPanel.add(inInput);
        mainPanel.add(inPanel);

        JPanel outPanel = new JPanel();
        outPanel.add(new JLabel("Out:"));
        JTextField outInput = new JTextField(15);
        outPanel.add(outInput);
        mainPanel.add(outPanel);

        // 추가 필드 입력
        ArrayList<JTextField> extraInputList = new ArrayList<>();
        
        for (int i = 0; i < extra_fields.size(); i++) {
        	String field = extra_fields.get(i);
            System.out.println(field);
            JPanel extraPanel = new JPanel();
            extraPanel.add(new JLabel(field + ":"));
            JTextField extraInput = new JTextField(10);
            extraInputList.add(extraInput);
            extraPanel.add(extraInput);
            mainPanel.add(extraPanel);
        }

        // 에러 메시지 출력용 레이블
        JLabel messageLabel = new JLabel(" ");
        mainPanel.add(messageLabel);

        // 버튼 추가 및 액션 정의
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> {
            // 추가 버튼 클릭 시 로직
            String transaction_date = "";
            try{
                int yy = Integer.parseInt(inputYear.getText());
                int mm = Integer.parseInt(inputMonth.getText());
                int dd = Integer.parseInt(inputDay.getText());
                if(!(yy > 0 && mm > 0 && dd > 0)){
                    throw new Exception("Error: input date value");
                }

                if(yy<100)yy+=2000;
                transaction_date += String.valueOf(yy);
                transaction_date += "-" + String.valueOf(mm+100).substring(1);
                transaction_date += "-" + String.valueOf(dd+100).substring(1);

            }catch(Exception e1){
                // nothing..
                messageLabel.setText("There is a problem with the input");
                System.out.println(e1);
                return;
            }

            String branch = branchComboBox.getSelectedItem().toString();
            String input = inInput.getText();
            String output = outInput.getText();
            ArrayList<String> extra_values = new ArrayList<>();
            for(int i = 0; i < extra_fields.size(); i++) {
            	extra_values.add(extraInputList.get(i).getText());
            }
            if(isValidAddition(transaction_date, input, output, extra_values)) {
            	additionQuery(transaction_date, branch, input, output, extra_values);
                String sqlQuery = makeQuerySql();
                tableSetting(sqlQuery);		
                messageLabel.setText("added");
                frame.dispose();
            }else{
                messageLabel.setText("There is a problem with the input");
            }
        });
        buttonPanel.add(addButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> frame.dispose()); // 창 닫기
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel);

        // 프레임 설정
        frame.add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null); // 중앙에 위치
        frame.setVisible(true);
        frameList.add(frame);
    }

    private void additionQuery(String transaction_date, String branch, String input, String output,
			ArrayList<String> extra_values) {
    	int cashFlow = 0;
		if(input.length() > 0) {
			cashFlow += Integer.parseInt(input);
		}else {
			cashFlow -= Integer.parseInt(output);
		}

		String sql = "INSERT INTO transactions (transaction_date, branch, cashFlow, updateTime";
		for(int i = 0; i < extra_fields.size(); i++) {
			sql += ", " + extra_fields.get(i);
		}
		sql += ") ";
		
		sql += "VALUES (";
		sql += "'" + transaction_date + "'";
		sql += ",'" + branch + "'";
		sql += "," + String.valueOf(cashFlow);
		sql += ",'" + DataHandler.getCurrentFormattedTime() + "'";
		for(int i = 0; i < extra_values.size(); i++) {
			if(extra_values.get(i).length() == 0) {
				sql += ", NULL";
			}else {
				String type = typeDictionary.get(extra_fields.get(i));
				if(type.substring(0,3) == "int") {
					sql += ", " + extra_values.get(i);
				}else {
					sql += ", '" + extra_values.get(i) + "'";
				}				
			}
		}
		sql += ") ";
		System.out.println(sql);
		try {			
	        PreparedStatement insert = con.prepareStatement(sql);
	        insert.execute();
			System.out.println("Addition Success");
		}catch(Exception e){
			System.out.println(e);
		}
		
	}

	private static boolean isValidAddition(String transaction_date, String input, String output, ArrayList<String> extra_values) {
    	if (DataHandler.isValidDate(transaction_date) == false) {
    		return false;
    	}
    	try {
    		int inputValue = 0, outputValue = 0;
    		if(input.length() > 0) {
    			inputValue += Integer.parseInt(input);
    		}
    		if(output.length() > 0) {
    			outputValue += Integer.parseInt(output);
    		}
    		if(inputValue > 0 ^ outputValue > 0 == false) {
    			return false;
    		}
    		for(int i = 0; i < extra_values.size(); i++) {
				String type = typeDictionary.get(extra_fields.get(i));
				if(type.substring(0, 3) == "int" && extra_values.get(i).length() > 0) {
					int testInt = Integer.parseInt(extra_values.get(i));
				}
    		}
    		return true;
    	}catch(Exception e) {
    		return false;
    	}
	}

	public static Connection getConnection() {
		try {
			String url = "jdbc:h2:~/test"; // 데이터베이스 URL
			String user = "sa"; // 사용자 이름
			String password = ""; // 비밀번호
			
			// 연결 시도
			Connection conn = DriverManager.getConnection(url, user, password);
			System.out.println("The Connection Succesful!");
			return conn;
		}catch(Exception e) {
			System.out.println("Connection has been fail.. "); 
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> new ManageTransactions().setVisible(true));
    }
}
