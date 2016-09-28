#!/bin/bash
#
#$ -cwd
#$ -S /bin/bash
#$ -o output
#$ -e errors
#$ -V
#
#Usage: rungame ai_1_ID ai_2_ID ai_1 ai_2
connectk="Tournament.jar"
java -jar $connectk
