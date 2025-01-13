package com.ludobos1.message;

public class UpdateMessage implements Message{
  TypeEnum type;
  String positions;
  String boardSize;

  public UpdateMessage(String positions){
    type = TypeEnum.UPDATE;
    this.positions = positions;
  }
  public UpdateMessage(String positions, String boardSize){
    type = TypeEnum.UPDATE;
    this.positions = positions;
    this.boardSize = boardSize;
  }
  @Override
  public TypeEnum getType() {
    return type;
  }
  @Override
  public String getContent() {
    if (boardSize!=null){
      return positions + "/" + boardSize;
    } else {
      return positions;
    }
  }
}