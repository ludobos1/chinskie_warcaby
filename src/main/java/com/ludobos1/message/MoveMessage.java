package com.ludobos1.message;

public class MoveMessage implements Message {
  TypeEnum type;
  int to1;
  int to2;
  String PieceId;

  public MoveMessage(String pieceId, int to1, int to2) {
    type = TypeEnum.MOVE;
    this.PieceId = pieceId;
    this.to1 = to1;
    this.to2 = to2;
  }

  @Override
  public String getContent() {
    return PieceId + "," + to1 + "," + to2;
  }

  @Override
  public TypeEnum getType(){
    return type;
  }
}
