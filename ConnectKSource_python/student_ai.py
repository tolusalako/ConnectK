#Author: Toluwanimi Salako

from collections import defaultdict
import random

team_name = "StudentAI-Default"

class StudentAI():
    def __init__(self, player, state):
        self.last_move = state.get_last_move()
    def make_move(self, model,deadline):
        '''Write AI Here. Return a tuple (col, row)'''
        width = model.get_width()
        height = model.get_height()
        spaces = defaultdict(int)
        print(model)
        for i in range(width):
            for j in range(height):
                spaces[(i,j)] = model.get_space(i, j)

        moves = [k for k in spaces.keys() if spaces[k] == 0]
        return moves[random.randint(0, len(moves) - 1)]
