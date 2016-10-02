#ConnectK

This readme provides instructions and tips that may aid you in the programming of your ConnectK AI

Running ConnectK.jar:
	You can run it using the commandline/terminal with the command java -jar ConnectK.jar from the directory the file is in.
	Alternatively, you can simply doubleclick the jar to run it.
	
Playing Connect K with ConnectK.jar:
	After starting up ConnectK, the game will default to a two player (human vs. human) game of connect 5.

	You can change the settings of the game by selecting File->New from the drop down menu on the upper left. The width, height, K, and gravity of the board can be modified by changing the values in the 4 boxes labeled width, height, K, and gravity. 
	
	Additionally, you can choose to import AI opponents. 3 AI opponents are provided for you. Click Add AI and navigate to ~/AverageAI/AverageAI.class located in the folder containing this README. Click open, and new game to start a game against the average AI. 

Writing an AI:
Preparation: 
	The following instructions will tell you how to begin writing an AI with eclipse. 
	
	Create a new java project.
	
	Put dummyAI.java in the default package for the new project. This file will act as starter code to help write your own AI. Make sure you rename the file to something else. 

	Add ConnectK.jar to the build path of the project. You can do this by right clicking the project, scrolling to "build path" and click "add external archives". Locate your ConnectK.jar file and click open. 
	
	*Note the source of ConnectK.jar is provided for you as a convenience. Do NOT include it in your java project. 
	
	Now you can begin formulating your own AI. 

The template:
	 An overloaded method and a constructor are provided. 
	 
	 The constructor will be called with a player (1 or 2) and a blank BoardModel containing the dimensions (width, height) and rules (gravity, and k) for the game. 
	 
	 The method getMove will be called with an updated gamestate (i.e. blank if it is a new game, has a piece for each move that has been played since) and should return a move in the form of a java.awt.Point(). 
	 
	 GetMove can also be called with an additional parameter: deadline. deadline represents the amount of time in milliseconds that you have to return a move. 

BoardModel: 
	BoardModel contains the rules, dimensions, and pieces in the game. 
	
	The pieces are stored in a 2D array of bytes with the indices [x][y] representing column and row, respectively. The lower left corner is represented by (0,0) while the upper left, upper right, and bottom right corners in a R by C gameboard are represented by (0, R-1), (C-1, R-1), (C-1,0) respectively. Each slot in the array contains either a 0,1,2 which represent that the space is empty, occupied by player 1, or occupied by player 2, respectively. Note that these integer values are different from the C++ version.
	
	
	There are getters available for the rules and dimensions as well as getters for the last move placed i.e. getLastMove()
	
	There are also many convenience methods available such as toString() which outputs a string representation of the game board. Please refer to BoardModel.java in the file "ConnectKSource.zip".	
	
	Although most of the member variables are public, it is recommended to use the getters (e.g. getSpace(int x, int y) instead of directly accessing 'pieces[x][y]'. 

C++ users:
	Please get C++ code from http://www.ics.uci.edu/~rickl/courses/AI-projects/connect-k/CS171ConnectKStudentResources/cPlusPlusSource/. ConnectK.cpp has main function to get a current state from the java shell. Please implement your AI agent in AIShell::makeMove(). AIShell::gameState has the current game state as array of integers. 0, 1 and -1 represent empty, AI piece and human piece respectively. Note that these integer values are different from the Java version.

	Then compile your code and make an executable file (for example on Windows, myAI.exe). On Linux, open a terminal and type
			g++ ConnectK.cpp Move.cpp AIShell.cpp -o myAI

	Then you can test your AI agent by running ConnectK.jar with the following command.
	On Windows:
		   java -jar ConnectK.jar cpp:myAI.exe
	On Linux:
		   java -jar ConnectK.jar cpp:myAI
Python users:
	Python has it's own standalone shell located in ConnectKSource_python. The shell only supports python AIs at the moment. To run your python AIs in the java shell:
	Write your ai in the make_move() method in dummyai.py.
	Then start with the following command:
		java -jar ConnectK.jar py:dummyai.py
