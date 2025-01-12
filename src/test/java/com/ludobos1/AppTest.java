package com.ludobos1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.List;


/**
 * Unit test for simple App.
 */
public class AppTest {
    private Board board;

    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }
    @BeforeEach
    public void setUp() {
        board = new Board(1);  
    }

    @Test
    public void testGenerateStarBoard() {
        List<int[]> allowedPositions = board.getAllowedPositions();
        assertNotNull(allowedPositions, "Lista dozwolonych pozycji nie powinna być null.");
        assertTrue(allowedPositions.size() > 0, "Lista dozwolonych pozycji powinna zawierać elementy.");
    }

    @Test
    public void testAddPiece() {
        Board board = new Board(1);
    
        board.addPiece("A1", 1, 1);
        
        assertEquals(1, board.getPieces().size());
    }    

    @Test
    public void testMovePiece() {
        board.addPiece("A1", 1, 1);
        assertTrue(board.movePiece("A1", 2, 2), "Ruch pionka A1 na (2, 2) powinien być udany.");
        Piece piece = board.getPieces().get(0);
        assertEquals(2, piece.getX(), "Pozycja X pionka A1 powinna wynosić 2.");
        assertEquals(2, piece.getY(), "Pozycja Y pionka A1 powinna wynosić 2.");
    }

    @Test
public void testMovePieceToOccupiedField() {
    Board board = new Board(1);
    
    board.addPiece("A1", 1, 1);  
    board.addPiece("B1", 2, 2);  

    boolean result = board.movePiece("A1", 2, 2); 


    assertFalse(result);  
}

    

    @Test
    public void testIllegalMove() {
        board.addPiece("A1", 1, 1);
        assertFalse(board.isLegal(1, 1, 10, 10), "Ruch poza dozwolonym obszarem gry powinien być nielegalny.");
    }

    @Test
    void testInitializePlayerPieces() {
        Board board = new Board(1);
        board.generateStarBoard(1);
        List<int[]> sector = board.findFirstFreeSector();
        board.getSectorNumber(sector);
        
        assertNotNull(sector, "Nie znaleziono dostępnego sektora.");
        assertFalse(sector.isEmpty(), "Znaleziony sektor jest pusty.");
        
        board.initializePlayerPieces('A', sector);
        
        List<Piece> pieces = board.getPieces();
        System.out.println("Liczba pionków gracza A: " + pieces.size()); 
        Assertions.assertEquals(10, pieces.size(), "Gracz powinien mieć 10 pionków.");
    }
    
    

    @Test
    public void testFindFirstFreeSector() {
        List<int[]> sector = board.findFirstFreeSector();
        assertNotNull(sector, "Powinien zostać znaleziony pierwszy wolny sektor.");
    }
}
    

    