package com.ludobos1.message;

public class SaveMessage implements Message {
  private final TypeEnum type;
  private final String nazwa;

  public SaveMessage(String nazwa){
    this.type = TypeEnum.SAVE;
    this.nazwa = nazwa;
  }

  @Override
  public TypeEnum getType() {
    return type;
  }

  @Override
  public String getContent(){
    return nazwa;
  }
}
