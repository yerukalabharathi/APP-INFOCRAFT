import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

public class frontend {
    private JFrame mainFrame;
    private JPanel mainPanel;
    private JLabel titleLabel;
    private JLabel instructionLabel;
    private JButton summaryButton;
    private JButton imageButton;
    private JButton tabularDataButton;
    private JButton graphicalDataButton;
 // Added graphical data analysis button

    public frontend() {
        mainFrame = new JFrame("Infocraft");
        mainPanel = new JPanel();
        titleLabel = new JLabel("<html><u><b>Welcome to Infocraft!</b></u></html>");
        Font titleFont = new Font(titleLabel.getFont().getName(), Font.PLAIN, 24);
        titleLabel.setFont(titleFont);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        instructionLabel = new JLabel("Click an option to proceed");
        Font instructionFont = new Font(instructionLabel.getFont().getName(), Font.PLAIN, 18);
        instructionLabel.setFont(instructionFont);
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        summaryButton = new JButton("Summary Writing");
        imageButton = new JButton("Reading and Recognizing Images");
        tabularDataButton = new JButton("Reading Tabular Data");
        graphicalDataButton = new JButton("Analyze Graphical Data"); // Added graphical data button

        summaryButton.addActionListener(e -> performTextSummarization());
        imageButton.addActionListener(e -> performImageRecognition());
        tabularDataButton.addActionListener(e -> performTabularDataProcessing());
        graphicalDataButton.addActionListener(e -> performGraphicalDataAnalysis()); // Added action for graphical data analysis

        mainPanel.setLayout(new GridLayout(6, 1));
        mainPanel.add(titleLabel);
        mainPanel.add(instructionLabel);
        mainPanel.add(summaryButton);
        mainPanel.add(imageButton);
        mainPanel.add(tabularDataButton);
        mainPanel.add(graphicalDataButton); // Added the graphical data button to the main panel

        mainFrame.add(mainPanel);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(400, 350);
        mainFrame.setVisible(true);
    }

    private JLabel createTitleLabel() {
        JLabel titleLabel = new JLabel("<html><u><b>Welcome to Infocraft!</b></u></html>");
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.PLAIN, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        return titleLabel;
    }

    private JLabel createInstructionLabel() {
        JLabel instructionLabel = new JLabel("Click an option to proceed");
        instructionLabel.setFont(new Font(instructionLabel.getFont().getName(), Font.PLAIN, 18));
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        return instructionLabel;
    }

    private JButton createOptionButton(String label, Runnable action) {
        JButton button = new JButton(label);
        button.addActionListener(e -> action.run());
        return button;
    }

    private void performTextSummarization() {
        String inputText = JOptionPane.showInputDialog(mainFrame, "Enter the text to summarize");
        if (inputText != null) {
            PrecisModule precisModule = new PrecisModule();
            String summary = precisModule.generateSummary(inputText, 3); // Adjust summary length as needed
            displayResult("Summary", summary);
        }
    }

    private void performImageRecognition() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(mainFrame);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            String imagePath = fileChooser.getSelectedFile().getAbsolutePath();
            try {
                String recognizedText = ImageRecognitionModule.extractTextFromImage(imagePath);
                displayResult("Image Text", recognizedText);
            } catch (IOException ex) {
                displayResult("Image Recognition Error", "Error: " + ex.getMessage());
            }
        }
    }


    private void performTabularDataProcessing() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(mainFrame);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();

            // Call the TabularDataModule for processing
            TabularDataModule tabularDataModule = new TabularDataModule();
            String tableData = tabularDataModule.processTabularData(filePath);

            displayResult("Tabular Data", tableData);
        }
    }

    private void performGraphicalDataAnalysis() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(mainFrame);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            String imagePath = fileChooser.getSelectedFile().getAbsolutePath();
            try {
                BufferedImage graphImage = ImageIO.read(new File(imagePath));
                GraphicalDataModule graphModule = new GraphicalDataModule();
                String graphAnalysisResult = graphModule.analyzeGraph(graphImage);
                displayResult("Graphical Data Analysis", graphAnalysisResult);
            } catch (IOException ex) {
                displayResult("Graphical Data Analysis Error", "Error: " + ex.getMessage());
            }
        }
    }

    private void displayResult(String title, String result) {
        JTextArea resultArea = new JTextArea(10, 40);
        resultArea.setText(result);
        resultArea.setWrapStyleWord(true);
        resultArea.setLineWrap(true);
        resultArea.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(resultArea);
        JOptionPane.showMessageDialog(mainFrame, scrollPane, title, JOptionPane.PLAIN_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new frontend());
    }
}
