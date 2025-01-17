package com.ludobos1;

/**
 * Reprezentuje pionek w grze.
 * Klasa przechowuje informacje o unikalnym ID pionka oraz jego pozycji na planszy.
 */
public class Piece {
    private final String pieceId; 
    private int x, y;

    /**
     * Konstruktor klasy Piece.
     * Tworzy nowy pionek z określonym ID i pozycją na planszy.
     *
     * @param pieceId Unikalne ID pionka.
     * @param x Pozycja pionka w osi X.
     * @param y Pozycja pionka w osi Y.
     */
    public Piece(String pieceId, int x, int y) {
        this.pieceId = pieceId;
        this.x = x;
        this.y = y;
    }

    /**
     * Zwraca unikalne ID pionka.
     *
     * @return ID pionka.
     */
    public String getPieceId() {
        return pieceId;
    }

    /**
     * Zwraca identyfikator gracza przypisanego do tego pionka.
     * ID gracza jest pierwszym znakiem w ID pionka.
     *
     * @return ID gracza przypisanego do pionka.
     */
    public char getPlayerId() {
        return pieceId.charAt(0); 
    }

    /**
     * Zwraca pozycję pionka w osi X.
     *
     * @return Pozycja pionka w osi X.
     */
    public int getX() {
        return x;
    }

    /**
     * Zwraca pozycję pionka w osi Y.
     *
     * @return Pozycja pionka w osi Y.
     */
    public int getY() {
        return y;
    }

    /**
     * Ustawia nową pozycję pionka na planszy.
     *
     * @param x Nowa pozycja pionka w osi X.
     * @param y Nowa pozycja pionka w osi Y.
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Zwraca tekstową reprezentację pionka.
     *
     * @return Reprezentacja pionka w formacie "Pionek{ID=ID, Pozycja=(x, y)}".
     */
    @Override
    public String toString() {
        return "Pionek{" +
                "ID=" + pieceId +
                ", Pozycja=(" + x + ", " + y + ")" +
                '}';
    }
}
