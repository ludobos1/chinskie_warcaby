import java.util.ArrayList;
import java.util.List;

public class Board {
    private final List<Piece> pieces = new ArrayList<>();

    /**
     * Dodaje pionek na planszę.
     */
    public void addPiece(String pieceId, int x, int y) {
        pieces.add(new Piece(pieceId, x, y));
    }

    /**
     * Przesuwa pionek na nowe współrzędne.
     */
    public boolean movePiece(String pieceId, int newX, int newY) {
                    piece.setPosition(newX, newY);
                    //TODO:sprawdzenie legalnosci

    }

    @Override
    public String toString() {
        StringBuilder boardRepresentation = new StringBuilder("Stan planszy:\n");
        for (Piece piece : pieces) {
            boardRepresentation.append(piece).append("\n");
        }
        return boardRepresentation.toString();
    }
}
