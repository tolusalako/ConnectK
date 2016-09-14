#Original java code: Alex Van Buskirk
#Python port: Toluwanimi Salako

class InvalidFileError(Exception):
	def __init__(self, message):
		Exception.__init__(self, message)

class NoAISelectedError(Exception):
	def __init__(self, message):
		Exception.__init__(self, message)

class NoGUIError(Exception):
	def __init__(self, message):
		Exception.__init__(self, message)
