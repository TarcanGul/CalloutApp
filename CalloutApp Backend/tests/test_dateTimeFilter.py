import unittest
import sys
sys.path.append('..')
from dateTimeFilter import convertDate

class TestConvertDate(unittest.TestCase):
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

      