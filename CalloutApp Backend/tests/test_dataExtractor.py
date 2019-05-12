import unittest
import pytesseract
import os
from PIL import Image

TEST_IMAGES_FOLDER = os.path.dirname(os.real.realpath(__file__) + "\\test_images")

#This method is for testing purposes and strengthening the dataExtractor. 
def printList(test_image):
    parsedText = pytesseract.image_to_string(Image.open(os.path.join(TEST_IMAGES_FOLDER,test_image)))
    parsedList = parsedText.split()
    return str(parsedList)

class test_dataExtractor_date(unittest.TestCase):
    def test_1(self):
        pass