package com.ludobos1;

public class Piece {
    private final String pieceId; 
    private int x, y;            

    public Piece(String pieceId, int x, int y) {
        if (pieceId == null || pieceId.length() < 2) {
            throw new IllegalArgumentException("Invalid pieceId format. Expected format is 'LetterNumber', e.g., 'A1'.");
        }
        this.pieceId = pieceId;
        this.x = x;
        this.y = y;
    }

    public String getPieceId() {
        return pieceId;
    }

    public char getPlayerId() {
        return pieceId.charAt(0); 
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Pionek{" +
                "ID=" + pieceId +
                ", Pozycja=(" + x + ", " + y + ")" +
                '}';
    }
}


