package com.spamalot.reversi;

import com.spamalot.boardgame.Coordinate;
import com.spamalot.boardgame.Direction;
import com.spamalot.boardgame.Game;
import com.spamalot.boardgame.GameControllable;
import com.spamalot.boardgame.GameException;
import com.spamalot.boardgame.Move;
import com.spamalot.boardgame.Piece;
import com.spamalot.boardgame.PieceColor;
import com.spamalot.boardgame.PieceCount;
import com.spamalot.boardgame.Square;
import com.spamalot.boardgame.ai.MinMaxSearchable;
import com.spamalot.boardgame.ai.NegaMax;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * Handle the Reversi Game.
 * 
 * @author gej
 *
 */
public final class ReversiGame extends Game implements MinMaxSearchable<ReversiMove>, GameControllable<ReversiGame, ReversiMove> {

  /** Default Board Size Constant. */
  private static final int DEFAULT_REVERSI_BOARD_SIZE = 8;

  /** Stack for undo move list. */
  private Stack<ReversiUndoMove> undoMoveStack = new Stack<>();

  /**
   * Create a Reversi board of the default size.
   * 
   * @throws GameException
   *           if something goes wrong.
   */
  ReversiGame() throws GameException {
    this(DEFAULT_REVERSI_BOARD_SIZE);
  }

  /**
   * Construct the game Object with a square board of a specified size.
   * 
   * @param size
   *          Size of a side
   * @throws GameException
   *           when there is some problem.
   */
  private ReversiGame(final int size) throws GameException {
    setBoard(new ReversiBoard(size));
    initGame();
  }

  /**
   * Set up board.
   */
  @Override
  protected void initGame() {
    getBoard().getSquareAt((getNumFiles() / 2) - 1, (getNumRanks() / 2) - 1).setPiece(new Piece(PieceColor.WHITE));
    getBoard().getSquareAt(getNumFiles() / 2, getNumRanks() / 2).setPiece(new Piece(PieceColor.WHITE));
    getBoard().getSquareAt((getNumFiles() / 2) - 1, getNumRanks() / 2).setPiece(new Piece(PieceColor.BLACK));
    getBoard().getSquareAt(getNumFiles() / 2, (getNumRanks() / 2) - 1).setPiece(new Piece(PieceColor.BLACK));
  }

  @Override
  public int evaluate(final boolean gameOver) {
    PieceCount p = getPieceCount();

    return (p.getWhiteCount() - p.getBlackCount()) * 100;
  }

  /**
   * Undo the effects of the last move made.
   */
  @Override
  public void undoLastMove() {
    ReversiUndoMove undoMove = this.undoMoveStack.pop();

    if (undoMove.getMove().getType() != Move.Type.PASS) {
      Coordinate c = undoMove.getMove().getToCoordinate();
      Square sq = this.getBoard().getSquareAt(c);
      sq.pickupPiece();

      flipPieces(undoMove.getFlippedPieceList());
    }
    switchColorToMove();
  }

  @Override
  public void makeMove(final ReversiMove move) {

    List<Piece> piecesToFlip = null;

    if (move.getType() != Move.Type.PASS) {
      Piece piece = new Piece(move.getColor());

      Coordinate c = move.getToCoordinate();
      Square toSquare = this.getBoard().getSquareAt(c);

      toSquare.setPiece(piece);

      piecesToFlip = getListOfPiecesToFlip(toSquare);
      flipPieces(piecesToFlip);
    }

    ReversiUndoMove undoMove = new ReversiUndoMove(move, piecesToFlip);
    this.undoMoveStack.add(undoMove);

    switchColorToMove();
  }

  /**
   * Generate the List of Pieces that will need to be flipped. This method does
   * not do any flipping. No flipping! No flipping!
   * 
   * @param toSquare
   *          Square where move is made
   * 
   * @return the List of Pieces.
   */
  private List<Piece> getListOfPiecesToFlip(final Square toSquare) {
    List<Piece> piecesToFlip = new ArrayList<>();

    for (Direction dir : Direction.values()) {
      List<Piece> candidatePiecesToFlip = new ArrayList<>();
      Square square = toSquare.getSquareInDirection(dir);
      while (hasOppositeColorPiece(square)) {
        candidatePiecesToFlip.add(square.getPiece());
        square = square.getSquareInDirection(dir);
      }
      if (hasSameColorPiece(square)) {
        piecesToFlip.addAll(candidatePiecesToFlip);
      }
    }
    return piecesToFlip;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ReversiGame [board=\n");
    builder.append(getBoard());
    builder.append("toMove=");
    builder.append(getColorToMove());
    builder.append("\ngetAvailableMoves()=");
    builder.append(getAvailableMoves());
    builder.append("\n]");
    return builder.toString();
  }

  @Override
  public List<ReversiMove> getAvailableMoves() {

    // Using a Set to avoid duplicate moves being stored.
    Set<ReversiMove> moves = new HashSet<>();

    for (int file = 0; file < getNumFiles(); file++) {
      for (int rank = 0; rank < getNumRanks(); rank++) {
        Square sq = getBoard().getSquareAt(file, rank);
        if (hasSameColorPiece(sq)) {
          for (Direction dir : Direction.values()) {
            ReversiMove newMove = findMoveInDirection(sq, dir);
            if (newMove != null) {
              moves.add(newMove);
            }
          }
        }
      }
    }

    List<ReversiMove> ret = new ArrayList<>();
    if (moves.size() > 0) {
      ret.addAll(moves);
    } else {
      ret.add(new ReversiMove());
    }

    return ret;
  }

  /**
   * Look in a direction from a square to see if it can make a move and create
   * the ReversiMove object if it can.
   * 
   * @param square
   *          Square to start from
   * @param dir
   *          Direction to look
   * @return A ReversiMove or null if no move.
   */
  private ReversiMove findMoveInDirection(final Square square, final Direction dir) {
    Square lookSquare = square.getSquareInDirection(dir);

    // Exit early if the first neighbor isn't an opposite color piece.
    if (!hasOppositeColorPiece(lookSquare)) {
      return null;
    }

    lookSquare = lookSquare.getSquareInDirection(dir);
    while (hasOppositeColorPiece(lookSquare)) {
      lookSquare = lookSquare.getSquareInDirection(dir);
    }

    if (lookSquare != null && lookSquare.isEmpty()) {
      return new ReversiMove(this.getColorToMove(), lookSquare.getCoordinate());
    }

    return null;
  }

  @Override
  public ReversiMove parseMove(final String text) throws GameException {
    Coordinate toCoordinate = textPositionToCoordinate(text);

    Square toSquare = getSquareAt(toCoordinate.getX(), toCoordinate.getY());

    return new ReversiMove(getColorToMove(), toSquare.getCoordinate());
  }

  @Override
  public NegaMax<ReversiGame, ReversiMove> getThinker() throws GameException {
    NegaMax<ReversiGame, ReversiMove> ret = new NegaMax<>(this);

    ret.setDiffModifier(3);
    ret.setInitialDiff(1);

    return ret;
  }

  @Override
  public ReversiGame copyGame() throws GameException {
    ReversiGame ret = new ReversiGame();

    ret.getBoard().makeCopyOfPiecesInSquaresFromBoard(this.getBoard());
    ret.setColorToMove(this.getColorToMove());

    return ret;
  }

}
