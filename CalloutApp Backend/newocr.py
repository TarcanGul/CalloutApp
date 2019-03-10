from PIL import Image
import pytesseract 
import os
from dataExtractor import InfoExtractor

'''
This is a potential replacement for ocr.py.
'''

#Getting the path of the file so we can open an image file in the directory.
dir_path = os.path.dirname(os.path.realpath(__file__))

#This is where the file input will enter.
parsedText = pytesseract.image_to_string(Image.open(os.path.join(dir_path,'PandaFlyer.jpg')))

#From the parsedText, we have to get the original words
parsedList = parsedText.split()

#Creating extractor object
extractor = InfoExtractor()
extractor.extractWords(parsedList)

#Print the date(might be inaccurate)
print(extractor.getDate())
#print(extractor.getTime())