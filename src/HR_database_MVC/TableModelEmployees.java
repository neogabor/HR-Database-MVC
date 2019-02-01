package HR_database_MVC;

import javax.swing.table.DefaultTableModel;

public class TableModelEmployees extends DefaultTableModel{

    public TableModelEmployees(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if((columnIndex >= 2 && columnIndex  <= 6) || columnIndex == 8){
            return String.class;
        }
        else{
            return Integer.class;
        }
    }
    
}
