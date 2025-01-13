package com.ludobos1;

public class BoardBuilder {
  private int playerNum;
  private int variant;
  public BoardBuilder setPlayerNum(int playerNum) {
    this.playerNum = playerNum;
    return this;
  }
  public BoardBuilder setVariant(int variant) {
    this.variant = variant;
    return this;
  }
  public Board build() {
    return new Board(variant, playerNum);
  }
}
