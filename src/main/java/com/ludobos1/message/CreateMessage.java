package com.ludobos1.message;

public class CreateMessage implements Message{
  TypeEnum type;
  int playerNum;
  int variant;
  String name;

  public CreateMessage(int playerNum, int variant, String name) {
    type = TypeEnum.CREATE;
    this.variant = variant;
    this.name = name;
    this.playerNum = playerNum;
  }

  public String getContent() {
    return (playerNum + "," + variant + "," + name);
  }

  @Override
  public TypeEnum getType() {
    return type;
  }
}
