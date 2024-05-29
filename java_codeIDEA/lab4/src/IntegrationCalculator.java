import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.LinkedList;
import java.util.function.Function;

public class IntegrationCalculator{

    private static DefaultTableModel tableModel;
    private static LinkedList<RecIntegral> dataList = new LinkedList<>();

    public static void main(String[] args) {
        JFrame frame = new JFrame("Numerical Integration of sin(x)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        tableModel = new DefaultTableModel();
        tableModel.addColumn("Start");
        tableModel.addColumn("End");
        tableModel.addColumn("h");
        tableModel.addColumn("Result");

        // Заполнение коллекции dataList с предварительно заданными данными
        double start1 = 0.0;
        double end1 = Math.PI / 2;
        double h1 = 0.1;
        double result1 = integralTrapec(start1, end1, h1, a3 -> Math.sin((Double) a3));
        dataList.add(new RecIntegral(start1, end1, h1, result1));

        double start2 = Math.PI / 2;
        double end2 = Math.PI;
        double h2 = 0.1;
        double result2 = integralTrapec(start2, end2, h2, a2 -> Math.sin((Double) a2));
        dataList.add(new RecIntegral(start2, end2, h2, result2));

        double start3 = Math.PI;
        double end3 = 3 * Math.PI / 2;
        double h3 = 0.1;
        double result3 = integralTrapec(start3, end3, h3, a1 -> Math.sin((Double) a1));
        dataList.add(new RecIntegral(start3, end3, h3, result3));

        double start4 = 3 * Math.PI / 2;
        double end4 = 2 * Math.PI;
        double h4 = 0.1;
        double result4 = integralTrapec(start4, end4, h4, a -> Math.sin((Double) a));
        dataList.add(new RecIntegral(start4, end4, h4, result4));

        JTable table = new JTable(tableModel){
            @Override
            public boolean isCellEditable(int i, int i1){
                return false;
            }
        };
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel inputPanel = new JPanel(new GridLayout(9, 3));

        JLabel startLabel = new JLabel("Start:");
        JTextField startField = new JTextField();
        JLabel endLabel = new JLabel("End:");
        JTextField endField = new JTextField();
        JLabel hLabel = new JLabel("h:");
        JTextField hField = new JTextField();
        JButton calculateButton = new JButton("Calculate");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");
        JButton fillButton = new JButton("Fill");
        JButton addAllDataButton = new JButton("Add for link");
        JButton deleteAllDataButton = new JButton("Clear all from link");
        JButton saveButton = new JButton("Save to File");
        JButton loadButton = new JButton("Load from File");
        JButton saveBinaryButton = new JButton("Save Binary");
        JButton loadBinaryButton = new JButton("Load Binary");

        inputPanel.add(startLabel);
        inputPanel.add(startField);
        inputPanel.add(endLabel);
        inputPanel.add(endField);
        inputPanel.add(hLabel);
        inputPanel.add(hField);
        inputPanel.add(calculateButton);
        inputPanel.add(deleteButton);
        inputPanel.add(clearButton);
        inputPanel.add(fillButton);
        inputPanel.add(addAllDataButton);
        inputPanel.add(deleteAllDataButton);
        inputPanel.add(saveButton);
        inputPanel.add(loadButton);
        inputPanel.add(saveBinaryButton);
        inputPanel.add(loadBinaryButton);


        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try
                {
                    double a = Double.parseDouble(startField.getText());
                    double b = Double.parseDouble(endField.getText());
                    double h = Double.parseDouble(hField.getText());
                    if (a > b)
                    {
                        throw new InvalidDataException("Invalid data input. Start more then end");
                    }
                    if (h > b-a)
                    {
                        throw new InvalidDataException("Invalid data input. h shouldn't be more then (end-start)");
                    }
                    if (a < 0.000001 || a > 1000000 || b < 0.000001 || b > 1000000 || h < 0.000001 || h > 1000000)
                    {
                        throw new InvalidDataException("Invalid data input. Values should be between 0.000001 and 1000000.");
                    }
                    double integral = integralTrapec(a, b, h, a1 -> Math.sin((Double) a1));

                    addToTable(a, b, h, integral);
                }catch (InvalidDataException ex) { JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); } } });




        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.removeRow(selectedRow);
                dataList.remove(selectedRow);
            }
        });


        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableModel.setRowCount(0);
            }
        });


        fillButton.addActionListener(e -> {
            for (RecIntegral rec : dataList) {
                tableModel.addRow(new Object[]{rec.getStart(), rec.getEnd(), rec.getH(), rec.getResult()});
            }
        });


        addAllDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try
                {
                    double start = Double.parseDouble(startField.getText());
                    double end = Double.parseDouble(endField.getText());
                    double h = Double.parseDouble(hField.getText());
                    if (start > end)
                    {
                        throw new InvalidDataException("Invalid data input. Start more then end");
                    }
                    if (h > end-start)
                    {
                        throw new InvalidDataException("Invalid data input. h shouldn't be more then (end-start)");
                    }
                    if (start < 0.000001 || start > 1000000 || end < 0.000001 || end > 1000000 || h < 0.000001 || h > 1000000)
                    {
                        throw new InvalidDataException("Invalid data input. Values should be between 0.000001 and 1000000.");
                    }


                    double integral = integralTrapec(start, end, h, a -> Math.sin((Double) a));

                    dataList.add(new RecIntegral(start, end, h, integral));
                }
                catch (InvalidDataException ex) { JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); } } });


        deleteAllDataButton.addActionListener(e -> dataList.clear());

        saveButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showSaveDialog(null);

            if (option == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = fileChooser.getSelectedFile();
                    if (!file.getName().endsWith(".txt")) {
                        file = new File(file.getPath() + ".txt");
                    }
                    FileWriter writer = new FileWriter(file);
                    for (RecIntegral rec : dataList) {
                        writer.write(rec.getStart() + ";" + rec.getEnd() + ";" + rec.getH() + ";" + rec.getResult() + "\n");
                    }
                    writer.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        loadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showOpenDialog(null);

            if (option == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = fileChooser.getSelectedFile();
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String line;

                    // Очищаем текущие данные из dataList
                    dataList.clear();
                    tableModel.setRowCount(0);

                    while ((line = reader.readLine()) != null) {
                        String[] values = line.split(";");
                        double start = Double.parseDouble(values[0]);
                        double end = Double.parseDouble(values[1]);
                        double h = Double.parseDouble(values[2]);
                        double result = Double.parseDouble(values[3]);
                        dataList.add(new RecIntegral(start, end, h, result));
                        tableModel.addRow(new Object[]{start, end, h, result});
                    }
                    reader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        saveBinaryButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showSaveDialog(null);

            if (option == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = fileChooser.getSelectedFile();
                    if (!file.getName().endsWith(".txt")) {
                        file = new File(file.getPath() + ".txt");
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        Double start = (Double) tableModel.getValueAt(i, 0);
                        Double end = (Double) tableModel.getValueAt(i, 1);
                        Double h = (Double) tableModel.getValueAt(i, 2);
                        Double result = (Double) tableModel.getValueAt(i, 3);
                        RecIntegral rec = new RecIntegral(start, end, h, result);
                        objectOutputStream.writeObject(rec);
                    }

                    objectOutputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });



        loadBinaryButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showOpenDialog(null);

            if (option == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = fileChooser.getSelectedFile();
                    FileInputStream fileInputStream = new FileInputStream(file);
                    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

                    dataList.clear();
                    tableModel.setRowCount(0);
                    while (fileInputStream.available() > 0) {
                        RecIntegral rec = (RecIntegral) objectInputStream.readObject();
                        dataList.add(rec);
                        tableModel.addRow(new Object[]{rec.getStart(), rec.getEnd(), rec.getH(), rec.getResult()});
                    }
                    objectInputStream.close();
                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });






        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public static double integralTrapec(double a, double b, double h, Function function) {
        double area = 0;
        for (int i = 0; a + i * h < b; i++) {
            area += h * (0.5 * (double)function.apply(a + i * h) + (double)function.apply(a + (i + 1) * h));
        }
        return area;
    }

    public static void addToTable(double a, double b, double h, double result) {
        tableModel.addRow(new Object[]{a, b, h, result});
        dataList.add(new RecIntegral(a, b, h, result));
    }

}

class RecIntegral implements Serializable {
    private double start;
    private double end;
    private double h;
    private double result;

    public RecIntegral(double start, double end, double h, double result) {
        this.start = start;
        this.end = end;
        this.h = h;
        this.result = result;
    }
    public double getStart() {
        return start;
    }

    public double getEnd() {
        return end;
    }

    public double getH() {
        return h;
    }

    public double getResult() {
        return result;
    }
}

