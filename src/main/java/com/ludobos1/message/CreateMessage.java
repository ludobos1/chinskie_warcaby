package com.ludobos1.message;

/**
 * Reprezentuje wiadomość używaną do tworzenia nowej sesji gry.
 */
public class CreateMessage implements Message{
  TypeEnum type;
  int playerNum;
  int variant;
  String name;

  /**
   * Tworzy nowy obiekt {@code CreateMessage} z określonymi parametrami.
   *
   * @param playerNum liczba graczy w sesji gry
   * @param variant   wariant sesji gry
   * @param name      nazwa sesji gry
   */
  public CreateMessage(int playerNum, int variant, String name) {
    type = TypeEnum.CREATE;
    this.variant = variant;
    this.name = name;
    this.playerNum = playerNum;
  }

  /**
   * Zwraca zawartość wiadomości w formacie "playerNum,variant,name".
   *
   * @return zawartość wiadomości
   */
  public String getContent() {
    return (playerNum + "," + variant + "," + name);
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
}
