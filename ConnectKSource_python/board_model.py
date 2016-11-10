#Original java code: Alex Van Buskirk
#Python port: Toluwanimi Salako

def two_dim_list_of_ints(width, height):
	'''Returns a 2D list of ints filled with 0s'''
	result = [];
	for x in range(width):
		row = [0 for y in range(height)]
		result.append(row)
	return result

class BoardModel():
	_winner = -2
	last_move = tuple()
	def __init__(self, width = 9, height = 7, k = 5, gravity = False):
		'''Creates a new game board'''
		self.width = width
		self.height = height
		self.k_length = k
		self.gravity = gravity
		self.spaces_left = width*height
		self.pieces = two_dim_list_of_ints(width, height)

	def __str__(self): 
		'''Returns game board as string'''
		return str(self.pieces).replace("],", "]\n")

	def place_piece(self, location, player):
		'''Updates the board with player moves'''
		x, y = location
		assert(self.pieces[x][y] == 0);
		result_board = self.clone()
		while (self.gravity and y > 0 and self.pieces[x][y-1] == 0):
			y -= 1
		result_board.last_move = (x, y)
		result_board.pieces[x][y] = player
		result_board.spaces_left = self.spaces_left - 1
		return result_board

	def get_space_tuple(self, location):
		'''Returns the player who is in location'''
		x,y = location
		return self.get_space(x, y)

	def get_space(self, x, y):
		'''Returns the player who is in x,y'''
		assert(x >= 0  and x < self.width)
		assert(y >= 0 and y < self.height)
		return self.pieces[x][y]

	def get_width(self):
		'''Returns the width of the game board'''
		return self.width

	def get_height(self):
		'''Returns the height of the game board'''
		return self.height

	def get_k_length(self):
		'''Returns the k_length of the game board'''
		return self.k_length

	def gravity_enabled(self):
		'''Returns if gravity is enabled or not'''
		return self.gravity

	def get_last_move(self):
		'''Returns the last move made'''
		return self.last_move

	def has_moves_left(self):
		'''Returns if the game board has any empty spaces left'''
		return self.spaces_left > 0

	def winner(self):
		'''Checks for and returns a winner if there is one'''
		width = self.width
		height = self.height
		if (self._winner == -2): #uncached 
			uncached = False
			for i in range(self.width):
				for j in range(self.height):
					if(self.pieces[i][j] == 0):
						if(self.gravity):
							break
						else:
							continue

					if(i-1<0 or self.pieces[i-1][j] != self.pieces[i][j]):
						count = 1
						while(i+count < width and self.pieces[i][j] == self.pieces[i+count][j]):
							count+=1
							if(count >= self.k_length):
								self._winner = self.pieces[i][j]
								uncached = True
								break
						if uncached: break

					if(i-1<0 or j-1<0 or self.pieces[i-1][j-1] != self.pieces[i][j]):
						count = 1
						while(i+count < width and j+count < height and self.pieces[i][j] == self.pieces[i+count][j+count]):
							count+=1
							if(count >= self.k_length):
								self._winner = self.pieces[i][j]
								uncached = True
								break
						if uncached: break

					if(i-1<0 or j+1>=height or self.pieces[i-1][j+1] != self.pieces[i][j]):
						count = 1
						while(i+count < width and j-count >= 0 and self.pieces[i][j] == self.pieces[i+count][j-count]):
							count+=1
							if(count >= self.k_length):
								self._winner = self.pieces[i][j]
								uncached = True
								break
						if uncached: break

					if(j-1<0 or self.pieces[i][j-1] != self.pieces[i][j]):
						count = 1
						while(j+count < height and self.pieces[i][j] == self.pieces[i][j+count]):
							count+=1
							if(count >= self.k_length):
								self._winner = self.pieces[i][j]
								uncached = True
								break
						if uncached: break
				if uncached: break
			if not uncached:
				self._winner = -1 if self.has_moves_left() else 0
		return self._winner

	def winning_spaces(self):
		'''Returns the winning spaces'''
		result = []
		width = self.width
		height = self.height
		for i in range(self.width):
			for j in range(self.height):
				if(self.pieces[i][j] == 0):
					if(self.gravity):
						break
					else:
						continue

				if(i-1<0 or self.pieces[i-1][j] != self.pieces[i][j]):
					count = 1
					while(i+count < width and self.pieces[i][j] == self.pieces[i+count][j]):
						count+=1
						if(count >= self.k_length):
							for k in range(self.k_length):
								result.append((i+k, j))
							return result

				if(i-1<0 or j-1<0 or self.pieces[i-1][j-1] != self.pieces[i][j]):
					count = 1
					while(i+count < width and j+count < height and self.pieces[i][j] == self.pieces[i+count][j+count]):
						count+=1
						if(count >= self.k_length):
							for k in range(self.k_length):
								result.append((i+k, j+k))
							return result
							
					
				if(i-1<0 or j+1>=height or self.pieces[i-1][j+1] != self.pieces[i][j]):
					count = 1
					while(i+count < width and j-count >= 0 and self.pieces[i][j] == self.pieces[i+count][j-count]):
						count+=1
						if(count >= self.k_length):
							for k in range(self.k_length):
								result.append((i+k, j-k))
							return result

				if(j-1<0 or self.pieces[i][j-1] != self.pieces[i][j]):
					count = 1
					while(j+count < height and self.pieces[i][j] == self.pieces[i][j+count]):
						count+=1
						if(count >= self.k_length):
							for k in range(self.k_length):
								result.append((i, j+k))
							return result
		return result

	def clone(self):
		'''Returns a clone of the game board'''
		cloned = BoardModel(self.width, self.height, self.k_length, self.gravity)
		cloned.last_move = self.last_move
		cloned.spaces_left = self.spaces_left
		for i in range(self.width):
			for j in range(self.height):
				cloned.pieces[i][j] = self.pieces[i][j]
		return cloned

	def equals(right):
		'''Checks if 'right' is equal to 'self' '''
		if (not right.__class__ == BoardModel):
			return False
		if(self.width != right.width or self.height != right.height or self.k_length != right.k_length):
			return False
		for i in range(width):
			for j in range(height):
				if(self.pieces[i][j] != right.pieces[i][j]):
					return False
		return True


