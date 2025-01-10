package com.ludobos1;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


public class Board {
    private char playerId;
    private final List<int[]> positions = new ArrayList<>();
    private final List<int[]> p1 = new ArrayList<>();
    private final List<int[]> p2 = new ArrayList<>();
    private final List<int[]> p3 = new ArrayList<>();
    private final List<int[]> p4 = new ArrayList<>();
    private final List<int[]> p5 = new ArrayList<>();
    private final List<int[]> p6 = new ArrayList<>();
    private final List<int[]> allowedPositions;
    private List<Piece> pieces = new ArrayList<>();
    private final Map<Character, String> playerSectors = new HashMap<>(); // Mapowanie ID gracza do sektora


    public Board(int sideLength) {
        if (sideLength < 1) {
            throw new IllegalArgumentException("Długość boku musi być większa niż 0");
        }
        allowedPositions = generateStarBoard(sideLength);
    }

    public List<int[]> generateStarBoard(int sideLength) {
        // Górna i środkowa część sześciokąta
        int rowWidth = sideLength;
        int startX = sideLength;
        for (int y = sideLength; y < sideLength * 2; y++) {
            for (int x = startX; x < startX + rowWidth; x++) {
                positions.add(new int[]{x, y});
            }
            startX--;
            rowWidth++;
        }

        // Dolna część sześciokąta
        rowWidth = sideLength;
        startX = sideLength;
        for (int y = sideLength * 3 - 2; y > sideLength * 2 - 1; y--) {
            for (int x = startX; x < startX + rowWidth; x++) {
                positions.add(new int[]{x, y});
            }
            startX--;
            rowWidth++;
        }

        // Górny róg
        for (int y = 1; y < sideLength; y++) {
            for (int x = 1; x <= y; x++) {
                positions.add(new int[]{x, y});
                p1.add(new int[]{x, y});
            }
        }

        // Dolny róg
        for (int y = (sideLength - 1) * 4 + 1; y > (sideLength - 1) * 3 + 1; y--) {
            for (int x = 1; x <= (sideLength - 1) * 4 + 2 - y; x++) {
                positions.add(new int[]{x, y});
                p4.add(new int[]{x, y});
            }
        }

        // Lewy górny róg
        for (int y = sideLength; y < sideLength * 2 - 1; y++) {
            for (int x = 1; x < sideLength * 2 - 1 - y; x++) {
                positions.add(new int[]{x, y});
                p6.add(new int[]{x, y});
            }
        }

        // Prawy górny róg
        for (int y = sideLength; y < sideLength * 2 - 1; y++) {
            for (int x = sideLength * 2; x < sideLength * 4 - 2 - y; x++) {
                positions.add(new int[]{x, y});
                p2.add(new int[]{x, y});
            }
        }

        // Lewy dolny róg
        for (int y = sideLength * 2; y < sideLength * 3 - 1; y++) {
            for (int x = 1; x <= y + 1 - sideLength * 2; x++) {
                positions.add(new int[]{x, y});
                p5.add(new int[]{x, y});
            }
        }

        // Prawy dolny róg
        for (int y = sideLength * 2; y < sideLength * 3 - 1; y++) {
            for (int x = sideLength * 2; x < y + 1; x++) {
                positions.add(new int[]{x, y});
                p3.add(new int[]{x, y});
            }
        }

        return positions;
    }
    public List<int[]> findFirstFreeSector() {

        List<List<int[]>> sectors = List.of(p1, p2, p3, p4, p5, p6);
    

        for (List<int[]> sector : sectors) {
            boolean isSectorFree = true;
    

            for (int[] position : sector) {
                if (!isFieldFree(position[0], position[1])) { 
                    isSectorFree = false;
                    break; 
                }
            }
    
            if (isSectorFree) {
                return sector;
            }
        }
    
        return null;
    }
    

