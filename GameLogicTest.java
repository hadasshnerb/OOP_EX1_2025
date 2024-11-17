import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for the GameLogic class.
 */
class GameLogicTest {

    private GameLogic gameLogic;

    @BeforeEach
    void setUp() {
        gameLogic = new GameLogic();
    }

    // --- Game Setup and Initialization Tests ---

    @Test
    void testInitialBoardSetup() {
        // Verify the initial setup of the game board
        assertNotNull(gameLogic.getDiscAtPosition(new Position(3, 3)));
        assertNotNull(gameLogic.getDiscAtPosition(new Position(4, 4)));
        assertNotNull(gameLogic.getDiscAtPosition(new Position(3, 4)));
        assertNotNull(gameLogic.getDiscAtPosition(new Position(4, 3)));
    }

    @Test
    void testResetRestoresInitialState() {
        // Make a move, then reset the game and validate the board is reset
        gameLogic.locate_disc(new Position(4, 2), new SimpleDisc(gameLogic.getFirstPlayer()));
        gameLogic.reset();
        assertNull(gameLogic.getDiscAtPosition(new Position(4, 2)));
        assertNotNull(gameLogic.getDiscAtPosition(new Position(3, 3)));
        assertNotNull(gameLogic.getDiscAtPosition(new Position(4, 4)));
    }

    @Test
    void testBoardSize() {
        // Check the board size
        assertEquals(8, gameLogic.getBoardSize());
    }

    // --- Disc Placement Tests ---

    @Test
    void testPlaceValidDisc() {
        Position validPosition = new Position(5, 3);
        assertTrue(gameLogic.locate_disc(validPosition, new SimpleDisc(gameLogic.getFirstPlayer())));
    }

    @Test
    void testPlaceDiscOnOccupiedPosition() {
        Position occupiedPosition = new Position(3, 3);
        assertFalse(gameLogic.locate_disc(occupiedPosition, new SimpleDisc(gameLogic.getSecondPlayer())));
    }

    @Test
    void testPlaceDiscOutOfBounds() {
        assertFalse(gameLogic.locate_disc(new Position(-1, -1), new SimpleDisc(gameLogic.getFirstPlayer())));
        assertFalse(gameLogic.locate_disc(new Position(8, 8), new SimpleDisc(gameLogic.getFirstPlayer())));
    }

    // --- Valid Moves Tests ---

    @Test
    void testValidMovesExistAtStart() {
        List<Position> validMoves = gameLogic.ValidMoves();
        assertFalse(validMoves.isEmpty());
    }

    @Test
    void testValidMovesAfterReset() {
        gameLogic.reset();
        List<Position> validMoves = gameLogic.ValidMoves();
        assertFalse(validMoves.isEmpty());
    }

    @Test
    void testNoValidMovesOnFullBoard() {
        for (int row = 0; row < gameLogic.getBoardSize(); row++) {
            for (int col = 0; col < gameLogic.getBoardSize(); col++) {
                gameLogic.locate_disc(new Position(row, col), new SimpleDisc(gameLogic.getFirstPlayer()));
            }
        }
        assertTrue(gameLogic.ValidMoves().isEmpty());
    }

    @Test
    void testValidMovesNearEdges() {
        // Start by ensuring no move is valid at (0, 0) since it doesn't flip any discs
        Position edgePosition = new Position(0, 0);
        assertFalse(gameLogic.locate_disc(edgePosition, new SimpleDisc(gameLogic.getFirstPlayer())),
                "The first move at (0, 0) should be invalid as it doesn't flip any discs.");

        // Validate that there are valid moves near the edges at the start of the game
        List<Position> validMoves = gameLogic.ValidMoves();
        System.out.println(gameLogic.isFirstPlayerTurn());

        // Positions (0, 1) and (1, 0) are not valid at the start due to the initial board setup
        assertFalse(validMoves.contains(new Position(0, 1)), "Position (0, 1) should not be valid at the start.");
        assertFalse(validMoves.contains(new Position(1, 0)), "Position (1, 0) should not be valid at the start.");

        // Validate positions closer to the initial discs
        assertTrue(validMoves.contains(new Position(2, 4)), "Position (2, 3) should be a valid move.");
        assertTrue(validMoves.contains(new Position(4, 2)), "Position (3, 2) should be a valid move.");
    }

    // --- Special Disc Tests ---

    @Test
    void testPlaceUnflippableDisc() {
        Position position = new Position(2, 4);
        assertTrue(gameLogic.locate_disc(position, new UnflippableDisc(gameLogic.getFirstPlayer())));
        assertEquals(1, gameLogic.getFirstPlayer().getNumber_of_unflippedable());
    }

    @Test
    void testPlaceBombDisc() {
        Position bombPosition = new Position(4, 2);
        assertTrue(gameLogic.locate_disc(bombPosition, new BombDisc(gameLogic.getFirstPlayer())));
        Position affectedPosition = new Position(4, 3);
        assertEquals(gameLogic.getFirstPlayer(), gameLogic.getDiscAtPosition(affectedPosition).getOwner());
    }

    @Test
    void testSpecialDiscCountRestorationAfterUndo() {
        Position bombPosition = new Position(4, 2);
        gameLogic.locate_disc(bombPosition, new BombDisc(gameLogic.getFirstPlayer()));
        gameLogic.undoLastMove();
        assertEquals(3, gameLogic.getFirstPlayer().getNumber_of_bombs());
    }

    // --- Undo Tests ---

    @Test
    void testUndoLastMove() {
        Position move = new Position(4, 2);
        gameLogic.locate_disc(move, new SimpleDisc(gameLogic.getFirstPlayer()));
        gameLogic.undoLastMove();
        assertNull(gameLogic.getDiscAtPosition(move));
        assertTrue(gameLogic.isFirstPlayerTurn());
    }

    @Test
    void testUndoMultipleMoves() {
        Position move1 = new Position(4, 2);
        Position move2 = new Position(5, 2);
        gameLogic.locate_disc(move1, new SimpleDisc(gameLogic.getFirstPlayer()));
        gameLogic.locate_disc(move2, new SimpleDisc(gameLogic.getSecondPlayer()));
        gameLogic.undoLastMove();
        assertNull(gameLogic.getDiscAtPosition(move2));
        assertNotNull(gameLogic.getDiscAtPosition(move1));
        gameLogic.undoLastMove();
        assertNull(gameLogic.getDiscAtPosition(move1));
    }

    // --- Game State Tests ---

    @Test
    void testGameNotFinishedAtStart() {
        assertFalse(gameLogic.isGameFinished());
    }

    @Test
    void testGameFinishedOnFullBoard() {
        for (int row = 0; row < gameLogic.getBoardSize(); row++) {
            for (int col = 0; col < gameLogic.getBoardSize(); col++) {
                gameLogic.locate_disc(new Position(row, col), new SimpleDisc(gameLogic.getFirstPlayer()));
            }
        }
        assertTrue(gameLogic.isGameFinished());
    }
}
