#!/bin/bash


connectk="java -jar ConnectK_1.8_latest.jar"


#Test python AI vs C++ AI

	$connectk cpp:cppshell_linux.exe py:dummyai.py


#Test python AI vs JAVA AI

	$connectk PoorAI/PoorAI.class py:dummyai.py
