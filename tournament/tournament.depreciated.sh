#!/bin/bash

unzip_script="unzip_ais.sh"
extracted_ais="AssignmentSubmission/extracted_ais"
score_tracker="score_log"

#Creates a new tournament
create_tournament(){
    local team_folders=($(ls $extracted_ais))
    teams=()
    for f in ${team_folders[@]}
    do
        local n=(${f//_/ })
        local team_name=${n[@]:(-1)}
        local t=$(find "$extracted_ais/$f/" \( -name ${team_name}AI.class -o -name ${team_name}AI.exe -o -name ${team_name}AI.py \))
        if [[ -n $t ]]; then
            teams+=($t)
        else
            :
            #echo "Coulnd't find file for $f-$team_name"
        fi
    done
    local n=${#teams[@]}
    echo "Creating tournament with $n teams."
    for (( i=0; i<$n; i+=2 ))
    do
        if [[ -n $i && -n $((i+1)) ]]; then
            #printf "[$i]-${teams[$i]} \n vs [$((i+1))]-${teams[$((i+1))]}\n"    
            local winner=$(qsub rungame.sh $i $((i+1)) ${teams[$i]} ${teams[$((i+1))]})
        else
            :
        fi
    done
}

#Make sure the unzip script is in the same directory
if [[ ! -f $unzip_script ]]; then
   echo "Make sure this script is in the same directory as $unzip_script."
fi
if [[ ! -d $extracted_ais ]]; then
   read -r -p "Could not find $extracted_ais. Would you like to run $unzip_script? [y/N] " response
   if [[ $response =~ ^([yY][eE][sS]|[yY])$ ]]; then
       echo "Running $unzip_script"
       $unzip_script
       result=$?
       if [[ $result == 0 ]]; then
           create_tournament
       else
           echo "$unzip_script failed. Exiting..."
           exit 1
       fi
   else
       echo "Please manually run $unzip_script then re-run this script."
       exit 1
   fi
else
   create_tournament
fi
