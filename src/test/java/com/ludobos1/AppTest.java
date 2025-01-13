package com.ludobos1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.beans.Transient;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for Board class.
 */
public class AppTest {
    private Board board;

    @BeforeEach
    public void setUp() {
        board = new BoardBuilder().setVariant(1).setPlayerNum(6).build();  // Przykładowy typ gry
        board.starBoard = board.generateStarBoard(); // Generowanie planszy dla testów
    }

    @Test
    public void testGenerateStarBoard() {
        List<int[]> allowedPositions = board.getAllowedPositions();
        assertNotNull(allowedPositions, "Lista dozwolonych pozycji nie powinna być null.");
        assertTrue(allowedPositions.size() > 0, "Lista dozwolonych pozycji powinna zawierać elementy.");
    }

    @Test
    public void testAddPiece() {
        board.addPiece("A1", 12, 16);
        assertEquals(1, board.getPieces().size(), "Powinien zostać dodany jeden pionek.");
    }

    @Test
    public void testMovePiece() {
        board.addPiece("A1", 9, 10);
        boolean moved = board.movePiece("A1",10, 12);

        assertTrue(moved, "Ruch pionka A1 na (10, 12) powinien być udany.");
        Piece piece = board.getPieces().get(0);
        assertEquals(10, piece.getX(), "Pozycja X pionka A1 powinna wynosić 10.");
        assertEquals(12, piece.getY(), "Pozycja Y pionka A1 powinna wynosić 12.");
    }

    @Test
    public void testMovePieceToOccupiedField() {
        board.addPiece("A1", 9, 10);
        board.addPiece("B1", 10, 12);

        boolean result = board.movePiece("A1", 10, 12);

        assertFalse(result, "Ruch na zajęte pole powinien być nieudany.");
    }

    @Test
public void testMoveByJumping() {
    // Ustawienie pionków na planszy
    board.addPiece("A1", 9, 10); // Pionek, który ma wykonać skok
    board.addPiece("B1", 10, 12); // Pionek, przez który A1 ma skoczyć

    // Próba wykonania skoku
    boolean result = board.movePiece("A1", 11, 14);

    // Sprawdzanie wyniku skoku
    assertTrue(result, "Ruch przez skok powinien być dozwolony.");
    
    // Weryfikacja nowej pozycji pionka
    Piece movedPiece = board.getPieces().stream()
        .filter(p -> p.getPieceId().equals("A1"))
        .findFirst()
        .orElse(null);

    assertNotNull(movedPiece, "Pionek A1 powinien istnieć.");
    assertEquals(11, movedPiece.getX(), "Pionek A1 powinien znajdować się na pozycji X=11.");
    assertEquals(14, movedPiece.getY(), "Pionek A1 powinien znajdować się na pozycji Y=14.");
}
    
    public void testMoveByMultipleJumping() {
        // Ustawienie pionków na planszy
        board.addPiece("A1", 9, 10);
        board.addPiece("B1", 10, 12);
        board.addPiece("B2",12,16);

        boolean result = board.movePiece("A1", 13, 18);

        assertTrue(result, "Ruch przez skok powinien być dozwolony.");
    
        Piece movedPiece = board.getPieces().stream()
            .filter(p -> p.getPieceId().equals("A1"))
            .findFirst()
            .orElse(null);

        assertNotNull(movedPiece, "Pionek A1 powinien istnieć.");
        assertEquals(13, movedPiece.getX(), "Pionek A1 powinien znajdować się na pozycji X=13.");
        assertEquals(18, movedPiece.getY(), "Pionek A1 powinien znajdować się na pozycji Y=18.");
    }

    @Test
    public void testIllegalMove() {
        board.addPiece("A1", 9, 10);
        boolean result = board.isLegal("A1", 18, 24,1);

        assertFalse(result, "Ruch poza dozwolonym obszarem gry powinien być nielegalny.");
    }


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

    @Test
    public void testFastJumping(){
        board.addPiece("A1",8,24);
        board.addPiece("B1", 12,24);
      assertTrue(board.isLegal("A1",16,24,2), "Taki skok powinien być możliwy");
    }

    @Test
    public void testFastDiagonalJumpig(){
        board.addPiece("A1",8,24);
        board.addPiece("B1", 10,20);
      assertTrue(board.isLegal("A1",12,16,2), "Taki skok powinien być możliwy");
    }
    

    @Test
    public void testCountFreeSpacesInLine(){
        assertEquals(1,board.countFreeSpacesInLine(8, 24, 12, 24),"Między tymi polami jest jedno pole");
    }

    @Test
    public void testcanSkipWithSymmetryRule(){
        board.addPiece("A1", 12, 24);
        assertTrue(board.canSkipWithSymmetryRule(8,24,16,24),"Oba pola dzieli taka sama liczba wolnych pól");

    }
}
    

    