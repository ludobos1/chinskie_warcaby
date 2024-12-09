package com.ludobos1.message;

public class JoinMessage implements Message {
  int sessionNumber;
  TypeEnum type;

  public JoinMessage(int sessionNumber) {
    type = TypeEnum.JOIN;
    this.sessionNumber = sessionNumber;
  }

  @Override
  public TypeEnum getType() {
    return type;
  }

  @Override
  public String getContent() {
    return String.valueOf(sessionNumber);
  }
}
