#!/bin/bash

connectk="python3 connectk.py"

#Test no gui game with AI vs AI
$connectk -u:0 student_ai.py student_ai.py

#Test gui game with AI vs AI
$connectk -u:1 student_ai.py student_ai.py

#Test gui game different settings
$connectk -u:1 -w:5 -h:5 -g:1 student_ai.py student_ai.py
