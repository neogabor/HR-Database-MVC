package HR_database_MVC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TreeMap;

public class Model implements ConnectionData{
    private Connection connection=null;
    private ArrayList<Object> columnNames=null;
    private ArrayList<Object> datas1=null;
    private HashSet<Integer> departmentNums=new HashSet<>();
    private TreeMap<Integer,String> departmentNames=new TreeMap<>();
    
    private void open()throws OwnException{
        try {
            Class.forName(DRIVER);
            connection=DriverManager.getConnection(URL,USER,PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new OwnException(e.getMessage()); 
        }catch (SQLException e){
            throw new OwnException(e.getMessage());
        }
    }
    private void close(){
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println("Probléma zárásnál");
        }
    }
    
    public Object[] columnNames(String source) throws OwnException{
        columnNames = new ArrayList<>();
        columnNames.clear();
        open();
        String SQL=
                "SELECT COLUMN_NAME "
                +"FROM ALL_TAB_COLS "
                +"WHERE TABLE_NAME = ?";
        Locale magyar = new Locale("hu", "HU");
        ResourceBundle translationPackage=ResourceBundle.getBundle("HR_database_MVC.Translate", magyar);
        try {
            PreparedStatement request = connection.prepareStatement(SQL);
            request.setString(1, source);
            ResultSet result = request.executeQuery();
            while(result.next()){
                columnNames.add(translationPackage.getString(result.getString("COLUMN_NAME")));
            }
        } catch (SQLException e) {
            throw new OwnException(e.getMessage());
        }
        close();
        return columnNames.toArray();
    }
    
    public Object[][] data(String source, int columns)throws OwnException{
        datas1=new ArrayList<>();
        open();
        String SQL=
                "SELECT *\n"
                +"FROM "+source+"\n";
        try {
            Statement request = connection.createStatement();
            ResultSet result = request.executeQuery(SQL);
            while(result.next()){
                for (int i = 1; i <= columns; i++) {
                 datas1.add(result.getString(i)); 
                }
                if (source.equals("EMPLOYEES")) {
                    departmentNums.add(result.getInt("DEPARTMENT_ID"));
                }
                if (source.equals("DEPARTMENTS")) {
                    if (departmentNums.contains(result.getInt("DEPARTMENT_ID"))) {
                        departmentNames.put(result.getInt("DEPARTMENT_ID"), result.getString("DEPARTMENT_NAME"));
                    }
                }
            }
        } catch (SQLException e) {
            throw new OwnException(e.getMessage());
        }
        close();
        Object[][] dataArray = new Object[datas1.size()/columns][columns];
        int k = 0;
        for (int i = 0; i < datas1.size()/columns; i++) {
            for (int j = 0; j < columns; j++) {
                if(source.equals("EMPLOYEES")) {
                    if((j >= 1 && j <= 6) || j == 8){
                        if (datas1.get(k) == null) {
                            dataArray[i][j] = "";
                        }
                        else dataArray[i][j] = datas1.get(k).toString();  
                    }
                    else{
                        if (datas1.get(k)==null) {
                            dataArray[i][j]=null;
                        }
                        else dataArray[i][j] = Integer.parseInt(datas1.get(k).toString());
                    }
                } else if(source.equals("DEPARTMENTS")) {
                  if(j == 1){
                        if (datas1.get(k) == null) {
                            dataArray[i][j] = "";
                        }
                        else dataArray[i][j] = datas1.get(k).toString();  
                    }
                    else{
                        if (datas1.get(k) == null) {
                            dataArray[i][j] = null;
                        }
                        else dataArray[i][j] = Integer.parseInt(datas1.get(k).toString());
                    }  
                }
                
                k++;
            }
        }
        return dataArray;
    }

    public ArrayList<String> getDepartments() {
        departmentNums.remove(0);
        ArrayList<String> list = new ArrayList<>();
        for (Integer number : departmentNums) {
            list.add(departmentNames.get(number));
        }
        return list;
    }
    
    public Object[] getDepartmentNumbers(){
        return departmentNums.toArray();
    }
    
    Boolean refresh(String modifiable, String modified) throws OwnException{
        Boolean success = false;
        String SQL = "update departments "
                +"set department_name = ? "
                +"where department_id = ?";
        open();
        try {
            PreparedStatement request = connection.prepareStatement(SQL);
            request.setString(1, modified);
            request.setString(2
                    , modifiable);
            if (request.executeUpdate()!=1) {
                throw new OwnException("felülírás hiba"); 
            }else {
                success = true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        close();
        return success;
    }
}
