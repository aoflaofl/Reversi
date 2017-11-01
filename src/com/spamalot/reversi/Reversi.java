package com.spamalot.reversi;

import com.spamalot.boardgame.GameController;
import com.spamalot.boardgame.GameException;

/**
 * Game of Reversi.
 * 
 * @author gej
 *
 */
public final class Reversi {
  /**
   * Construct nothing.
   */
  private Reversi() {
  }

  /**
   * Start here.
   * 
   * @param args
   *          Arguments to the program
   * @throws GameException
   *           If something goes wrong.
   */
  public static void main(final String[] args) throws GameException {
    System.out.println("Reversi Game\nGene Johannsen");
    ReversiGame reversiGame = new ReversiGame();

    GameController<ReversiGame, ReversiMove> game = new GameController<>(reversiGame);
    game.control();
  }

}
