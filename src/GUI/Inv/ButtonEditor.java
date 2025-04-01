package GUI.Inv;
import java.awt.Component;
import java.util.function.Consumer;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;

import Helper.LoadSound;

class ButtonEditor extends DefaultCellEditor {
    protected JButton button;
    private String label;
    private boolean isPushed;
    private Consumer<Integer> onClick; // Callback pour gérer le clic sur le bouton

    public ButtonEditor(JCheckBox checkBox, Consumer<Integer> onClick) {
        super(checkBox);
        this.onClick = onClick;
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        label = (value == null) ? "Craft" : value.toString();
        button.setText(label);
        isPushed = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed) {
            LoadSound.playSound("button_click");
            onClick.accept(((JTable) button.getParent()).getSelectedRow());
        }
        isPushed = false;
        return label;
    }
}
