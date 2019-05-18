import unittest
import pytesseract
import os
import sys
sys.path.append(os.path.join(os.path.dirname(sys.path[0]),'utils'))
from utils.dateTimeFilter import convertDate, convertTime
from utils.dataExtractor import InfoExtractor
from PIL import Image

TEST_IMAGES_FOLDER = os.getcwd() + "\\tests\\test_images"


class test_dataExtractor(unittest.TestCase):
    def test_1(self):
        wordsList = printList("test_1.jpg")
        infoExtractor = InfoExtractor()
        infoExtractor.extractWords(wordsList)
        self.assertEqual("2020-03-23", convertDate(infoExtractor.getDate()))
        self.assertEqual("18:00:00", convertTime(infoExtractor.getTime()))
        self.assertEqual("22:00:00", convertTime(infoExtractor.getEndTime()))
        self.assertEqual("Panda Express", infoExtractor.getLocations().pop())
    def test_2(self):
        wordsList = printList("test_2.png")
        infoExtractor = InfoExtractor()
        infoExtractor.extractWords(wordsList)
        self.assertEqual("2019-08-31", convertDate(infoExtractor.getDate()))
        self.assertEqual("19:00:00", convertTime(infoExtractor.getTime()))
        self.assertEqual("22:00:00", convertTime(infoExtractor.getEndTime()))
        self.assertEqual("BRNG B247", infoExtractor.getLocations().pop())





#This method is for testing purposes and strengthening the dataExtractor. 
def printList(test_image):
    parsedText = pytesseract.image_to_string(Image.open(os.path.join(TEST_IMAGES_FOLDER,test_image)))
    parsedList = parsedText.split()
    return parsedList
