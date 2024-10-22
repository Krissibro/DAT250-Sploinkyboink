import os

def get_kotlin_files_content(directory):
    kotlin_files_content = {}
    
    # Traverse through all files in the given directory
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith(".kt"):  # Change to .kt for Kotlin files
                file_path = os.path.join(root, file)
                with open(file_path, 'r', encoding='utf-8') as f:
                    kotlin_files_content[file] = f.read()
    
    return kotlin_files_content

# Get the directory where the Python script is located (same as Kotlin files)
directory = os.path.dirname(os.path.abspath(__file__))

kotlin_files_content = get_kotlin_files_content(directory)

# Print all file names and their content

# If you want to save all the content to a single file, uncomment this part:
with open(os.path.join(directory, "all_kotlin_files.txt"), "w", encoding="utf-8") as output_file:
    for file_name, content in kotlin_files_content.items():
        output_file.write(f"File: {file_name}\n")
        output_file.write(content)
        output_file.write("\n" + "-" * 80 + "\n")
