package com.spamalot.reversi;

import com.spamalot.boardgame.Piece;
import com.spamalot.boardgame.UndoMove;

import java.util.List;

/**
 * Class to hold Undo Move Information.
 * 
 * @author gej
 *
 */
class ReversiUndoMove extends UndoMove<ReversiMove> {

  /**
   * Class to hold undo information.
   * 
   * @param move
   *          Move to Undo
   * @param piecesToFlip
   *          Pieces Flipped
   */
  ReversiUndoMove(final ReversiMove move, final List<Piece> piecesToFlip) {
    setMove(move);
    setFlippedPieceList(piecesToFlip);
  }

  /** List of pieces that have been flipped. */
  private List<Piece> flippedPieces;

  /**
   * Set the flipped pieces.
   * 
   * @param flipped
   *          the flipped pieces
   */
  public void setFlippedPieceList(final List<Piece> flipped) {
    this.flippedPieces = flipped;
  }

  /**
   * Get the flipped piece list.
   * 
   * @return the flipped
   */
  public List<Piece> getFlippedPieceList() {
    return this.flippedPieces;
  }
}
