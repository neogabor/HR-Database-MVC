package HR_database_MVC;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;


public class View extends JFrame implements ActionListener{
    
    private JTable tBase;
    private DefaultTableModel tmDepartment;
    private DefaultTableModel tmEmployees;
    private JScrollPane spTableBase = new JScrollPane();
    private JLabel lbDepartments = new JLabel("Részlegek");
    private JLabel lbChangeTable = new JLabel("Melyik Tábla?");
    private JComboBox cbDepartmentFilter;
    private JComboBox cbTables;
    private JButton btDepartmentMod;
    private JButton btRefresh;
    private JPanel pn1 = new JPanel();
    private JDialog dModify;
    private JTextField tfInsert;
    private JComboBox cbOverwrite;
    
    private Model m = null;
    Object[][] dataDepartments;
    Object[][] dataEmployees;
    Object[] columnNameEmployees;
    Object[] columnNameDepartments;
    private String all = new String("Ősszes");
    private String[] tables = {"Dolgozók", "Részlegek"};
    private TableRowSorter<DefaultTableModel> Filter;
    
    public View(Model m){
        this.m=m;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setTitle("Vizsga");
        setLocationRelativeTo(this);
        setResizable(true);
        setLayout(new GridLayout(2,1));
        
        try{
            columnNameEmployees = m.columnNames("EMPLOYEES");
            dataEmployees = m.data("EMPLOYEES",columnNameEmployees.length);
            columnNameDepartments = m.columnNames("DEPARTMENTS");
            dataDepartments = m.data("DEPARTMENTS",columnNameDepartments.length);
        } catch(OwnException ex){
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
        tmEmployees = new TableModelEmployees(dataEmployees, columnNameEmployees);
        tBase = new JTable(tmEmployees);
        tBase.setAutoCreateRowSorter(true);
        tBase.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (tBase.getSelectedRow()==0) {
                    tBase.getRowSorter().toggleSortOrder(tBase.getSelectedColumn());
                }
            }
        });
        pn1.add(lbDepartments);
        cbDepartmentFilter = new JComboBox(sortedDepartmentNames(m));
        cbDepartmentFilter.setSelectedItem(all);
        cbDepartmentFilter.addActionListener(this);
        pn1.add(cbDepartmentFilter);
        pn1.add(lbChangeTable);
        cbTables = new JComboBox(tables);
        cbTables.setSelectedItem(tables[0]);
        pn1.add(cbTables);
        btDepartmentMod = new JButton("Részlegek módosítása");
        btDepartmentMod.addActionListener(this);
        pn1.add(btDepartmentMod);
        btRefresh = new JButton("Frissítés");
        btRefresh.addActionListener(this);
        pn1.add(btRefresh);
        add(pn1,SwingConstants.CENTER);
        spTableBase.setViewportView(tBase);
        add(spTableBase);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==btRefresh){
            refresh();
        }
        else if(e.getSource()==btDepartmentMod){
            dModify = new JDialog(this, "részleg módosítás", true);
            dModify.setSize(200, 200);
            dModify.setLocationRelativeTo(this);
            dModify.setLayout(new FlowLayout());
            tfInsert = new JTextField();
            tfInsert.setColumns(10);
            tfInsert.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent ke) {
                    if (ke.getKeyChar()==KeyEvent.VK_ENTER) {
                        boolean newDepartment = true;
                        if (tfInsert.getText()!="" ) {
                            for (int i = 0; i < cbDepartmentFilter.getItemCount(); i++) {
                                if (tfInsert.getText().equals(cbDepartmentFilter.getItemAt(i))) {
                                    newDepartment = false;
                                }
                            }
                            if (newDepartment) {
                                try {
                                    boolean success = m.refresh(cbOverwrite.getSelectedItem().toString(), tfInsert.getText());
                                    if (success) {
                                        JOptionPane.showMessageDialog(dModify, "sikeres módosítás");
                                    }
                                } catch (OwnException ex) {
                                    JOptionPane.showMessageDialog(dModify, ex.getMessage());
                                }
                            }
                        }
                    }
                }
                
            });
            dModify.add(new JLabel("bevitel utan Enter"));
            dModify.add(tfInsert);
            cbOverwrite = new JComboBox(m.getDepartmentNumbers());
            dModify.add(cbOverwrite);
            dModify.setVisible(true);
        }
        else if(e.getSource() == cbDepartmentFilter ){
            String filterText = cbDepartmentFilter.getSelectedItem().toString();
            String filterId = "";
            for (int i = 0; i < dataDepartments.length
                    && filterId.equals(""); i++) {
                for (int j = 0; j < columnNameDepartments.length; j++) {
                    if (dataDepartments[i][j] != null && dataDepartments[i][j].toString().equals(filterText)) {
                        filterId = dataDepartments[i][j-1].toString();
                    }
                }
            }
            
            if(!filterId.equals("")){
                int columnDepartment = 10;
                ArrayList<Object> filteredData = new ArrayList<>();
                for (int i = 0; i < dataEmployees.length; i++) {
                    if (dataEmployees[i][columnDepartment] != null 
                            && dataEmployees[i][columnDepartment].toString().equals(filterId)) {
                        for (int k = 0; k < columnNameEmployees.length; k++) {
                            filteredData.add(dataEmployees[i][k]);
                        }
                    }
                }
                Object[][] newDataModel = new Object[filteredData.size()/columnNameEmployees.length][columnNameEmployees.length];
                int k=0;
                for (int i = 0; i < filteredData.size()/columnNameEmployees.length; i++) {
                    for (int j = 0; j < columnNameEmployees.length; j++) {
                        newDataModel[i][j] = filteredData.get(k);
                        k++;
                    }
                }
                tmEmployees.setDataVector(newDataModel, columnNameEmployees);
            }
            else{
                tmEmployees.setDataVector(dataEmployees, columnNameEmployees);
            }
        }
    }

    private String[] sortedDepartmentNames(Model m) {
        ArrayList<String> departments = m.getDepartments();
        departments.add(all);
        Collections.sort(departments, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                Collator huCollator = Collator.getInstance(Locale.getDefault());
                return huCollator.compare(o1, o2);
            }
        });
        return departments.toArray(new String[departments.size()]);
    }

    private void refresh(){
        try{
            columnNameEmployees = m.columnNames("EMPLOYEES");
            dataEmployees = m.data("EMPLOYEES",columnNameEmployees.length);
            columnNameDepartments = m.columnNames("DEPARTMENTS");
            dataDepartments = m.data("DEPARTMENTS",columnNameDepartments.length);
        } catch(OwnException ex){
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
        tmEmployees = new TableModelEmployees(dataEmployees, columnNameEmployees);
        tmDepartment = new TableModelDepartment(dataDepartments, columnNameDepartments);
        if(cbTables.getSelectedItem().equals(tables[0])) {
            tBase.setModel(tmEmployees);
            lbDepartments.setVisible(true);
            cbDepartmentFilter.setVisible(true);
        } else if (cbTables.getSelectedItem().equals(tables[1])) {
            tBase.setModel(tmDepartment);
            lbDepartments.setVisible(false);
            cbDepartmentFilter.setVisible(false);
        }
        cbDepartmentFilter.removeActionListener(this);
        cbDepartmentFilter.removeAllItems();
        String[] cbElements = sortedDepartmentNames(m);
        for (String elem : cbElements) {
            cbDepartmentFilter.addItem(elem);
        }
        cbDepartmentFilter.setSelectedItem(all);
        cbDepartmentFilter.addActionListener(this);
    }
}
