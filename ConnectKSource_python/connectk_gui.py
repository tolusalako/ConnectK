#Original java code: Alex Van Buskirk
#Python port: Toluwanimi Salako

import tkinter as TK
from os import listdir
import connectk
from board_model import BoardModel
from player import Player, AIPlayer, GUIPlayer
from errors import NoAISelectedError, NoGUIError
from threading import Thread, Lock
import sys, traceback

def create_newgame(model = None, player1AI=None, player2AI=None, gui = True, stepmode = False):
    '''Creates a new game. If no_gui, both players must be AI players'''
    if not gui:
        '''No GUI'''
        if player1AI is not None and player2AI is not None:
            player1 = AIPlayer(1, model, player1AI)
            player2 = AIPlayer(2, model, player2AI)
            game = connectk.ConnectK(model, player1, player2)
            p = Thread(target = game.play)
            p.start()
            return
        else:
            raise NoGUIError("If no GUI option is used, both players must be AIs.")
    else:
        '''GUI'''
        root = TK.Tk()
        if model is None: model = BoardModel() #New instance with default settings
        gui = ConnectKGUI(root, model)
        if stepmode:
            gui.stepmode.set(True)

        if player1AI is None:
            player1 = GUIPlayer(1, model)
            gui.add_button_listener(player1.action_listener, 1)
        else:
            player1 = AIPlayer(1, model, player1AI)

        if player2AI is None:
            player2 = GUIPlayer(2, model)
            gui.add_button_listener(player2.action_listener, 2)
        else:
            player2 = AIPlayer(2, model, player2AI)

        game = connectk.ConnectK(model, player1, player2, gui)
        p = Thread(target = game.play)
        gui.update_gui(time = 2)
        p.start()
        root.mainloop()

class ConnectKGUI():
    STICKY_ALL = TK.N + TK.S + TK.W + TK.E
    def __init__(self, root, model):
        '''Creates a new gui'''
        self.root = root
        self.root.wm_title("ConnectK")
        self.root.geometry('{}x{}'.format(530, 400))
        self.root.report_callback_exception = self.print_error
        self.root.protocol("WM_DELETE_WINDOW", self.root.destroy)
        # icon = TK.PhotoImage(file='icon.ico')
        # self.root.tk.call('wm', 'iconphoto', self.root.w, icon)
        TK.Grid.rowconfigure(self.root, 0, weight=1)
        TK.Grid.columnconfigure(self.root, 0, weight=1)
        self.width = model.width
        self.height = model.height
        self.gravity = model.gravity
        self.k = model.k_length
        self.colors = [None, 'red', 'blue']
        self.winning_colors = [None, '#8A1741', '#4F36C9']
        self.job_lock = Lock()
        self.jobs = []
        self.current_status = ""
        self.status = TK.StringVar()
        self.stepmode = TK.BooleanVar()
        self.step = True
        self.init_menu()
        self.init_game()

    def init_menu(self):
        '''Creates the menu items'''
        self.menu = TK.Menu(self.root)
        self.filemenu = TK.Menu(self.menu, tearoff = 0)
        self.menu.add_cascade(label="File", menu=self.filemenu)
        self.filemenu.add_command(label="New", command=self.new_game)
        self.filemenu.add_command(label="Quit", command=self.root.quit)
        self.menu.add_checkbutton(label="StepMode", variable = self.stepmode)
        self.menu.add_separator()
        self.root.config(menu = self.menu)

    def init_game(self):
        '''Creates the frames and buttons for the game'''
        self.frame_buttons = TK.Frame(self.root)
        for x in range(self.width):
            TK.Grid.columnconfigure(self.frame_buttons, x, weight=1)
        for y in range(self.height):
            TK.Grid.rowconfigure(self.frame_buttons, y, weight=1)
        self.frame_buttons.grid(row = 0, sticky = self.STICKY_ALL)

        def binded():
            print("Bound")

        self.buttons = []
        for x in range(self.width):
            col = []
            for y in range(self.height):
                b = TK.Button(self.frame_buttons, text = str(x) + ":" + str(y), width = 7, height = 3) #Might need to create custom button class
                b.grid(row = (self.height - 1) - y, column = x, sticky = self.STICKY_ALL)
                b.bind("<Button-1>", self.bind_button)
                col.append(b)
            self.buttons.append(col)


        self.frame_labels = TK.Frame(self.root)
        self.frame_labels.grid(row = 1, sticky = TK.S)
        self.status_label = TK.Label(self.frame_labels, textvariable = self.status)
        self.status_label.grid(sticky = TK.W)
        self.listens = [None, None, None]

        self.root.bind("<space>", self.toggle_step)

    def add_button_listener(self, function, player):
        self.listens[player] = function

    def bind_button(self, event):
        '''Binds a button press to a GUIplayer'''
        if self.listens[connectk.current_player] is not None:
            self.listens[connectk.current_player](event)
    
    def toggle_step(self, event):
        self.step = not self.step

    def new_game(self):
        '''Shows the newgame window'''
        dialog = NewGameWindow(self.root, self.width, self.height, self.k, self.gravity)

    def set_status(self, s):
        self.job_lock.acquire()
        self.jobs.append("self.current_status = '{}'".format(s))
        self.job_lock.release()

    def place_piece(self, location, player):
        x,y = location
        self.job_lock.acquire()
        self.jobs.append("self.buttons[{}][{}]['background'] = '{}'".format(x,y,self.colors[player]))
        self.job_lock.release()

    def highlight_spaces(self, winning_spaces, winner):
        self.job_lock.acquire()
        for x,y in winning_spaces:
            self.jobs.append("self.buttons[{}][{}]['background'] = '{}'".format(x,y,self.winning_colors[winner]))
        self.job_lock.release()

    def update_gui(self, repeat = 1, time = 2):
        self.job_lock.acquire()
        for job in self.jobs:
           exec(job)
        del self.jobs[:]
        self.job_lock.release()
        if repeat:
            self.root.after(time, self.update_gui)

    def end_updates(self, i):
        if i == 0:
            self.job_lock.acquire()
            self.jobs.append("self.end_updates(1)")
            self.job_lock.release()
        else:
            self.root.after_cancel(self.update_gui) #Cancel automatic update
            self.update_gui(0) #Update one last time
    
    def print_error(self, *args):
        '''Prints errors as they occur. 
        Tkinter usually hangs and prints all errors after exiting'''
        err = traceback.format_exception(*args)
        for i in range(len(err)):
            print (err[i])
        #err = args
        #print err[0].__name__,"\b:", err[1]
        sys.stdout.flush()
        sys.exit()

