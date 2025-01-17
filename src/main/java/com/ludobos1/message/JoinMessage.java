package com.ludobos1.message;

/**
 * Reprezentuje wiadomość używaną do dołączania do istniejącej sesji gry.
 */
public class JoinMessage implements Message {
  int sessionNumber;
  TypeEnum type;

  /**
   * Tworzy nowy obiekt {@code JoinMessage} z określonym numerem sesji.
   *
   * @param sessionNumber numer sesji, do której gracz chce dołączyć
   */
  public JoinMessage(int sessionNumber) {
    type = TypeEnum.JOIN;
    this.sessionNumber = sessionNumber;
  }

  /**
   * Zwraca typ wiadomości.
   *
   * @return typ wiadomości
   */
  @Override
  public TypeEnum getType() {
    return type;
  }

  /**
   * Zwraca zawartość wiadomości w formie tekstowej, zawierającej numer sesji.
   *
   * @return numer sesji jako ciąg znaków
   */
  @Override
  public String getContent() {
    return String.valueOf(sessionNumber);
  }
}
