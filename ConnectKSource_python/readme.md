#ConnectK Python Readme

This file explains how to run connect k (python) on openlab

You will need to set up Putty and Xming (for gui). Instructions can be found (here) [http://laptops.eng.uci.edu/instructional-computing/incoming-students/using-linux/how-to-configure-xming-putty].

1) Log on to openlab.ics.uci.edu with X11 forwarding enabled.
-X11 let's us interact with the graphical user interface while logged on to openlab.
2) Make sure you are in the python source directory
3) Load the python/3.5.1 module -> "module load python/3.5.1"
-The default python version does not have the necessary libraries to run ConnectK. Loading version 3.5.1 also loads additional libraries.
4) Start the game -> "python3 connectk.py" or "python3 connectk.py student_ai.py -w:5 -k:4"

'''
Options:
'w'-width
'h'-height
'k'-k_length
'g'-gravity 
'u'-gui
's'-stepmode
'''

##Known Issues
1. If you get a import error after running "python3 connectk.py". The error says that module "connectk_gui" can't be found.
FIX: Run "python3 ./connectk.py" instead. Make sure you are running from the directory as python doesn't automatically add the current working directory to path.

2. THE GUI SUCKS. I suggest we use kivy, or some newer gui lib for the python shell

