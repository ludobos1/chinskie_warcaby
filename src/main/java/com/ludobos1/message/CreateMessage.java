package com.ludobos1.message;

public class CreateMessage implements Message{
  TypeEnum type;
  int x1;
  int x2;
  int y1;
  int y2;
  String name;

  public CreateMessage(int x1, int x2, int y1, int y2, String name) {
    type = TypeEnum.CREATE;
    this.x1 = x1;
    this.x2 = x2;
    this.y1 = y1;
    this.y2 = y2;
    this.name = name;
  }

  public String getContent() {
    return (x1+","+x2+","+y1+","+y2+","+name);
  }

  @Override
  public TypeEnum getType() {
    return type;
  }
}
