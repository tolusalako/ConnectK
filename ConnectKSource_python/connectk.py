#Author: Toluwanimi Salako


from datetime import datetime
from threading import Thread
from connectk_gui import *
import sys, time

MAXTIME = 0 #In seconds. 0 = no timer
FIRST_PLAYER = 1
current_player = 0

def print_(message):
    print(message)
    sys.stdout.flush()

class PlayerThread(Thread):
    '''A thread to allow the GUI to run alongside the game'''
    def __init__(self, player, model, deadline):
        Thread.__init__(self)
        self.player = player
        self.model = model
        self.deadline = deadline
        self.move = None

    def run(self):
        try:
            self.move = self.player.get_move(self.model, self.deadline)
            self.player.reset_move() #Resets the player's last move for the next turn
        except Exception as e:
            raise e

class ConnectK():
    '''Handles player moves, turns, and timing'''
    def __init__(self, model, player1, player2, view = None):
        self.currentboard = model
        self.players = [None, player1, player2]
        self.view = view

    def play(self):
        self.current_player = FIRST_PLAYER
        self.winner = 0

        while self.currentboard.winner() == -1:
            if (not self.view is None and self.view.stepmode.get()):
                if self.view.step:
                    if self.step(): break
                    self.view.toggle_step(None)
                time.sleep(0.3)
            else:
                if self.step(): break

        if (self.winner != 0):
            if (self.view is not None):
                self.view.set_status("Player {} [{}] wins!".format(self.winner,
                    self.players[self.winner].team_name))
                self.view.highlight_spaces(self.currentboard.winning_spaces(), self.winner)
            print_("Player {} [{}] wins!".format(self.winner, self.players[self.winner].team_name))
        if (not self.currentboard.has_moves_left()):
            if (self.view is not None):
                self.view.set_status("Draw")
            print_("Draw")
        return self.winner

    def step(self): 
        global current_player
        current_player = self.current_player #Update value for GUI

        print_("Player {} says: ".format(self.current_player))
        move = None
        begin = datetime.now()
        pt = PlayerThread(self.players[self.current_player], self.currentboard.clone(), MAXTIME)
        pt.start()
        pt.join()
        move = pt.move
        
        if (move is None or self.currentboard.get_space_tuple(move) != 0):
            print_ ("Player {} returned bad move: {}. Or went over time: {}"
                .format(self.current_player, str(move), str(datetime.now() - begin)))
            self.winner = 1 if self.current_player == 2 else 2 #Forfeit
            return 1
        else:
            self.currentboard = self.currentboard.place_piece(move, self.current_player) #Update board
            print_ ("Player {} returns move {}" .format(self.current_player, str(move)))
            if (self.view is not None):
                self.view.place_piece(self.currentboard.last_move, self.current_player)
            self.current_player = 1 if self.current_player == 2 else 2
        self.winner = self.currentboard.winner()

    def width(self):
        return self.currentboard.width

    def height(self):
        return self.currentboard.height


if __name__ == '__main__':
    argc = len(sys.argv)
    if argc > 1:
        options = {
            'w':9, #width
            'h':7, #height
            'k':5, #k_length
            'g':0, #gravity 
            'u': 1, #gui
            's': 0 #stepmode
        }
        ai_players = []
        for i in range(1, argc):
            if sys.argv[i][0] == '-':
                options[sys.argv[i][1].lower()] = int(sys.argv[i][3])
            else:
                ai_players.append(sys.argv[i])
        model = BoardModel(width = options['w'], height = options['h'], k = options['k'],
         gravity = options['g'])
        if len(ai_players) == 2:
            create_newgame(model, ai_players[0], ai_players[1], gui = options['u'], stepmode = options['s']) 
        elif len(ai_players) == 1:
             create_newgame(model, ai_players[0], gui = options['u'], stepmode = options['s']) 
        else:
            create_newgame(model = model, gui = options['u'], stepmode = options['s'])
    else:
        create_newgame()
