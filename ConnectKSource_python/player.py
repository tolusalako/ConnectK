#Original java code: Alex Van Buskirk
#Python port: Toluwanimi Salako

import os, imp
from datetime import datetime, timedelta
from errors import InvalidFileError

ai_class = "StudentAI"
ai_name = "team_name"

def load_from_file(filepath):
    '''Loads an AI player from file'''
    global ai_class
    py_ai = None
    ai_file,file_ext = "",""
    #Make sure the file is compiled & can be opened
    try:
        ai_file,file_ext = os.path.splitext(os.path.split(filepath)[-1])
        if file_ext != ".py":
            raise InvalidFileError("[{}]: must be compiled.".format(ai_name+file_ext))
        else:
            py_ai = imp.load_source(ai_file, filepath)
    except Exception as e:
        raise e

    #Make sure the file has the AI class
    if ((py_ai is None) or (not hasattr(py_ai, ai_class))):
        raise InvalidFileError("{} must contain class \"{}\".".format(ai_name+file_ext, ai_class))
    #Make sure the file has the AI name
    if (not hasattr(py_ai, ai_name)):
        raise InvalidFileError("{} must contain global variable \"{}\".".format(ai_name+file_ext, ai_file))
    else:
        return py_ai

class Player():
    def __init__(self, player, state):
        self.last_move = None
        self.player = player
        self.start_state = state
        self.team_name = ""

    def __str__(self):
        return self.team_name

    def set_teamname(self, name):
        self.team_name = name

    def get_move(self, state, deadline = 0):
        result = None
        if deadline == 0:
            while(result is None):
                result = self.last_move
        else:
            result = self.get_move_with_time(state, deadline);
        return self.last_move

    def get_move_with_time(self, state, deadline):
        result = None
        deadline = timedelta(seconds=deadline)
        begin = datetime.now()
        while(result is None):
            result = self.last_move
            if (datetime.now() - begin >= deadline):
                break
        return result

    def reset_move(self):
        self.last_move = None

class AIPlayer(Player):
    def __init__(self, player, state, filepath):
        Player.__init__(self, player, state)
        self.aifile = load_from_file(filepath)
        self.ai = self.aifile.StudentAI(player, state)
        self.set_teamname(self.aifile.team_name)

    def get_move(self, state, deadline = 0):
        result = None
        if deadline == 0:
            while(result is None):
                result = self.ai.make_move(state, deadline)
        else:
            result = self.get_move_with_time(state, deadline);
        self.last_move = result
        return self.last_move

    def get_move_with_time(self, state, deadline):
        result = None
        deadline = timedelta(seconds=deadline)
        begin = datetime.now()
        while(result is None):
            result = self.ai.make_move(state, deadline)
            if (datetime.now() - begin >= deadline):
                break
        return result

class GUIPlayer(Player):
    def __init__(self, player, state):
        Player.__init__(self, player, state)
        self.team_name = "GUI"

    def action_listener(self, event):
        x,y = event.widget["text"].split(":")
        self.last_move = (int(x), int(y))
        
