import unittest
import pytesseract
import os
import sys
sys.path.append(os.path.join(os.path.dirname(sys.path[0]),'utils'))
from utils.dateTimeFilter import convertDate, convertTime
from utils.dataExtractor import InfoExtractor
from PIL import Image

TEST_IMAGES_FOLDER = os.getcwd() + "\\tests\\test_images"


class test_dataExtractor_date(unittest.TestCase):
    def setUp(self):
        self.logging_file = open("tests/ParsedText.log", "w")

    def test_1(self):
        wordsList = printList("test_1.jpg")
        infoExtractor = InfoExtractor()
        self.logging_file.write(f"Wordlist from test_1 : \n{wordsList}\n")
        infoExtractor.extractWords(wordsList)
        self.assertEqual("2020-03-23", convertDate(infoExtractor.getDate()))
    def test_2(self):
        wordsList = printList("test_2.png")
        infoExtractor = InfoExtractor()
        self.logging_file.write(f"Wordlist from test_2 : \n{wordsList}\n")
        infoExtractor.extractWords(wordsList)
        self.assertEqual("2019-08-31", convertDate(infoExtractor.getDate()))
    def test_3(self):
        wordsList = printList("test_3.jpg")
        infoExtractor = InfoExtractor()
        self.logging_file.write(f"Wordlist from test_3 : \n{wordsList}\n")
        infoExtractor.extractWords(wordsList)
        self.assertEqual("2019-08-31", convertDate(infoExtractor.getDate()))
    def tearDown(self):
        self.logging_file.close()

class test_dataExtractor_start_time(unittest.TestCase):
    def setUp(self):
        self.logging_file = open("tests/ParsedText.log", "w")

    def test_1(self):
        wordsList = printList("test_1.jpg")
        infoExtractor = InfoExtractor()
        self.logging_file.write(f"Wordlist from test_1 : \n{wordsList}\n")
        infoExtractor.extractWords(wordsList)
        self.assertEqual("18:00:00", convertTime(infoExtractor.getTime()))
    def test_2(self):
        wordsList = printList("test_2.png")
        infoExtractor = InfoExtractor()
        self.logging_file.write(f"Wordlist from test_2 : \n{wordsList}\n")
        infoExtractor.extractWords(wordsList)
        self.assertEqual("19:00:00", convertTime(infoExtractor.getTime()))
    def test_3(self):
        wordsList = printList("test_3.jpg")
        infoExtractor = InfoExtractor()
        self.logging_file.write(f"Wordlist from test_3 : \n{wordsList}\n")
        infoExtractor.extractWords(wordsList)
        self.assertEqual("18:00:00", convertTime(infoExtractor.getTime()))
    def tearDown(self):
        self.logging_file.close()

class test_dataExtractor_end_time(unittest.TestCase):
    def setUp(self):
        self.logging_file = open("tests/ParsedText.log", "w")

    def test_1(self):
        wordsList = printList("test_1.jpg")
        infoExtractor = InfoExtractor()
        self.logging_file.write(f"Wordlist from test_1 : \n{wordsList}\n")
        infoExtractor.extractWords(wordsList)
        self.assertEqual("22:00:00", convertTime(infoExtractor.getEndTime()))
    def test_2(self):
        wordsList = printList("test_2.png")
        infoExtractor = InfoExtractor()
        self.logging_file.write(f"Wordlist from test_2 : \n{wordsList}\n")
        infoExtractor.extractWords(wordsList)
        self.assertEqual("22:00:00", convertTime(infoExtractor.getEndTime()))
    def test_3(self):
        wordsList = printList("test_3.jpg")
        infoExtractor = InfoExtractor()
        self.logging_file.write(f"Wordlist from test_3 : \n{wordsList}\n")
        infoExtractor.extractWords(wordsList)
        self.assertRaises(ValueError, infoExtractor.getEndTime)
    def tearDown(self):
        self.logging_file.close()

class test_dataExtractor_location(unittest.TestCase):
    def setUp(self):
        self.logging_file = open("tests/ParsedText.log", "w")

    def test_1(self):
        wordsList = printList("test_1.jpg")
        infoExtractor = InfoExtractor()
        self.logging_file.write(f"Wordlist from test_1 : \n{wordsList}\n")
        infoExtractor.extractWords(wordsList)
        self.assertEqual("Panda Express", infoExtractor.getLocations().pop())
    def test_2(self):
        wordsList = printList("test_2.png")
        infoExtractor = InfoExtractor()
        self.logging_file.write(f"Wordlist from test_2 : \n{wordsList}\n")
        infoExtractor.extractWords(wordsList)
        self.assertEqual("BRNG B247", infoExtractor.getLocations().pop())
    def test_3(self):
        wordsList = printList("test_3.jpg")
        infoExtractor = InfoExtractor()
        self.logging_file.write(f"Wordlist from test_3 : \n{wordsList}\n")
        infoExtractor.extractWords(wordsList)
        self.assertEqual("Matthew 210", infoExtractor.getLocations().pop())
    def tearDown(self):
        self.logging_file.close()




#This method is for testing purposes and strengthening the dataExtractor. 
def printList(test_image):
    parsedText = pytesseract.image_to_string(Image.open(os.path.join(TEST_IMAGES_FOLDER,test_image)))
    parsedList = parsedText.split()
    return parsedList
