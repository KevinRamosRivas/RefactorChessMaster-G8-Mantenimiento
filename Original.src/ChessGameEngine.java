import java.awt.Color;
import javax.swing.JOptionPane;
import java.awt.event.MouseEvent;
// -------------------------------------------------------------------------
/**
 * This is the backend behind the Chess game. Handles the turn-based aspects of
 * the game, click events, and determines win/lose conditions.
 *
 * @author Ben Katz (bakatz)
 * @author Myles David II (davidmm2)
 * @author Danielle Bushrow (dbushrow)
 * @version 2010.11.17
 */
public class ChessGameEngine{
    private ChessGamePiece currentPiece;
    private boolean        firstClick;
    private int            currentPlayer;
    private ChessGameBoard board;
    private King           king1;
    private King           king2;
    // ----------------------------------------------------------
    /**
     * Create a new ChessGameEngine object. Accepts a fully-created
     * ChessGameBoard. (i.e. all components rendered)
     *
     * @param board
     *            the reference ChessGameBoard
     */
    public ChessGameEngine( ChessGameBoard board ){
        firstClick = true;
        currentPlayer = 1;
        this.board = board;
        this.king1 = (King)board.getCell( 7, 3 ).getPieceOnSquare();
        this.king2 = (King)board.getCell( 0, 3 ).getPieceOnSquare();
        ( (ChessPanel)board.getParent() ).getGameLog().clearLog();
        ( (ChessPanel)board.getParent() ).getGameLog().addToLog(
            "A new chess "
                + "game has been started. Player 1 (white) will play "
                + "against Player 2 (black). BEGIN!" );
    }
    // ----------------------------------------------------------
    /**
    public void reset(){
        firstClick = true;
        currentPlayer = 1;
        ( (ChessPanel)board.getParent() ).getGraveyard( 1 ).clearGraveyard();
        ( (ChessPanel)board.getParent() ).getGraveyard( 2 ).clearGraveyard();
        ( (ChessPanel)board.getParent() ).getGameBoard().initializeBoard();
        ( (ChessPanel)board.getParent() ).revalidate();
        this.king1 = (King)board.getCell( 7, 3 ).getPieceOnSquare();
        this.king2 = (King)board.getCell( 0, 3 ).getPieceOnSquare();
        ( (ChessPanel)board.getParent() ).getGameLog().clearLog();
        ( (ChessPanel)board.getParent() ).getGameLog().addToLog(
            "A new chess "
                + "game has been started. Player 1 (white) will play "
                + "against Player 2 (black). BEGIN!" );
    }

      */
    /**
     Katherine Ambrocio - Code Smell 1: Se detectó el Code Smell de tipo Long Method y se aplicó Extract Method
     
    */
    
    public void reset() {
        firstClick = true;
        currentPlayer = 1;
    
        ChessPanel chessPanel = (ChessPanel)board.getParent();
        chessPanel.getGraveyard(1).clearGraveyard();
        chessPanel.getGraveyard(2).clearGraveyard();
        chessPanel.getGameBoard().initializeBoard();
        chessPanel.revalidate();
    
        initializeKings();
    
        chessPanel.getGameLog().clearLog();
        addToGameLog(chessPanel, "A new chess game has been started. Player 1 (white) will play against Player 2 (black). BEGIN!");
    }
    
    private void initializeKings() {
        ChessPanel chessPanel = (ChessPanel)board.getParent();
        this.king1 = (King)board.getCell(7, 3).getPieceOnSquare();
        this.king2 = (King)board.getCell(0, 3).getPieceOnSquare();
    }
    
