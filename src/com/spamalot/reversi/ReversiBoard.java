package com.spamalot.reversi;

import com.spamalot.boardgame.Board;

/**
 * The Reversi Board.
 * 
 * @author gej
 *
 */
class ReversiBoard extends Board {

  /**
   * Construct a Reversi board object.
   * 
   * @param size
   *          Length of each side of the board
   */
  ReversiBoard(final int size) {
    super(size);
    initSquares();
  }

  /**
   * Initialize the Squares to have information about their neighbors.
   */
  @Override
  protected void initSquares() {
    for (int rank = 0; rank < getNumRanks(); rank++) {
      for (int file = 0; file < getNumFiles(); file++) {
        initSquareDirectionMap(rank, file);
      }
    }
  }
}
