package com.ludobos1.message;

import java.util.List;

/**
 * Reprezentuje wiadomość zawierającą listę nazw sesji.
 */
public class SessionsMessage implements Message {
  private final TypeEnum type;
  private List<String> sessionNames;
  private List<String> saveNamesAndIds;

  /**
   * Tworzy nowy obiekt {@code SessionsMessage} z podaną listą nazw sesji.
   *
   * @param sessionNames lista nazw sesji
   */
  public SessionsMessage(List<String> sessionNames, List<String> saveNamesAndIds) {
    type = TypeEnum.SESSIONS;
    this.sessionNames = sessionNames;
    this.saveNamesAndIds = saveNamesAndIds;
  }

  /**
   * Zwraca zawartość wiadomości w formacie "sessionName1,sessionName2,...".
   *
   * @return zawartość wiadomości
   */
  @Override
  public TypeEnum getType() {
    return type;
  }

  /**
   * Zwraca typ wiadomości.
   *
   * @return typ wiadomości
   */
  @Override
  public String getContent () {
    StringBuilder content = new StringBuilder();
    for (String sessionName : sessionNames) {
      content.append(sessionName).append(",");
    }
    content.append("/");
    for (int i = 0; i < saveNamesAndIds.size()/2; i++) {
      content.append(saveNamesAndIds.get(2*i)).append(",");
      content.append(saveNamesAndIds.get(2*i+1)).append("!");
    }
    content.deleteCharAt(content.length() - 1);
    return content.toString();
  }
}
