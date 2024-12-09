package com.ludobos1.message;

import com.ludobos1.GameSession;

import java.util.List;

public class SessionsMessage implements Message {
  private final TypeEnum type;
  private List<String> sessionNames;

  public SessionsMessage(List<String> sessionNames) {
    type = TypeEnum.SESSIONS;
    this.sessionNames = sessionNames;
  }

  @Override
  public TypeEnum getType() {
    return type;
  }

  @Override
  public String getContent () {
    StringBuilder content = new StringBuilder();
    for (String sessionName : sessionNames) {
      content.append(sessionName).append(",");
    }
    return content.toString();
  }
}
