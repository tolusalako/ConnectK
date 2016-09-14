import os.path
import re, sys, subprocess

unzip_script="unzip_ais.sh"
extracted_ais="AssignmentSubmission/extracted_ais"
score_tracker="score_log"


def create_tournament():
    team_folders = subprocess.check_output(["ls", extracted_ais])
    team_folders = team_folders.decode("utf-8").split("\n")
    for team in team_folders:
        if len(team) > 0:
            team_name = team.split("_")[-1]
            team_ai = ""
            try:
                team_ai = subprocess.check_output(["find", extracted_ais+"/"+team, "\\( -name {0}AI.class -o -name {0}AI.exe -o -name {0}AI.py \\)".format(team_name)])
            except subprocess.CalledProcessError as e:
                pass
                #print(e.output)
            print(team_ai)

if __name__ == "__main__":
    if not os.path.exists(extracted_ais):
        response = str(input("Could not find '{}'. Would you like to run '{}'? [y/N] ".format(extracted_ais, unzip_script)))
        p = re.compile('^([yY][eE][sS]|[yY])$')
        if re.search(p, str(response)) is not None:
            if not os.path.exists(unzip_script):
                print("Make sure this script is in the same directory as {}.".format(unzip_script))
            else:
                try:
                    output = subprocess.check_output(["./"+unzip_script])
                except subprocess.CalledProcessError as e:
                    print(e.output)
        else:
            sys.exit(1)
    

    #Run Tournament
    create_tournament()
