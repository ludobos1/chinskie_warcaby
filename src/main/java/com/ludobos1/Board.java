package com.ludobos1;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


public class Board {
    private char playerId;
    private final List<int[]> p1 = new ArrayList<>();
    private final List<int[]> p2 = new ArrayList<>();
    private final List<int[]> p3 = new ArrayList<>();
    private final List<int[]> p4 = new ArrayList<>();
    private final List<int[]> p5 = new ArrayList<>();
    private final List<int[]> p6 = new ArrayList<>();
    private final List<int[]> allowedPositions= new ArrayList<>();
    private List<Piece> pieces = new ArrayList<>();
    private final Map<Character, String> playerSectors = new HashMap<>(); // Mapowanie ID gracza do sektora
    private final int gameType;
    private int sideLength=1;
    private int numberOfPlayers;

    public Board(int gameType, int numberOfPlayers) {
        this.gameType=gameType;
        this.numberOfPlayers=numberOfPlayers;
    }



    public List<int[]> generateStarBoard() {
        if(gameType==1) sideLength=5; //np. jesli gameType bedzie 1 to normalna plansza, no tu jeszcze ogarniemy jakie plansze w ogóle chcemy
        int startX;
        int startY;
        int endX;

        // Górna i środkowa część sześciokąta
        startX = (sideLength-1)*2;
        startY=(sideLength-1)*2;
        endX=startX + sideLength*2-2;
        for (int y = startY; y <= startY+sideLength*2; y=y+2) {
            for (int x = startX; x <= endX; x=x+2) {
                allowedPositions.add(new int[]{x, y});
            }
            startX--;
            endX++;
        }

        // Dolna część sześciokąta
        startX = (sideLength-1)*2;
        startY=(sideLength-1)*4+sideLength*2-2;
        endX=(sideLength-1)*2+sideLength*2-2;
        for (int y = startY; y > startY-sideLength*2+2; y=y-2) {
            for (int x = startX; x <= endX; x=x+2) {
                allowedPositions.add(new int[]{x, y});
            }
            startX--;
            endX++;
        }

        // Górny róg
        startX=sideLength*2+sideLength/2;
        startY=0;
        endX=sideLength*2+sideLength/2+1;
        for (int y = startY; y < sideLength+sideLength/2; y=y+2) {
            for (int x = startX; x<endX ; x=x+2){
                allowedPositions.add(new int[]{x, y});
                p1.add(new int[]{x, y});
            }
            endX=endX+1;
            startX--;
        }

        // Dolny róg
        startX=sideLength*2+sideLength/2;
        startY=((sideLength-1)*3+sideLength-1)*2;
        endX=sideLength*2+sideLength/2+1;
        for (int y = startY ; y >startY-(sideLength-1)*2 ; y=y-2) {
            for (int x = startX; x < endX; x=x+2) {
                allowedPositions.add(new int[]{x, y});
                p4.add(new int[]{x, y});
            }
            endX=endX+1;
            startX--;
        }

        // Lewy górny róg
        startX=0 ;
        startY=sideLength+sideLength/2+1;
        endX=(sideLength-1)*2-2;
        for (int y = startY; y <=(sideLength+sideLength/2)*2; y=y+2) {
            for (int x = startX; x <= endX; x=x+2) {
                allowedPositions.add(new int[]{x, y});
                p6.add(new int[]{x, y});
            }
            endX--;
            startX++;
        }

        // Prawy górny róg
        startX=(sideLength-1)*2+sideLength*2;
        startY=(sideLength-1)*2;
        endX=startX+(sideLength-1)*2-2;
        for (int y = startY; y < startY+(sideLength-1)*2; y=y+2) {
            for (int x = startX; x <= endX; x=x+2) {
                allowedPositions.add(new int[]{x, y});
                p2.add(new int[]{x, y});
            }
            endX--;
            startX++;
        }

        // Lewy dolny róg
        startX=0 ;
        startY=(sideLength-1)*6;
        endX=(sideLength-1)*2-2;
        for (int y = startY; y >startY-(sideLength-1)*2; y=y-2) {
            for (int x = startX; x <= endX; x=x+2) {
                allowedPositions.add(new int[]{x, y});
                p5.add(new int[]{x, y});
            }
            endX--;
            startX++;
        }

        // Prawy dolny róg
        startX=(sideLength-1)*2+sideLength*2 ;
        startY=(sideLength-1)*6;
        endX=startX+(sideLength-1)*2-2;
        for (int y = startY; y >startY-(sideLength-1)*2; y=y-2) {
            for (int x = startX; x <= endX; x=x+2) {
                allowedPositions.add(new int[]{x, y});
                p2.add(new int[]{x, y});
            }
            endX--;
            startX++;
        }

        return allowedPositions;
    }
    
