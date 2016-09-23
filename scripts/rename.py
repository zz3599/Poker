import sys
import os
import glob
path = sys.argv[1]

# path = 'C:\Users\brookz\Downloads\Playing Cards\Playing Cards\PNG-cards-1.3'


def rename(dir, pattern, replacement):
    filenames = os.listdir(path)
    print filenames
    for filename in filenames:
        if pattern in filename:
            os.rename(os.path.join(dir, filename), os.path.join(dir, filename.replace(pattern, replacement)))

rename(path, 'jack', '11')
rename(path, 'queen', '12')
rename(path, 'king', '13')
