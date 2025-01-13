package com.ludobos1.message;

public class UpdateMessage implements Message{
  TypeEnum type;
  String positions;
  String boardSize;
  String variant;
  String playersNum;

  public UpdateMessage(String positions){
    type = TypeEnum.UPDATE;
    this.positions = positions;
  }
  public UpdateMessage(String positions, String boardSize, String variant, String playersNum){
    type = TypeEnum.UPDATE;
    this.positions = positions;
    this.boardSize = boardSize;
    this.variant = variant;
    this.playersNum = playersNum;
  }
  @Override
  public TypeEnum getType() {
    return type;
  }
  @Override
  public String getContent() {
    if (boardSize!=null){
      return positions + "/" + boardSize + "/" + variant + "/" + playersNum;
    } else {
      return positions;
    }
  }
}
