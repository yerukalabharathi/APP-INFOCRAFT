import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

public class ImageRecognitionModule {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Image Recognition Module");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        JTextArea outputTextArea = new JTextArea(10, 40);
        JButton recognizeImageButton = new JButton("Select and Recognize Image");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(recognizeImageButton);

        frame.add(outputTextArea, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        recognizeImageButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "png", "jpg", "jpeg");
                    fileChooser.setFileFilter(filter);

                    int returnValue = fileChooser.showOpenDialog(null);

                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        String recognizedText = extractTextFromImage(selectedFile.getPath());
                        outputTextArea.setText(recognizedText);
                    }
                } catch (IOException ex) {
                    outputTextArea.setText("Error: " + ex.getMessage());
                }
            }
        });

        frame.setVisible(true);
    }

    public static String extractTextFromImage(String imagePath) throws IOException {
        try {
            String apiKey = "YOUR_API_KEY"; // Replace with your OCR.space API key
            String apiEndpoint = "https://api.ocr.space/parse/image";

            URL url = new URL(apiEndpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("apiKey", apiKey);
            connection.setRequestProperty("Content-Type", "multipart/form-data");

            // Enable output for the connection
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream();
                 FileInputStream fis = new FileInputStream(imagePath)) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                JSONObject jsonResponse = new JSONObject(response.toString());
                String recognizedText = jsonResponse.getJSONObject("ParsedResults")
                        .getJSONArray("ParsedText").getJSONObject(0).getString("Text");

                return recognizedText;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
