package com.ludobos1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for Board class.
 */
public class AppTest {
    private Board board;

    @BeforeEach
    public void setUp() {
        board = new Board(1);  // Przykładowy typ gry
        board.generateStarBoard(1); // Generowanie planszy dla testów
    }

    @Test
    public void testGenerateStarBoard() {
        List<int[]> allowedPositions = board.getAllowedPositions();
        assertNotNull(allowedPositions, "Lista dozwolonych pozycji nie powinna być null.");
        assertTrue(allowedPositions.size() > 0, "Lista dozwolonych pozycji powinna zawierać elementy.");
    }

    @Test
    public void testAddPiece() {
        board.addPiece("A1", 1, 1);
        assertEquals(1, board.getPieces().size(), "Powinien zostać dodany jeden pionek.");
    }

    @Test
    public void testMovePiece() {
        board.addPiece("A1", 1, 1);
        boolean moved = board.movePiece("A1", 2, 2);

        assertTrue(moved, "Ruch pionka A1 na (2, 2) powinien być udany.");
        Piece piece = board.getPieces().get(0);
        assertEquals(2, piece.getX(), "Pozycja X pionka A1 powinna wynosić 2.");
        assertEquals(2, piece.getY(), "Pozycja Y pionka A1 powinna wynosić 2.");
    }

    @Test
    public void testMovePieceToOccupiedField() {
        board.addPiece("A1", 1, 1);
        board.addPiece("B1", 2, 2);

        boolean result = board.movePiece("A1", 2, 2);

        assertFalse(result, "Ruch na zajęte pole powinien być nieudany.");
    }

    @Test
public void testMoveByJumping() {
    // Ustawienie pionków na planszy
    board.addPiece("A1", 1, 1); // Pionek, który ma wykonać skok
    board.addPiece("B1", 2, 2); // Pionek, przez który A1 ma skoczyć

    // Próba wykonania skoku
    boolean result = board.movePiece("A1", 3, 3);

    // Sprawdzanie wyniku skoku
    assertTrue(result, "Ruch przez skok powinien być dozwolony.");
    
    // Weryfikacja nowej pozycji pionka
    Piece movedPiece = board.getPieces().stream()
        .filter(p -> p.getPieceId().equals("A1"))
        .findFirst()
        .orElse(null);

    assertNotNull(movedPiece, "Pionek A1 powinien istnieć.");
    assertEquals(3, movedPiece.getX(), "Pionek A1 powinien znajdować się na pozycji X=3.");
    assertEquals(3, movedPiece.getY(), "Pionek A1 powinien znajdować się na pozycji Y=3.");
}
    
    public void testMoveByMultipleJumping() {
        // Ustawienie pionków na planszy
        board.addPiece("A1", 1, 1);
        board.addPiece("B1", 2, 2);
        board.addPiece("B2",4,4);

        boolean result = board.movePiece("A1", 5, 5);

        assertTrue(result, "Ruch przez skok powinien być dozwolony.");
    
        Piece movedPiece = board.getPieces().stream()
            .filter(p -> p.getPieceId().equals("A1"))
            .findFirst()
            .orElse(null);

        assertNotNull(movedPiece, "Pionek A1 powinien istnieć.");
        assertEquals(5, movedPiece.getX(), "Pionek A1 powinien znajdować się na pozycji X=3.");
        assertEquals(5, movedPiece.getY(), "Pionek A1 powinien znajdować się na pozycji Y=3.");
    }

    @Test
    public void testIllegalMove() {
        board.addPiece("A1", 0, 0);
        boolean result = board.isLegal("A1", 10, 10);

        assertFalse(result, "Ruch poza dozwolonym obszarem gry powinien być nielegalny.");
    }

    @Test

    @Test
    public void testInitializePlayerPieces() {
        board.initializePlayerPieces('A');

        List<Piece> pieces = board.getPieces();
        assertEquals(10, pieces.size(), "Gracz powinien mieć 10 pionków na początku gry.");

        for (Piece piece : pieces) {
            assertEquals('A', piece.getPieceId().charAt(0), "Każdy pionek powinien należeć do gracza A.");
        }
    }

    @Test
    public void testIfWon() {
        board.initializePlayerPieces('A');
        List<int[]> oppositeSector = board.getOppositeSector('A');

        // Przenosimy wszystkie pionki gracza A do przeciwnego sektora
        int index = 0;
        for (Piece piece : board.getPieces()) {
            int[] position = oppositeSector.get(index);
            piece.setPosition(position[0], position[1]);
            index++;
        }

        assertTrue(board.ifWon('A'), "Gracz A powinien wygrać, gdy wszystkie pionki są w przeciwnym sektorze.");
    }

    @Test
    public void testIsPieceInOppositeSector() {
        board.initializePlayerPieces('A');
        List<int[]> oppositeSector = board.getOppositeSector('A');

        // Przenosimy jeden pionek do przeciwnego sektora
        int[] position = oppositeSector.get(0);
        board.addPiece("A1", position[0], position[1]);

        assertTrue(board.isPieceInOppositeSector("A1"), "Pionek A1 powinien być w przeciwnym sektorze.");
    }
}
    

    