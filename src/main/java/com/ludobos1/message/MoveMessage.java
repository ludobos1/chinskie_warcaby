package com.ludobos1.message;

public class MoveMessage implements Message {
  TypeEnum type;
  int from1;
  int from2;
  int to1;
  int to2;
  public MoveMessage(int from1, int from2, int to1, int to2) {
    type = TypeEnum.MOVE;
    this.from1 = from1;
    this.from2 = from2;
    this.to1 = to1;
    this.to2 = to2;
  }

  public TypeEnum getType(){
    return type;
  }
}
