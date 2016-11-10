#Author: Toluwanimi Salako
from collections import defaultdict
import random
import sys
sys.path.append(r'\ConnectKSource_python')
import ConnectKSource_python.board_model as boardmodel

team_name = "StudentAI-Default" #TODO change me

class StudentAI():
    def __init__(self, player, state):
        self.last_move = state.get_last_move()
        self.model = state
    def make_move(self, model, deadline):
        '''Write AI Here. Return a tuple (col, row)'''
        width = self.model.get_width()
        height = self.model.get_height()
        spaces = defaultdict(int)

        for i in range(width):
            for j in range(height):
                spaces[(i,j)] = self.model.get_space(i, j)
        print(self.model.spaces_left)

        moves = [k for k in spaces.keys() if spaces[k] == 0]
        return moves[random.randint(0, len(moves) - 1)]

'''===================================
DO NOT MODIFY ANYTHING BELOW THIS LINE
==================================='''

is_first_player = False
deadline = 0
model = None

ai_piece = 1
human_piece = -1
no_piece = 0

def make_ai_shell_from_input():
    '''
    Reads board state from input and returns the move chosen by StudentAI
    DO NOT MODIFY THIS
    '''
    global is_first_player
    global model
    ai_shell = None
    begin =  "makeMoveWithState:"
    end = "end"

    go = True
    while (go):
        mass_input = input().split(" ")
        if (mass_input[0] == end):
            sys.exit()
        elif (mass_input[0] == begin):
            #first I want the gravity, then number of cols, then number of rows, then the col of the last move, then the row of the last move then the values for all the spaces.
            # 0 for no gravity, 1 for gravity
            #then rows
            #then cols
            #then lastMove col
            #then lastMove row.
            #then deadline.
            #add the K variable after deadline.
            #then the values for the spaces.
            #cout<<"beginning"<<endl;
            gravity = int(mass_input[1])
            col_count = int(mass_input[2])
            row_count = int(mass_input[3])
            last_move_col = int(mass_input[4])
            last_move_row = int(mass_input[5])

            #add the deadline here:
            deadline = -1
            deadline = int(mass_input[6])
            k = int(mass_input[7])
            #now the values for each space.


            counter = 8
            #allocate 2D array.
            model = boardmodel.BoardModel(col_count, row_count, k, gravity)
            count_own_moves = 0

            for col in range(col_count):
                for row in range(row_count):
                    model.pieces[col][row] = int(mass_input[counter])
                    if (model.pieces[col][row] == ai_piece):
                        count_own_moves += model.pieces[col][row]
                    if (not model.pieces[col][row] == no_piece):
                        model.spaces_left -= 1
                    counter+=1

            if (count_own_moves % 2 == 0):
                is_first_player = True

            model.last_move = (last_move_col, last_move_row)
            ai_shell = StudentAI(1 if is_first_player else 2, model)

            return ai_shell
        else:
            print("unrecognized command", mass_input)
        #otherwise loop back to the top and wait for proper _input.
    return ai_shell

def return_move(move):
    '''
    Prints the move made by the AI so the wrapping shell can input it
    DO NOT MODIFY THIS
    '''
    made_move = "ReturningTheMoveMade";
    #outputs made_move then a space then the row then a space then the column then a line break.
    print(made_move, move[0], move[1])

def check_if_first_player():
    global is_first_player
    return is_first_player

if __name__ == '__main__':
    '''
    DO NOT MODIFY THIS
    '''
    print ("Make sure this program is ran by the Java shell. It is incomplete on its own. :")
    go = True
    while (go): #do this forever until the make_ai_shell_from_input function ends the process or it is killed by the java wrapper.
        ai_shell = make_ai_shell_from_input()
        moveMade = ai_shell.make_move(model, deadline)
        return_move(moveMade)
        del ai_shell
        sys.stdout.flush()
