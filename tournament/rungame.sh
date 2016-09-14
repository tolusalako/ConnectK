#!/bin/bash
#
#$ -cwd
#$ -S /bin/bash
#$ -o output
#$ -e errors
#$ -V
#
#Usage: rungame ai_1_ID ai_2_ID ai_1 ai_2
connectk="ConnectK_1.8_latest.jar"
score_tracker="score_log"

if [[ -z $1 ]] || [[ -z $2 ]] || [[ -z $3 ]] || [[ -z $4 ]]; then
    echo "Error: Run with 2 or more  AIs as options."
    exit 1
fi

id1=$1
id2=$2
shift
shift
if [[ -n $1 && -n $2 ]]; then 
    output=$(java -jar $connectk -nogui $1 $2)
    winner=${output[@]:(-1)}
    echo "$id1 vs $id2: $winner"
fi
