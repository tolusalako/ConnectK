#!/bin/bash
_logfile="errorlog_unzip_ai"
ai_folder="AssignmentSubmission"
extracted_ais="$ai_folder/extracted_ais"

#Logger
log(){
    if [[ ! -f $_logfile ]]; then
        touch $_logfile
    fi
    echo -e "$(cat $_logfile)\n$1" > $_logfile
}

#Extracts all ais to an extracted_ais folder
extract_ais(){
    #Log files that aren't in .zip format (They'll be ignored)
    #Ignore .txt and .pdf student reports
    invalid_files=$(find $1 ! -path $ai_folder ! -name *.zip ! -name *.txt ! -name *.pdf)
    log "The following files were not compressed in .zip format:"
    for f in ${invalid_files[*]}
    do
        log $(echo $f | sed s/$ai_folder//g)
    done
    
    #Extract all files in .zip format
    compressed_ais=$(find $1 -name *.zip)
    for f in ${compressed_ais[*]}
    do
        f_name=$(echo $f | sed s/$ai_folder//g | sed s/.zip$//g) #File name only, removing the .zip extensions
        mkdir -p $extracted_ais/$f_name
        unzip -n $f -d $extracted_ais/$f_name 
    done

    echo "Finished Unzipping AIs."
}

#Look for the specified ai_folder, then call extract_ais
if [[ -d $ai_folder ]]; then
    echo "" > $_logfile #Clear log file
    if [[ -d $extracted_ais ]]; then
        rm -rf $extracted_ais #Del existing ais in the directory
    fi
    extract_ais $ai_folder
    exit 0
else
    echo "Make sure this script is in the same directory as $ai_folder."
    exit 1
fi
