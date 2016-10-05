#Author: Toluwanimi Salako

from collections import defaultdict
import random

team_name = "StudentAI-Default"

class StudentAI():
	def __init__(self, player, state):
		self.last_move = state.get_last_move()
		self.model = state
	def make_move(self, deadline):
		'''Write AI Here. Return a tuple (col, row)'''
		width = self.model.get_width()
		height = self.model.get_height()
		spaces = defaultdict(int)

		for i in range(width):
			for j in range(height):
				spaces[(i,j)] = self.model.get_space(i, j)

		moves = [k for k in spaces.keys() if spaces[k] == 0]
		return moves[random.randint(0, len(moves) - 1)]