    public void initializePlayerPieces(char playerId, List<int[]> sector) {
        if (sector == null || sector.isEmpty()) {
            System.out.println("Brak dostępnych pól w sektorze dla gracza " + playerId);
            return;
        }

        int sectorNumber = getSectorNumber(sector);
        String assignedSector="p1";

        if (sectorNumber < 4) {
            assignedSector = "p" + ((sectorNumber + 3));
        } else if (sectorNumber > 4) {
          assignedSector = "p" + ((sectorNumber - 3));
        } else {
            assignedSector = "p1";
        }


        playerSectors.put(playerId, assignedSector);

        int pieceCounter = 1; 
        for (int[] position : sector) {
            String pieceId = playerId + Integer.toString(pieceCounter);
            addPiece(pieceId, position[0], position[1]);
            pieceCounter++;
        }

        System.out.println("Pionki dla gracza " + playerId + " zostały rozmieszczone w sektorze " + assignedSector + ".");
    }

    public int getSectorNumber(List<int[]> sector) {
        if (sector == p1) return 1;
        if (sector == p2) return 2;
        if (sector == p3) return 3;
        if (sector == p4) return 4;
        if (sector == p5) return 5;
        if (sector == p6) return 6;
        return -1; // Nieznany sektor
    }


    public void addPiece(String pieceId, int x, int y) {
        if (isWithinBoard(x, y) && isFieldFree(x, y)) {
            pieces.add(new Piece(pieceId, x, y));
        } else {
            System.out.println("Pole (" + x + ", " + y + ") jest już zajęte lub nie jest dozwolone na tej planszy.");
        }
    }
   

    public boolean movePiece(String pieceId, int newX, int newY) {
        for (Piece piece : pieces) {
            if (piece.getPieceId().equals(pieceId)) {
                if (isLegal(piece.getX(), piece.getY(), newX, newY)) {
                    piece.setPosition(newX, newY);  
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;  // Pionek o takim ID nie istnieje
    }
    

    public boolean isFieldFree(int x, int y) {
        for (Piece piece : pieces) {
            if (piece.getX() == x && piece.getY() == y) {
                return false;
            }
        }
        return true;
    }

    public boolean isLegal(int oldX, int oldY, int newX, int newY) {
        if (!isWithinBoard(oldX, oldY) || !isWithinBoard(newX, newY)) {
            System.out.println("Nielegalny ruch – współrzędne poza dozwolonym obszarem gry.");
            return false;
        }

        Piece piece = null;
        for (Piece p : pieces) {
            if (p.getX() == oldX && p.getY() == oldY) {
                piece = p;
                break;
            }
        }

        if (piece == null) {
            System.out.println("Brak pionka na polu startowym (" + oldX + ", " + oldY + ").");
            return false;
        }

        if (!isFieldFree(newX, newY)) {
            System.out.println("Pole docelowe (" + newX + ", " + newY + ") jest zajęte.");
            return false;
        }

        int deltaX = Math.abs(newX - oldX);
        int deltaY = Math.abs(newY - oldY);

        if (deltaX <= 1 && deltaY <= 1) {
            return true;
        }

        if (deltaX == 2 || deltaY == 2) {
            int middleX = (oldX + newX) / 2;
            int middleY = (oldY + newY) / 2;

            if (!isFieldFree(middleX, middleY)) {
                return true;
            } else {
                System.out.println("Nielegalny skok – brak pionka na polu (" + middleX + ", " + middleY + ").");
                return false;
            }
        }
       

        System.out.println("Nielegalny ruch – niedozwolona odległość.");
        return false;
    }

    private boolean isWithinBoard(int x, int y) {
        for (int[] position : allowedPositions) {
            if (position[0] == x && position[1] == y) {
                return true;
            }
        }
        return false;
    }

    public List<int[]> getAllowedPositions() {
        return allowedPositions;
    }

    public List<Piece> getPieces() {
        return new ArrayList<>(pieces); 
    }
    
    

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int[] pos : allowedPositions) {
            sb.append(pos[0]).append(",").append(pos[1]).append(" ");
        }
        return sb.toString();
    }
}
