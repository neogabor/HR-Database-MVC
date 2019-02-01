package HR_database_MVC;

import javax.swing.table.DefaultTableModel;

public class TableModelDepartment extends DefaultTableModel{

    public TableModelDepartment(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if(columnIndex == 1){
            return String.class;
        }
        else{
            return Integer.class;
        }
    }
    
}
