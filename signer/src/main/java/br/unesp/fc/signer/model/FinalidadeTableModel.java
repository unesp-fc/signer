package br.unesp.fc.signer.model;

import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class FinalidadeTableModel extends AbstractTableModel {

    private List<Finalidade> list;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public void setList(List<Finalidade> list) {
        this.list = list;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Nome";
            case 1:
                return "Data";
        }
        return "";
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        var finalidade = list.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return finalidade.getNome();
            case 1:
                return sdf.format(finalidade.getData());
        }
        return "";
    }

    public Finalidade get(int rowIndex) {
        return list.get(rowIndex);
    }

}