    private void addToGameLog(ChessPanel chessPanel, String message) {
        chessPanel.getGameLog().addToLog(message);
    } 
    /**
     * Switches the turn to be the next player's turn.
     */
    private void nextTurn(){
        currentPlayer = ( currentPlayer == 1 ) ? 2 : 1;
        ( (ChessPanel)board.getParent() ).getGameLog().addToLog(
                "It is now Player " + currentPlayer + "'s turn." );
    }
    // ----------------------------------------------------------
    /**
     * Gets the current player. Used for determining the turn.
     *
     * @return int the current player (1 or 2)
     */
    public int getCurrentPlayer(){
        return currentPlayer;
    }
    private boolean selectedPieceIsValid(){
        if ( currentPiece == null ) // user tried to select an empty square
        {
            return false;
        }
        if ( currentPlayer == 2 ) // black player
        {
            if ( currentPiece.getColorOfPiece() == ChessGamePiece.BLACK ){
                return true;
            }
            return false;
        }
        else
        // white player
        {
            if ( currentPiece.getColorOfPiece() == ChessGamePiece.WHITE ){
                return true;
            }
            return false;
        }
    }
    /**
     * Determines if the requested King is in check.
     *
     * @param checkCurrent
     *            if true, will check if the current king is in check if false,
     *            will check if the other player's king is in check.
     * @return true if the king is in check, false otherwise
     */
    public boolean isKingInCheck( boolean checkCurrent ){
        if ( checkCurrent ){
            if ( currentPlayer == 1 ){
                return king1.isChecked( board );
            }
            return king2.isChecked( board );
        }
        else
        {
            if ( currentPlayer == 2 ){
                return king1.isChecked( board );
            }
            return king2.isChecked( board );
        }
    }
    /**
     * Asks the user if they want to play again - if they don't, the game exits.
     *
     * @param endGameStr
     *            the string to display to the user (i.e. stalemate, checkmate,
     *            etc)
     */
    private void askUserToPlayAgain( String endGameStr ){
        int resp =
            JOptionPane.showConfirmDialog( board.getParent(), endGameStr
                + " Do you want to play again?" );
        if ( resp == JOptionPane.YES_OPTION ){
            reset();
        }
        else
        {
            board.resetBoard( false );
            // System.exit(0);
        }
    }
    /**
     * Determines if the game should continue (i.e. game is in check or is
     * 'normal'). If it should not, the user is asked to play again (see above
     * method).
     */
    private void checkGameConditions(){
        int origPlayer = currentPlayer;
        for ( int i = 0; i < 2; i++ ){
            int gameLostRetVal = determineGameLost();
            if ( gameLostRetVal < 0 ){
                askUserToPlayAgain( "Game over - STALEMATE. You should both go"
                    + " cry in a corner!" );
                return;
            }
            else if ( gameLostRetVal > 0 ){
                askUserToPlayAgain( "Game over - CHECKMATE. " + "Player "
                    + gameLostRetVal + " loses and should go"
                    + " cry in a corner!" );
                return;
            }
            else if ( isKingInCheck( true ) ){
                JOptionPane.showMessageDialog(
                    board.getParent(),
                    "Be careful player " + currentPlayer + ", " +
                    "your king is in check! Your next move must get " +
                    "him out of check or you're screwed.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE );
            }
            currentPlayer = currentPlayer == 1 ? 2 : 1;
            // check the next player's conditions as well.
        }
        currentPlayer = origPlayer;
        nextTurn();
    }
    /**
     * Determines if the game is lost. Returns 1 or 2 for the losing player, -1
     * for stalemate, or 0 for a still valid game.
     *
     * @return int 1 or 2 for the losing play, -1 for stalemate, or 0 for a
     *         still valid game.
     */
    public int determineGameLost(){
        if ( king1.isChecked( board ) && !board.playerHasLegalMoves( 1 ) ) // player 1
        // loss
        {
            return 1;
        }
        if ( king2.isChecked( board ) && !board.playerHasLegalMoves( 2 ) ) // player 2
        // loss
        {
            return 2;
        }
        if ( ( !king1.isChecked( board ) && !board.playerHasLegalMoves( 1 ) )
            || ( !king2.isChecked( board ) && !board.playerHasLegalMoves( 2 ) )
            || ( board.getAllPiecesOfColor( ChessGamePiece.WHITE ).size() == 1 &&
                board.getAllPiecesOfColor( ChessGamePiece.BLACK ).size() == 1 ) ) // stalemate
        {
            return -1;
        }
        return 0; // game is still in play
    }
    // ----------------------------------------------------------
    /**
     * Given a MouseEvent from a user clicking on a square, the appropriate
     * action is determined. Actions include: moving a piece, showing the possi
     * ble moves of a piece, or ending the game after checking game conditions.
     *
     * @param e
     *            the mouse event from the listener
     */
    public void determineActionFromSquareClick( MouseEvent e ){
        BoardSquare squareClicked = (BoardSquare)e.getSource();
        ChessGamePiece pieceOnSquare = squareClicked.getPieceOnSquare();
        board.clearColorsOnBoard();
        // first click - selecting a piece
        if ( firstClick ){
            handleFirstClick( squareClicked );
        }
        // second click - moving a piece
        else if ( pieceOnSquare == null ||
                !pieceOnSquare.equals( currentPiece ) ) // moving
        {
                handleSecondClick( squareClicked );
        }
        // second click - deselecting a piece
        else
        {
            firstClick = true;
        }
    }
    //-------------------------------------------------
    // Tecnica extrac method para el metodo determineActionFromSquareClick
    // se crea el metodo handleFirstClick y el metodo handleSecondClick para
    // que el metodo determineActionFromSquareClick sea mas facil de leer y de entender
    // Author: Kevin Ramos Rivas
    //-------------------------------------------------
    /**
     * Handles the first click of a user. Determines if the piece is valid, and
     * if so, shows the legal moves of the piece.
     *
     * @param squareClicked
     *            the square that was clicked
     */
    private void handleFirstClick( BoardSquare squareClicked ){
        currentPiece = squareClicked.getPieceOnSquare();
        if ( selectedPieceIsValid() ){
            currentPiece.showLegalMoves( board );
            squareClicked.setBackground( Color.GREEN );
            firstClick = false;
        }
        else
        {
            if ( currentPiece != null ){
                showErrorMessage(
                    "You tried to pick up a piece that isn't yours! "
                        + "Get some glasses and pick a valid piece.",
                    "Illegal move" );
            }
            else
            {
                showErrorMessage(
                    "You tried to pick up an empty square! "
                        + "Get some glasses and pick a valid square.",
                    "Illegal move" );
            }
        }
    }
    /**
     * Handles the second click of a user. Determines if the move is valid, and
     * if so, moves the piece and checks game conditions.
     *
     * @param squareClicked
     *            the square that was clicked
     */
    private void handleSecondClick( BoardSquare squareClicked ){
        int row = squareClicked.getRow();
        int col = squareClicked.getColumn();
        boolean moveSuccessful =currentPiece.move(board,row,col);
        if ( moveSuccessful ){
            checkGameConditions();
        }
        else
        {
            showErrorMessage(
                "You can't move the piece to row " + row + ", column " + col
                    + "!",
                "Illegal move" );
        }
        firstClick = true;
    }

    private void showErrorMessage( String message, String title ){
        JOptionPane.showMessageDialog(
            board.getParent(),
            message,
            title,
            JOptionPane.ERROR_MESSAGE );
    }

}
