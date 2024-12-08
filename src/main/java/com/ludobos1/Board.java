import java.util.ArrayList;
import java.util.List;

public class Board {
    private final List<Piece> pieces = new ArrayList<>();
    private int fromX, fromY, toX, toY;
    public Board(int fromX, int fromY, int toX, int toY) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
    }

    /**
     * Dodaje pionek na planszę.
     */
    public void addPiece(String pieceId, int x, int y) {
        pieces.add(new Piece(pieceId, x, y));
    }

    /**
     * Przesuwa pionek na nowe współrzędne.
     */
    public void movePiece(String pieceId, int toX, int toY) {


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
