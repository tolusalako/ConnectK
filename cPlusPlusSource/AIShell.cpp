#include "AIShell.h"
#include <iostream>


AIShell::AIShell(int numCols, int numRows, bool gravityOn, int** gameState, Move lastMove)
{
	this->deadline=0;
	this->numRows=numRows;
	this->numCols=numCols;
	this->gravityOn=gravityOn;
	this->gameState=gameState;
	this->lastMove=lastMove;
}


AIShell::~AIShell()
{
	
	//delete the gameState variable.
	for (int i =0; i<numCols; i++){
		delete [] gameState[i];
	}
	delete [] gameState;

}

Move AIShell::makeMove(){
	//this part should be filled in by the student to implement the AI
	//Example of a move could be: Move move(1, 2); //this will make a move at col 1, row 2
	
	
	//this will move to the left-most column possible.
	for (int col = 0; col<numCols; col++){
		for (int row = 0; row<numRows; row++){
			if (gameState[col][row] == NO_PIECE){
				Move m(col, row);
				return m;
			}
		}
	}
	Move m(0, 0);
	return m;
	 
}
