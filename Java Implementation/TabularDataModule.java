import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class TabularDataModule {

    private JTextArea textArea;
    private JFileChooser fileChooser;
    private StringBuilder tableData = new StringBuilder();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TabularDataModule::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        TabularDataModule tabularDataModule = new TabularDataModule();
        tabularDataModule.setupUI();
    }

    private void setupUI() {
        JFrame frame = new JFrame("Tabular Data Processing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem openMenuItem = new JMenuItem("Open CSV File");
        JMenuItem clearMenuItem = new JMenuItem("Clear Text Area");

        openMenuItem.addActionListener(e -> openFile());
        clearMenuItem.addActionListener(e -> clearTextArea());

        fileMenu.add(openMenuItem);
        fileMenu.add(clearMenuItem);
        menuBar.add(fileMenu);

        frame.setJMenuBar(menuBar);

        textArea = new JTextArea(20, 40);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);

        JButton processButton = new JButton("Process Table");
        JButton saveButton = new JButton("Save Table");

        String filePath = null;
        processButton.addActionListener(e -> processTabularData(filePath));
        saveButton.addActionListener(e -> saveTable());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(processButton);
        buttonPanel.add(saveButton);

        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));

        frame.setVisible(true);
    }

    private void openFile() {
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();
            tableData = new StringBuilder(); // Reset the table data
            String tableContent = readCSV(filePath);
            textArea.setText(tableContent);
        }
    }

    private void clearTextArea() {
        textArea.setText("");
        tableData = new StringBuilder(); // Clear the table data
    }

    public String processTabularData(String filePath) {
        String text = textArea.getText();
        // Example: Calculate the sum of all numbers in the table (assuming numeric data in the CSV)
        double sum = 0;
        String[] lines = text.split("\n");
        for (String line : lines) {
            String[] values = line.split(",");
            for (String value : values) {
                try {
                    double num = Double.parseDouble(value.trim());
                    sum += num;
                } catch (NumberFormatException ignored) {
                    // Ignore non-numeric values
                }
            }
        }
        textArea.append("\nSum of numeric values: " + sum);
        return text;
    }

    private String readCSV(String filePath) {
        tableData = new StringBuilder(); // Reset the table data
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = br.readLine()) != null) {
                tableData.append(line).append("\n");
            }
            br.close(); // Close the BufferedReader
        } catch (FileNotFoundException e) {
            showError("File not found.");
        } catch (IOException e) {
            showError("Error reading the CSV file.");
        }
        return tableData.toString();
    }

    private void saveTable() {
        int returnValue = fileChooser.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(selectedFile)) {
                writer.print(tableData.toString()); // Save the table data
                showInfo("Table data saved successfully.");
            } catch (IOException e) {
                showError("Error saving the table data.");
            }
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(null, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
}
