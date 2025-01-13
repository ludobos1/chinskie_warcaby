package com.ludobos1.message;

public class UpdateMessage implements Message{
  TypeEnum type;
  String positions;
  String boardSize;
  String variant;
  String playersNum;
  String activePlayer;
  String yourId;

  public UpdateMessage(String positions, char activePlayer){
    type = TypeEnum.UPDATE;
    this.positions = positions;
    this.activePlayer = String.valueOf(activePlayer);
  }
  public UpdateMessage(String positions, String boardSize, String variant, String playersNum, char activePlayer, String yourId){
    type = TypeEnum.UPDATE;
    this.positions = positions;
    this.boardSize = boardSize;
    this.variant = variant;
    this.playersNum = playersNum;
    this.activePlayer = String.valueOf(activePlayer);
    this.yourId = yourId;
  }
  @Override
  public TypeEnum getType() {
    return type;
  }
  @Override
  public String getContent() {
    if (boardSize!=null){
      return activePlayer + "/" + positions + "/" + boardSize + "/" + variant + "/" + playersNum + "/" + yourId;
    } else {
      return activePlayer + "/" + positions;
    }
  }
}