    public void initializeAllPlayersPieces(int numberOfPlayers) {
        if (numberOfPlayers == 2) {
            // Dla 2 graczy: p1 (A), p4 (D)
            initializePlayerPieces('A');
            initializePlayerPieces('D');
        } else if (numberOfPlayers == 4) {
            // Dla 4 graczy: p1 (A), p2 (B), p4 (D), p5 (E)
            initializePlayerPieces('A');
            initializePlayerPieces('B');
            initializePlayerPieces('D');
            initializePlayerPieces('E');
        } else if (numberOfPlayers == 6) {
            // Dla 6 graczy: wszystkie sektory
            initializePlayerPieces('A');
            initializePlayerPieces('B');
            initializePlayerPieces('C');
            initializePlayerPieces('D');
            initializePlayerPieces('E');
            initializePlayerPieces('F');
        } else {
            System.out.println("Nieprawidłowa liczba graczy. Obsługiwane są tylko wartości 2, 4 lub 6.");
        }
    }
    

    public void initializePlayerPieces(char playerId) {
    List<int[]> sector = getSector(playerId);

    if (sector == null || sector.isEmpty()) {
        System.out.println("Brak dostępnych pól w sektorze dla gracza " + playerId);
        return;
    }

    int pieceCounter = 1; 
    for (int[] position : sector) {
        String pieceId = playerId + Integer.toString(pieceCounter);
        addPiece(pieceId, position[0], position[1]);
        pieceCounter++;
    }

    System.out.println("Pionki dla gracza " + playerId + " zostały rozmieszczone w sektorze " + playerSectors.get(playerId) + ".");
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
                if (isLegal(pieceId, newX, newY)) {
                    piece.setPosition(newX, newY);  
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;  // Pionek o takim ID nie istnieje
    }


    public boolean ifWon(char playerID) {
        // Pobierz przeciwny sektor dla gracza
        List<int[]> oppositeSector = getOppositeSector(playerID);
        
        // Sprawdź każdy pionek gracza
        for (Piece piece : pieces) {
            if (piece.getPlayerId() == playerID) {
                // Jeśli pionek nie znajduje się w przeciwnym sektorze, gra nie jest wygrana
                boolean isInOppositeSector = false;
                for (int[] position : oppositeSector) {
                    if (position[0] == piece.getX() && position[1] == piece.getY()) {
                        isInOppositeSector = true;
                        break;
                    }
                }
                if (!isInOppositeSector) {
                    // Przynajmniej jeden pionek nie jest w przeciwnym sektorze, więc nie wygrana
                    return false;
                }
            }
        }
    
        // Jeśli wszystkie pionki gracza są w przeciwnym sektorze, gracz wygrał
        return true;
    }




    public List<int[]> getSector(char playerId) {
        List<List<int[]>> sectors = List.of(p1, p2, p3, p4, p5, p6);

        // Przypisanie sektora na podstawie ID gracza
        int sectorIndex = (playerId - 'A') % sectors.size(); // A -> p1, B -> p2, ...
        List<int[]> sector = sectors.get(sectorIndex);

        // Aktualizacja mapy playerSectors
        playerSectors.put(playerId, "p" + (sectorIndex + 1));

        return sector;
    }
        

    public List<int[]> getOppositeSector(char playerId) {
        List<List<int[]>> sectors = List.of(p1, p2, p3, p4, p5, p6);
    
        // Przypisanie przeciwnika na podstawie ID gracza
        int sectorIndex = (playerId - 'A') % sectors.size(); // A -> p1, B -> p2, ...
    
        // Przypisanie sektorów przeciwnika
        if (sectorIndex == 0) return p4; // A -> p4
        if (sectorIndex == 1) return p5; // B -> p5
        if (sectorIndex == 2) return p6; // C -> p6
        if (sectorIndex == 3) return p1; // D -> p1
        if (sectorIndex == 4) return p2; // E -> p2
        if (sectorIndex == 5) return p3; // F -> p3
    
        return null; // Domyślny przypadek, jeśli nie rozpoznano ID gracza
    }
    
    public boolean isPieceInOppositeSector(String pieceId) {
        // Przeszukiwanie listy pionków
        for (Piece piece : pieces) {
            if (piece.getPieceId().equals(pieceId)) {
                // Pobieramy ID gracza z metody getPlayerId()
                char playerId = piece.getPlayerId();
                List<int[]> oppositeSector = getOppositeSector(playerId);
    
                // Sprawdzamy, czy pionek znajduje się w przeciwnym sektorze
                for (int[] position : oppositeSector) {
                    if (piece.getX() == position[0] && piece.getY() == position[1]) {
                        return true; // Pionek znajduje się w przeciwnym sektorze
                    }
                }
            }
        }
    
        return false; // Pionek nie znajduje się w przeciwnym sektorze
    }
    

    public boolean isFieldFree(int x, int y) {
        for (Piece piece : pieces) {
            if (piece.getX() == x && piece.getY() == y) {
                return false;
            }
        }
        return true;
    }

    public boolean isLegal(String pieceId, int newX, int newY) {
        // Znalezienie pionka na podstawie pieceId
        Piece piece = null;
        for (Piece p : pieces) {
            if (p.getPieceId().equals(pieceId)) {
                piece = p;
                break;
            }
        }
    
        if (piece == null) {
            System.out.println("Brak pionka o ID " + pieceId + " na planszy.");
            return false;
        }
    
        // Odczytanie współrzędnych starej pozycji z obiektu Piece
        int oldX = piece.getX();
        int oldY = piece.getY();
    
        // Sprawdzanie, czy pole docelowe jest na planszy
        if (!isWithinBoard(newX, newY)) {
            System.out.println("Nielegalny ruch – współrzędne poza dozwolonym obszarem gry.");
            return false;
        }
    
        // Sprawdzanie, czy pole docelowe jest zajęte
        if (!isFieldFree(newX, newY)) {
            System.out.println("Pole docelowe (" + newX + ", " + newY + ") jest zajęte.");
            return false;
        }
    
        // Jeśli pionek jest w przeciwnym sektorze, sprawdzamy, czy nie wychodzi z niego
        if (isPieceInOppositeSector(pieceId)) {
            List<int[]> oppositeSector = getOppositeSector(playerId);
            boolean isInSector = false;
            for (int[] position : oppositeSector) {
                if (position[0] == newX && position[1] == newY) {
                    isInSector = true;
                    break;
                }
            }
            if (!isInSector) {
                System.out.println("Nielegalny ruch – pionek nie może opuścić przeciwniczego sektora.");
                return false;
            }
        }
    
        // Sprawdzanie ruchu na sąsiednie pole
        if (isAdjacent(oldX, oldY, newX, newY)) {
            return true; // Ruch na sąsiednie pole jest legalny
        }
    
        // Sprawdzenie, czy ruch jest legalny na podstawie zasady skakania
        return canReachByHopping(oldX, oldY, newX, newY, new boolean[allowedPositions.size()]);
    }
    
    // Nowa metoda sprawdzająca, czy pole docelowe jest sąsiednie
    private boolean isAdjacent(int oldX, int oldY, int newX, int newY) {
        int[][] directions = {
            {2, 0}, {-2, 0}, {-1, -2}, {1, -2}, {-1, 2}, {1, 2}
        };
    
        for (int[] dir : directions) {
            if (oldX + dir[0] == newX && oldY + dir[1] == newY) {
                return true;
            }
        }
        return false;
    }
    
    

    private boolean canReachByHopping(int currentX, int currentY, int targetX, int targetY, boolean[] visited) {
        if (currentX == targetX && currentY == targetY) {
            return true; 
        }   

        int currentIndex = getIndex(currentX, currentY);
        if (currentIndex == -1 || visited[currentIndex]) {
            return false; 
        }
        visited[currentIndex] = true;

        // Możliwe kierunki skoku
        int[][] directions = {
            {2, -4}, {4, 0}, {2, 4}, {-2, 4}, {-4, 0}, {-2, -4}
        };

        for (int[] dir : directions) {
            int middleX = currentX + dir[0] / 2;
            int middleY = currentY + dir[1] / 2;
            int nextX = currentX + dir[0];
            int nextY = currentY + dir[1];

        // Sprawdzenie, czy możemy przeskoczyć przez środkowe pole i wylądować na następne
        if (!isFieldFree(middleX, middleY) && isFieldFree(nextX, nextY)) {
            if (canReachByHopping(nextX, nextY, targetX, targetY, visited)) {
                return true; // Można osiągnąć cel
            }
        }
    }

    return false; // Nie można osiągnąć celu
}


    private boolean isWithinBoard(int x, int y) {
        for (int[] position : allowedPositions) {
            if (position[0] == x && position[1] == y) {
                return true;
            }
        }
        return false;
    }


    private int getIndex(int x, int y) {
        for (int i = 0; i < allowedPositions.size(); i++) {
            int[] position = allowedPositions.get(i);
            if (position[0] == x && position[1] == y) {
                return i; // Zwraca indeks, jeśli pozycja pasuje
            }
        }
        return -1; // Zwraca -1, jeśli nie znaleziono pozycji
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
            sb.append(pos[0]).append(",").append(pos[1]).append(",");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    public String getAllPiecesInfo() {
        if (pieces.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Piece piece : pieces) {
            sb.append(piece.getX()).append(",")
                    .append(piece.getY()).append(",")
                    .append(piece.getPieceId().charAt(0)).append(",");
        }

        // Usunięcie ostatniego przecinka
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }

        return sb.toString();
    }
}
