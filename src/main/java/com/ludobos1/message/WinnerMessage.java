package com.ludobos1.message;

public class WinnerMessage implements Message {
  private String playerName;
  private TypeEnum type;
  public WinnerMessage(String playerName) {
    this.type = TypeEnum.Winner;
    this.playerName = playerName;
  }

  @Override
  public TypeEnum getType() {
    return type;
  }

  @Override
  public String getContent() {
    return playerName;
  }
}
