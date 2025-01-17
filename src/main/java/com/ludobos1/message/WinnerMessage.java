package com.ludobos1.message;

/**
 * Reprezentuje wiadomość informującą o zwycięzcy w grze.
 * Zawiera nazwę gracza, który wygrał.
 */
public class WinnerMessage implements Message {
  private String playerName;
  private TypeEnum type;

  /**
   * Tworzy nowy obiekt {@code WinnerMessage} z podaną nazwą gracza.
   *
   * @param playerName nazwa gracza, który wygrał
   */
  public WinnerMessage(String playerName) {
    this.type = TypeEnum.Winner;
    this.playerName = playerName;
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
   * Zwraca zawartość wiadomości, czyli nazwę gracza, który wygrał.
   *
   * @return zawartość wiadomości
   */
  @Override
  public String getContent() {
    return playerName;
  }
}
