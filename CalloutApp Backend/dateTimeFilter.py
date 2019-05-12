import datetime
from dataExtractor import monthAbbreviations

def __findMonthNumber(month_string):
    first_three_letters = month_string[0:3]
    if first_three_letters == 'jan':
        return 1
    elif first_three_letters == 'feb':
        return 2
    elif first_three_letters == 'mar':
        return 3
    elif first_three_letters == 'apr':
        return 4
    elif first_three_letters == 'may':
        return 5
    elif first_three_letters == 'jun':
        return 6
    elif first_three_letters == 'jul':
        return 7
    elif first_three_letters == 'aug':
        return 8
    elif first_three_letters == 'sep':
        return 9
    elif first_three_letters == 'oct':
        return 10
    elif first_three_letters == 'nov':
        return 11
    elif first_three_letters == 'dec':
        return 12
    else:
        return None

def __hasPunc(time_string):
    punc = ['.', '-', ':']
    for c in time_string:
        if c in punc:
            return c
    return None

def convertDate(date_in_string):
    #Should return a format
    #Possible formats
    '''
    May 6
    May 6 2019
    05-06-2019
    5-6-2019
    May 6
    05-06
    '''
    #Catching useless punctuation at the end.
    if date_in_string == None: 
        raise ValueError("Date is None.")
    if date_in_string == '':
        raise ValueError("Date is empty.")
    while not date_in_string[-1].isdigit():
        date_in_string = date_in_string[:-1]
        if date_in_string == '':
            raise ValueError("Date format is not recognized: There are only punctuations.")
    #If first one is a month abbreviation, handle it.
    date_list = date_in_string.split(' ', 3)
    if len(date_list) == 3 and date_list[0].lower() in monthAbbreviations:
        #May 16, 2019
        month_string = date_list[0].lower()
        month = __findMonthNumber(month_string)
        #Remove comma if exists.
        if date_list[1][-1] == ',':
            date_list[1] = date_list[1][:-1]
        day = date_list[1]
        year = date_list[2]
        return str(datetime.date(year=int(year), month=month, day=int(day)))
    #No spaces in the string
    elif len(date_list) == 1:
        #We have to put the leading zeros.
        #05-06-2019 
        #5-6-2019
        #10-06-2019
        delim = None
        first_num_is_year = False
        if not date_in_string[1].isdigit() and len(date_in_string) > 0:
            delim = date_in_string[1]
        elif not date_in_string[2].isdigit() and len(date_in_string) > 1:
            delim = date_in_string[2]
        #Year is first. Assume 4 digits. 
        elif len(date_in_string) > 4:
            first_num_is_year = True
            delim = date_in_string[4]
        else:
            raise ValueError("Date value not recognized, expected delimeters.")
        date_list = date_in_string.split(delim, 3)
        if len(date_list) == 3 and first_num_is_year:
            return str(datetime.date(year=int(date_list[0]), month=int(date_list[1]), day=int(date_list[2])))
        elif len(date_list) == 3:
            return str(datetime.date(year=int(date_list[2]), month=int(date_list[0]), day=int(date_list[1])))
        #No year is specified. 
        elif len(date_list) == 2:
            current_time = datetime.datetime.now()
            year = None
            month = int(date_list[0])
            if not date_list[1][-1].isdigit():
                date_list[1] = date_list[1][:-1]
            day = int(date_list[1])
            if month < current_time.month:
                year = int(current_time.year) + 1
            else:
                year = int(current_time.year)
            return str(datetime.date(year = year, month = month, day = day))
        else:
            return ValueError("Date format cannot be recognized.")
    #No year is specified. Assumption is that the first entry is month
    elif len(date_list) == 2 and date_list[0].lower() in monthAbbreviations:
        month = __findMonthNumber(date_list[0].lower())
        if not date_list[1][-1].isdigit():
            date_list[1] = date_list[1][:-1]
        day = int(date_list[1])
        current_time = datetime.datetime.now()
        year = None
        if month < current_time.month:
            year = int(current_time.year) + 1
        else:
            year = int(current_time.year)
        return str(datetime.date(year = year, month = month, day = day))
    else: 
        raise ValueError("The input date format cannot be recognized.")


def convertTime(time_string):
  #second will be always zero
  time_string = time_string.strip()
  time_words = ['am', 'pm']
  '''
  Possible formats:
  3 pm, am
  15:00
  noon
  3:10 pm
  midnight
  '''
  if time_string == 'noon':
      return str(datetime.time(hour = 12, minute= 0))
  elif time_string == 'midnight':
      return str(datetime.time(0,0))
  else:
      time_list = time_string.split(' ', 2)
      #This means this is '3.00 pm' (punc can change) or '3 pm' format.
      if len(time_list) == 2:
          if time_list[1] in time_words:
              written_time = time_list[0]
              minute = 0
              hour = None
              #Check if 3.00 form or 3 form  
              #If written time has any of the punctuation.
              possible_delim = __hasPunc(time_list[0])
              if possible_delim:
                  extracted_time = time_list[0].split(possible_delim)
                  hour = int(extracted_time[0])
                  minute = int(extracted_time[1])
              else:
                  hour = int(written_time)
              if time_list[1] == 'pm':
                  if hour != 12: 
                      hour += 12
              return str(datetime.time(hour, minute))
      elif len(time_list) == 1:
            #15.00 or 15:00 format
            #Now using time_string since time_list has 1 element anyway
            potential_delim = __hasPunc(time_string)
            hour = None
            minute = None
            if potential_delim:
                time_list = time_string.split(potential_delim, 2)
                hour = int(time_list[0])
                minute = int(time_list[1])
            else:
                raise ValueError("Time format cannot be recognized: No spaces thus expected a delimeter")
            if hour in range(24) and minute in range(60):
                return str(datetime.time(hour, minute))
            else:
                raise ValueError("Time format cannot be recognized: Hour or minute is too big.")
      else:
          raise ValueError("Time format cannot be recognized.")

def turnToDateTime(date_string, time_string):
    return convertDate(date_string) + "T" + convertTime(time_string)

              
                 


