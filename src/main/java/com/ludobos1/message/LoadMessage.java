package com.ludobos1.message;

public class LoadMessage implements Message{
  private final TypeEnum type;
  private final String idZapisu;

  public LoadMessage(String idZapisu){
    this.type = TypeEnum.LOAD;
    this.idZapisu = idZapisu;
  }

  @Override
  public TypeEnum getType() {
    return type;
  }

  @Override
  public String getContent(){
    return idZapisu;
  }
}
