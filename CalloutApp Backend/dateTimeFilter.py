import datetime
from dataExtractor import monthAbbreviations

def __findMonthNumber(month_string):
    return int(monthAbbreviations.index(month_string) / 3) + 1


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
    while not date_in_string[-1].isdigit():
        date_in_string = date_in_string[:-1]
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
        return str(datetime.date(year=int(year), month=month, day=int(day)).isoformat())
    #No spaces in the string
    elif len(date_list) == 1:
        #We have to put the leading zeros.
        #05-06-2019 
        #5-6-2019
        #10-06-2019
        delim = None
        first_num_is_year = False
        if not date_in_string[1].isdigit():
            delim = date_in_string[1]
        elif not date_in_string[2].isdigit():
            delim = date_in_string[2]
        #Year is first. Assume 4 digits. 
        else:
            first_num_is_year = True
            delim = date_in_string[4]
        date_list = date_in_string.split(delim, 3)
        if len(date_list) == 3 and first_num_is_year:
            return str(datetime.date(year=int(date_list[0]), month=int(date_list[1]), day=int(date_list[2])).isoformat())
        elif len(date_list) == 3:
            return str(datetime.date(year=int(date_list[2]), month=int(date_list[0]), day=int(date_list[1])).isoformat())
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
        pass

        


