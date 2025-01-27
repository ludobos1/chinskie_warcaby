package com.ludobos1;
import java.util.*;

public class BotLogic
{
    private int finalX=12;
    private int finalY=0;
    int[] tab = new int[2];
    int[] konc= new int[2];
    Board board;
    List<int[]> fWP1 = new ArrayList<>();
    List<int[]> fWP2 = new ArrayList<>();
    List<int[]> fWP3 = new ArrayList<>();
    List<int[]> fWP4 = new ArrayList<>();
    List<int[]> fWP5 = new ArrayList<>();
    List<int[]> fWP6 = new ArrayList<>();
    int table2[];
    
    public BotLogic(Board board)
    {
        this.board=board;
    }


    /*public void createFrameWinList()
    {
        int sideLength=5;
        //górny
        for(int x=sideLength*2-1;x<=sideLength*3;x=x+2){
            fWP1.add(new int[]{x,(sideLength-1)*2-2});
        }
        //dolny
        for(int x=sideLength*2-1;x<=sideLength*3;x=x+2){
            fWP2.add(new int[]{x,(sideLength-1)*6+2});
        }
        //lewy górny
        for(int y=(sideLength-1)*2;y<=(sideLength-1)*4-2;y=y+2){
        for(int x=(sideLength-1)*2-2;x>=sideLength-2;x--)
        {
            fWP6.add(new int[]{x,y});
        }
        }
        //lewy dolny
        for(int y=(sideLength-1)*2+4;y<=(sideLength-1)*4-2;y=y+2){
            for(int x=sideLength-2;x<=(sideLength-1)*2-1;x++){
                fWP5.add(new int[]{x,y});
            }
        }
        //prawy górny
        for(int y=(sideLength-1)*2;y<=(sideLength-1)*4-2;y=y+2){
            for(int x=sideLength*4-2;x<=sideLength*4-2+sideLength-2;x++){
                fWP2.add(new int[]{x,y});
            }
        }
        //prawy dolny
        for(int y=(sideLength-1)*2+4;y<=(sideLength-1)*4-2;y=y+2){
            for(int x=sideLength*4-2+sideLength-2;x<=sideLength*4-2;x--){
                fWP3.add(new int[]{x,y});
            }
        }
    }*/
public String randomPiece() {
    Random random = new Random();
    double probability = random.nextDouble();
    System.out.println("PROBABILITY " + probability);

    if (probability < 0.3) {
        return findFastestPieceToWin();
    } else {
        String selectedPieceId = "";
        int randomNumber;
        do {
            randomNumber = random.nextInt(10) + 1; 
            selectedPieceId = "D" + randomNumber; 
        } while (findSpaceFastestToWin(selectedPieceId, finalX, finalY) == Integer.MAX_VALUE);
        findSpaceFastestToWin("D"+randomNumber, finalX, finalY);
        konc[0]=tab[0];
        konc[1]=tab[1];
        System.out.println("Selected piece: " + selectedPieceId + 
                           ", SpaceFastestToWin: " + findSpaceFastestToWin(selectedPieceId, finalX, finalY));
        return selectedPieceId;
    }
}



    public String findFastestPieceToWin(){
        String minId="";
        int minM=Integer.MAX_VALUE;
        konc[0]=-1;
        konc[1]=-1;
        tab[0]=-1;
        tab[1]=-1;
        int ifZmienil=0;


            for(Piece piece:board.getpieces())
            {
                if(piece.getPieceId().charAt(0)=='D'){
                    if(findSpaceFastestToWin(piece.getPieceId(),finalX,finalY)<countFreeSpacesToWin(piece.getX(), piece.getY(),finalX, finalY)){
                        if(findSpaceFastestToWin(piece.getPieceId(),finalX, finalY)<minM){
                            minId=piece.getPieceId();
                            konc[0]=tab[0];
                            konc[1]=tab[1];
                            minM=findSpaceFastestToWin(piece.getPieceId(), finalX, finalY);
                        }
                    }   
                }
            }
            minM=Integer.MAX_VALUE;
            if(ifZmienil==0){
            for(Piece piece:board.getpieces()){
                    if(piece.getPieceId().charAt(0)=='D'){ 
                        table2=findClosestFreeFinish(piece);
                        findSpaceFastestToWin(piece.getPieceId(), table2[0], table2[1]);
                        if(findSpaceFastestToWin(piece.getPieceId(),table2[0], table2[1])<minM&&!board.isPieceInOppositeSector(piece.getPieceId())){
                            minId=piece.getPieceId();
                            minM=findSpaceFastestToWin(piece.getPieceId(),table2[0], table2[1]);
                            konc[0]=tab[0];
                            konc[1]=tab[1];
                        }
                    }
            }
            }
            System.out.println(ifZmienil + " -ifZmienil");
            return minId;
    }

    /*public boolean iffinished(Piece piece){
        for(int[] coords:board.getp1()){
            if(piece.getX()==coords[0]&&piece.getY()==coords[1]){}
            return true;
        }
        return false;
    }*/

    public int [] findClosestFreeFinish(Piece piece){
        int minDistance = Integer.MAX_VALUE;
        int minX=Integer.MAX_VALUE;
        int minY=Integer.MAX_VALUE;
        for(int[] coords: board.getp1()){
            if(board.isFieldFreeOnlyD(coords[0],coords[1])){
                if(countSpacesToPole(piece.getX(), piece.getY(), coords[0], coords[1])<minDistance){
                    minDistance=countSpacesToPole(piece.getX(), piece.getY(), coords[0], coords[1]);
                    minX=coords[0]; 
                    minY=coords[1];
                }
            }
        }

        int table[]={minX,minY};
        return table;
        
    }

    public int findSpaceFastestToWin(String Id, int fX, int fY) {
        int minX = -1, minY = -1; 
        int minM = Integer.MAX_VALUE; 
         
        for (int[] tile : board.getAllowedPositions()) {
            if(tile[0]==148&&tile[1]==8){
            }
            if (board.isLegal(Id, tile[0], tile[1], Integer.parseInt(board.getVariant()))) {
                int currentFreeSpaces = countFreeSpacesToWin(tile[0], tile[1],fX, fY);
                if (currentFreeSpaces < minM) {
                    minM = currentFreeSpaces; 
                    minX = tile[0];
                    minY = tile[1];
                }
            }
        }
        tab[0] = minX;
        tab[1] = minY;
    
        return minM;
    }
    

    public int countFreeSpacesToWin(int spaceX, int spaceY,int finX, int finY){
        int minS=Integer.MAX_VALUE;
            if(countSpacesToPole(spaceX,spaceY,finX, finY)<minS){
                minS=countSpacesToPole(spaceX,spaceY,finX,finY);
            }        
        return minS;
    }

    public int countSpacesToPole(int startX, int startY, int endX, int endY)
    {
        return (endX-startX)*(endX-startX)+(endY-startY)*(endY-startY);
    }

    public int getTargetX()
    {
        return konc[0];
    }

    public int getTargetY()
    {
        return konc[1];
    }

    public int gettab0()
    {
        return tab[0];
    }

    public int gettab1()
    {
        return tab[1];
    }

}