import unittest
import sys
sys.path.append('..')
from dateTimeFilter import convertDate, convertTime, turnToDateTime

class TestConvertDateTime(unittest.TestCase):
  def test_date_regular(self): 
      self.assertEqual("2019-05-10", convertDate("05-10-2019"))
      self.assertEqual("2020-06-27", convertDate("6-27-2020"))
  def test_date_regular_without_year(self):
      self.assertEqual("2019-07-07", convertDate("7-7"))
      self.assertEqual("2019-08-07", convertDate("8.7."))
  def test_date_year_first(self):
      self.assertEqual("2019-05-04", convertDate("2019-5-4"))
  def test_diff_delimeters(self):
      self.assertEqual("2020-06-27", convertDate("6.27.2020"))
      self.assertEqual("2020-06-27", convertDate("2020/6/27"))
      self.assertEqual("2020-06-27", convertDate("06'27'2020"))
  def test_date_with_month_name(self):
      self.assertEqual("2021-05-07", convertDate("May 7, 2021"))
      self.assertEqual("2020-06-06", convertDate("JUN. 6 2020"))
  def test_date_with_month_name_but_without_year(self):
      self.assertEqual("2019-05-07", convertDate("May 7"))
      self.assertEqual("2020-03-07", convertDate("March 7"))
  def test_date_with_useless_punc(self):
      self.assertEqual("2019-05-07", convertDate("May 7!"))
      self.assertEqual("2019-05-07", convertDate("May 7!!"))
      self.assertEqual("2021-05-07", convertDate("May 7, 2021!!!!!"))
  def test_date_exception_ValueError(self):
      self.assertRaises(ValueError, convertDate, "332")
      self.assertRaises(ValueError, convertDate, "May")
      self.assertRaises(ValueError, convertDate, "")
  def test_time_special_words(self):
      self.assertEqual("12:00:00", convertTime("noon"))
      self.assertEqual("00:00:00", convertTime("midnight"))
  def test_time_regular_with_words(self):
      self.assertEqual("13:00:00", convertTime("1 pm"))
      self.assertEqual("05:30:00", convertTime("5.30 am"))
      self.assertEqual("15:10:00", convertTime("3:10 pm"))
      self.assertEqual("12:00:00", convertTime("12 pm"))
  def test_time_regular(self):
      self.assertEqual("11:59:00", convertTime("11.59"))
      self.assertEqual("17:25:00", convertTime("17:25"))
  def test_time_exceptions(self):
      self.assertRaises(ValueError, convertTime, "1232")
      self.assertRaises(ValueError, convertTime, "28:32")
      self.assertRaises(ValueError, convertTime, "clocks")
  def test_datetime(self):
      self.assertEqual("2019-05-07T15:30:00", turnToDateTime("May 7", "3.30 pm"))

      


      