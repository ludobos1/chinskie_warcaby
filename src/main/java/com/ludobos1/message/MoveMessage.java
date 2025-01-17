package com.ludobos1.message;

/**
 * Reprezentuje wiadomość określającą ruch w grze.
 */
public class MoveMessage implements Message {
  TypeEnum type;
  int to1;
  int to2;
  String PieceId;

  /**
   * Tworzy nowy obiekt {@code MoveMessage} z podanymi parametrami.
   *
   * @param pieceId identyfikator figury wykonującej ruch
   * @param to1     pozycja docelowa na osi X
   * @param to2     pozycja docelowa na osi Y
   */
  public MoveMessage(String pieceId, int to1, int to2) {
    type = TypeEnum.MOVE;
    this.PieceId = pieceId;
    this.to1 = to1;
    this.to2 = to2;
  }

  /**
   * Zwraca zawartość wiadomości w formacie "pieceId,to1,to2".
   *
   * @return zawartość wiadomości
   */
  @Override
  public String getContent() {
    return PieceId + "," + to1 + "," + to2;
  }

  /**
   * Zwraca typ wiadomości.
   *
   * @return typ wiadomości
   */
  @Override
  public TypeEnum getType(){
    return type;
  }
}
