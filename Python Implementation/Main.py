import tkinter as tk
from tkinter import filedialog
from tkinter import messagebox
from PIL import Image
import cv2
import numpy as np
from sentence_transformers import SentenceTransformer
from torch import cosine_similarity
import pandas as pd
import matplotlib.pyplot as plt

# Initialize the Sentence Embedding model
embedder = SentenceTransformer('paraphrase-MiniLM-L6-v2')

# Function to perform color-based image recognition
def recognize_image():
    file_path = filedialog.askopenfilename()
    if file_path:
        image = Image.open(file_path)
        central_pixel = image.getpixel((image.width // 2, image.height // 2))

        if isinstance(central_pixel, (tuple, list)) and len(central_pixel) == 3:
            red, green, blue = central_pixel
            if red > 200 and green < 100 and blue < 100:
                result_label.config(text="This image contains something predominantly red, possibly a red object.")
            elif red < 100 and green > 200 and blue < 100:
                result_label.config(text="This image contains something predominantly green, possibly a green object.")
            elif red < 100 and green < 100 and blue > 200:
                result_label.config(text="This image contains something predominantly blue, possibly a blue object.")
            elif red > 200 and green > 200 and blue < 100:
                result_label.config(text="This image contains something predominantly yellow, possibly a yellow object.")
            elif red > 200 and green > 100 and blue < 50:
                result_label.config(text="This image contains something predominantly orange, possibly an orange object.")
            else:
                result_label.config(text="Cannot recognize any specific color in this image.")
        else:
            result_label.config(text="Invalid pixel data in the image.")
    else:
        result_label.config(text="No image selected.")

# Function to interpret a simple bar chart from an image
def interpret_bar_chart(image_path):
    image = cv2.imread(image_path)
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    _, thresholded = cv2.threshold(gray, 200, 255, cv2.THRESH_BINARY_INV)
    contours, _ = cv2.findContours(thresholded, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    bar_heights = [cv2.boundingRect(contour)[3] for contour in contours]
    max_height_index = np.argmax(bar_heights)
    tallest_bar_height = bar_heights[max_height_index]
    return tallest_bar_height

# Function to interpret the bar chart
def interpret_bar_chart(values, labels):
    max_value = max(values)
    min_value = min(values)
    max_index = values.index(max_value)
    min_index = values.index(min_value)
    max_label = labels[max_index]
    min_label = labels[min_index]
    return f"Highest bar: {max_label} - {max_value}\nLowest bar: {min_label} - {min_value}"

# Function to get details of the table
def get_table_details(table_data):
    try:
        df = pd.DataFrame(table_data)
        num_rows, num_columns = df.shape
        column_names = df.columns.tolist()
        stats = df.describe().to_dict()
        result = {
            "Number of Rows": num_rows,
            "Number of Columns": num_columns,
            "Column Names": column_names,
            "Statistics": stats
        }
        return result
    except Exception as e:
        return str(e)

# Function to generate a summary from input text
def extract_sentences(input_text):
    pass

def generate_summary(input_text, num_sentences=10, max_words=1000):
    sentences = extract_sentences(input_text)
    sentence_embeddings = embedder.encode(sentences, convert_to_tensor=True)
    similarity_matrix = cosine_similarity(sentence_embeddings, sentence_embeddings)
    sentence_importance = similarity_matrix.sum()
    sorted_indices = np.argsort(sentence_importance, axis=0)[::-1]
    selected_indices = sorted_indices[:num_sentences]
    selected_sentences = [sentences[i] for i in sorted(selected_indices)]
    summary = '. '.join(selected_sentences) + '.'
    return summary

# Create the main window
root = tk.Tk()
root.title("Infocraft")

# Initialize widgets
result_label = tk.Label(root, text="", font=("Helvetica", 14))
input_text = tk.Text(root, height=10, width=50)

# Functions for buttons
def perform_text_summarization():
    user_input = input_text.get("1.0", tk.END).strip()
    summary = generate_summary(user_input, num_sentences=3, max_words=300)
    result_label.config(text=summary)

def perform_image_recognition():
    recognize_image()

def perform_tabular_data_reading():
    tabular_data = input_text.get("1.0", tk.END).strip().split('\n')
    table_details = get_table_details(tabular_data)
    result_label.config(text=str(table_details))

def perform_graphical_data_analysis():
    image_path = filedialog.askopenfilename()
    tallest_bar_height = interpret_bar_chart(image_path)
    result = f"Tallest bar in the chart has a height of {tallest_bar_height} pixels."
    result_label.config(text=result)
# Create buttons for different options
summary_button = tk.Button(root, text="Summary Writing", command=perform_text_summarization)
image_button = tk.Button(root, text="Reading and Recognizing Images", command=perform_image_recognition)
tabular_data_button = tk.Button(root, text="Reading Tabular Data", command=perform_tabular_data_reading)
graphical_data_button = tk.Button(root, text="Analyze Graphical Data", command=perform_graphical_data_analysis)

# Layout widgets
input_text.pack(padx=10, pady=10)
summary_button.pack()
image_button.pack()
tabular_data_button.pack()
graphical_data_button.pack()
result_label.pack()

# Run the Tkinter main loop
root.mainloop()


