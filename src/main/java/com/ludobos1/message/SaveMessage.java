package com.ludobos1.message;

public class SaveMessage implements Message {
  private final TypeEnum type;

  public SaveMessage(){
    this.type = TypeEnum.SAVE;
  }

  @Override
  public TypeEnum getType() {
    return type;
  }

  @Override
  public String getContent(){
    return null;
  }
}