class NewGameWindow(TK.Toplevel):
    def __init__(self, parent, width, height, k, gravity):
        '''Creates a new game window'''
        TK.Toplevel.__init__(self)
        self.title("New Game")
        self.option_add("*Label.Font", "helvetica 12")
        self.label_width = TK.Label(self, text="Width: ")
        self.label_width.grid(row=0, column = 0)
        self.width = TK.StringVar(self)
        self.spinbox_width = TK.Spinbox(self, from_ = 1, to = 99, width = 2, textvariable = self.width)
        self.spinbox_width.grid(row = 0, column = 1)
        self.label_height = TK.Label(self, text="Height: ")
        self.label_height.grid(row=0, column = 2)
        self.height = TK.StringVar(self)
        self.spinbox_height = TK.Spinbox(self, from_ = 1, to = 99, width = 2, textvariable = self.height)
        self.spinbox_height.grid(row = 0, column = 3)

        self.label_k = TK.Label(self, text="K: ")
        self.label_k.grid(row=0, column = 4)
        self.k = TK.StringVar(self)
        self.spinbox_k = TK.Spinbox(self, from_ = 1, to = 99, width = 2, textvariable = self.k)
        self.spinbox_k.grid(row = 0, column = 5)
        self.label_gravity = TK.Label(self, text="Gravity: ")
        self.label_gravity.grid(row=0, column = 6)
        self.gravity = TK.StringVar(self)
        self.spinbox_gravity = TK.Spinbox(self, values=("On", "Off"), width = 3, textvariable = self.gravity)
        self.spinbox_gravity.grid(row = 0, column = 7)

        self.player1_labelframe = TK.LabelFrame(master = self, text = 'Player 1')
        self.player1_labelframe.grid(row = 1, columnspan = 8, sticky = TK.W + TK.E)
        self.player1=TK.IntVar()
        self.radiobutton_p1_human = TK.Radiobutton(master = self.player1_labelframe, text = "Human", variable = self.player1, value = 0)
        self.radiobutton_p1_human.grid(row = 0, column = 0,sticky = TK.W)
        self.radiobutton_p1_AI = TK.Radiobutton(master = self.player1_labelframe, text = "AI", variable = self.player1, value = 1)
        self.radiobutton_p1_AI.grid(row = 0, column = 1,sticky = TK.W)
        self.listbox_p1_AI = TK.StringVar(self)

        self.player2_labelframe = TK.LabelFrame(master = self, text = 'Player 2')
        self.player2_labelframe.grid(row = 2, columnspan = 8, sticky = TK.W + TK.E)
        self.player2=TK.IntVar()
        self.radiobutton_p2_human = TK.Radiobutton(master = self.player2_labelframe, text = "Human", variable = self.player2, value = 0)
        self.radiobutton_p2_human.grid(row = 0, column = 0,sticky = TK.W)
        self.radiobutton_p2_AI = TK.Radiobutton(master = self.player2_labelframe, text = "AI", variable = self.player2, value = 1)
        self.radiobutton_p2_AI.grid(row = 0, column = 1,sticky = TK.W)
        self.listbox_p2_AI = TK.StringVar(self)

        self.button_frame = TK.Frame(self)
        self.button_frame.grid(row = 3, column = 0, columnspan = 8, sticky = TK.W + TK.E)
        self.button_add_ai = TK.Button(self.button_frame, text = "Add AI", command = self.add_ai)
        self.button_add_ai.grid(row = 0, column = 2)
        self.button_newgame = TK.Button(self.button_frame, text = "New Game", command = self.newgame)
        self.button_newgame.grid(row = 0, column = 3)
        self.button_cancel = TK.Button(self.button_frame, text = "Cancel", command = self.destroy)
        self.button_cancel.grid(row = 0, column = 4)

        self.width.set(width)
        self.height.set(height)
        self.k.set(k)
        self.gravity.set("On" if gravity else "Off")
        self.parent = parent
        self.update_ai_list()

    def update_ai_list(self, ai = None):
        '''Updates the list of AIs with those in the folder + ai'''
        self.default_ai_tuple = tuple([f for f in listdir("./") if f[-3:] == ".py"])
        if (ai is None):
            self.p1_sources = TK.OptionMenu(*(self.player1_labelframe, self.listbox_p1_AI) + self.default_ai_tuple)
            self.p2_sources = TK.OptionMenu(*(self.player2_labelframe, self.listbox_p2_AI) + self.default_ai_tuple)
        else:
            self.p1_sources = TK.OptionMenu(*(self.player1_labelframe, self.listbox_p1_AI) + self.default_ai_tuple + (ai,))
            self.p2_sources = TK.OptionMenu(*(self.player2_labelframe, self.listbox_p2_AI) + self.default_ai_tuple + (ai,))
        self.p1_sources.grid(row = 0, column = 2)
        self.p2_sources.grid(row = 0, column = 2)

    def add_ai(self):
        '''Opens a file dialog for player to select AI'''
        ai = TK.filedialog.askopenfilename(filetypes=[("AI files", "*.pyc")], title="Select AI")
        if ai is not None:
            self.update_ai_list(ai)

    def newgame(self):
        '''Creates a new game based on the player's options'''
        player1AI = None
        player2AI = None
        model = BoardModel(width = int(self.width.get()), height = int(self.height.get()), k = int(self.k.get()),
         gravity = True if self.gravity.get() == "On" else False)
        #Setup human or AI player
        if (self.player1.get()):
            p1 = self.listbox_p1_AI.get()
            if len(p1) != 0:
                player1AI = p1 #Load AI Player
            else:
                raise NoAISelectedError("Select file for plater 1 AI.")

        if (self.player2.get()):
            p2 = self.listbox_p2_AI.get()
            if len(p2) != 0:
                player2AI = p2 #Load AI Player
            else:
                raise NoAISelectedError("Select file for plater 2 AI.")

        self.destroy()
        self.parent.destroy()
        create_newgame(model, player1AI, player2AI)
