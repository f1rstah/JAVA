import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Function;

public class IntegrationCalculator{

    private static DefaultTableModel tableModel;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Numerical Integration of sin(x)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);

        tableModel = new DefaultTableModel();
        tableModel.addColumn("Start");
        tableModel.addColumn("End");
        tableModel.addColumn("h");
        tableModel.addColumn("Result");

        JTable table = new JTable(tableModel){
            @Override
            public boolean isCellEditable(int i, int i1){
                return false;
            }
        };
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel inputPanel = new JPanel(new GridLayout(5, 2));

        JLabel startLabel = new JLabel("Start:");
        JTextField startField = new JTextField();
        JLabel endLabel = new JLabel("End:");
        JTextField endField = new JTextField();
        JLabel hLabel = new JLabel("h:");
        JTextField hField = new JTextField();
        JButton calculateButton = new JButton("Calculate");
        JButton deleteButton = new JButton("Delete");

        inputPanel.add(startLabel);
        inputPanel.add(startField);
        inputPanel.add(endLabel);
        inputPanel.add(endField);
        inputPanel.add(hLabel);
        inputPanel.add(hField);
        inputPanel.add(calculateButton);
        inputPanel.add(deleteButton);

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double a = Double.parseDouble(startField.getText());
                double b = Double.parseDouble(endField.getText());
                double h = Double.parseDouble(hField.getText());
                double integral = integralTrapec(a, b, h, Math::sin);
                addToTable(a, b, h, integral);
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    tableModel.removeRow(selectedRow);
                }
            }
        });

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public static double integralTrapec(double a, double b, double h, Function<Double, Double> function) {
        double area = 0;
        for (int i = 0; a + i * h < b; i++) {
            area += h * (0.5 * (function.apply(a + i * h) + function.apply(a + (i + 1) * h)));
        }
        return area;
    }

    public static void addToTable(double a, double b, double h, double result) {
        tableModel.addRow(new Object[]{a, b, h, result});
    }
}