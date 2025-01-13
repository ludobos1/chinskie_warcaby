package com.ludobos1.message;

public class ErrorMessage implements Message {
  private String errorNum;
  private TypeEnum type;

  public ErrorMessage(String errorNum) {
    this.errorNum = errorNum;
    type = TypeEnum.ERROR;
  }

  public TypeEnum getType() {
    return type;
  }
  public String getContent() {
    return errorNum;
  }
}
