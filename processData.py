import csv
import sys
import math

if len(sys.argv) != 3:
    print("please enter the input csv file name and the output csv file!")
    exit()

rows = [["bartenders", "tables", "avg_guests", "stddev_guests"]]


with open(sys.argv[1], 'r') as csvfile:
    reader = csv.reader(csvfile, delimiter=',')

    currBartenders = -1
    currGuests = 0
    currTables = 0
    currEntryCount = 0
    currGuestCounts = []

    for row in reader:
        if row[0] != "run":
            if int(float(row[1])) != currBartenders:
                if currBartenders >= 0 and currEntryCount != 0:
                    avgGuestCount = currGuests / currEntryCount

                    sigmaGuests = 0
                    for guestCount in currGuestCounts:
                        sigmaGuests += (guestCount - avgGuestCount) * (guestCount - avgGuestCount)
                    sigmaGuests /= currEntryCount
                    sigmaGuests = math.sqrt(sigmaGuests)

                    rows.append([currBartenders, currTables, avgGuestCount, sigmaGuests])
                currBartenders = int(float(row[1]))
                currTables = int(float(row[4]))
                currGuests = 0
                currEntryCount = 0
                currGuestCounts = []

            if float(row[3]) > 2000:
                currGuestCounts.append(int(float(row[2])))
                currGuests += int(float(row[2]))
                currEntryCount += 1
def value(row):
    if type(row[0]) is str:
        return -1
    return row[0]

rows.sort(key=value)

with open(sys.argv[2], 'w') as csvfile:
    writer = csv.writer(csvfile, delimiter=',')
    for row in rows:
        writer.writerow(row)

