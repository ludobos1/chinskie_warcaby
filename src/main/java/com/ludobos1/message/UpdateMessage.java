package com.ludobos1.message;

/**
 * Reprezentuje wiadomość aktualizującą stan gry.
 * Zawiera informacje o pozycjach na planszy, rozmiarze planszy, wariancie gry,
 * liczbie graczy, aktywnym graczu i identyfikatorze użytkownika.
 */
public class UpdateMessage implements Message{
  TypeEnum type;
  String positions;
  String boardSize;
  String variant;
  String playersNum;
  String activePlayer;
  String yourId;

  /**
   * Tworzy nowy obiekt {@code UpdateMessage} z podanymi pozycjami na planszy i aktywnym graczem.
   *
   * @param positions pozycje na planszy
   * @param activePlayer aktywny gracz
   */
  public UpdateMessage(String positions, char activePlayer){
    type = TypeEnum.UPDATE;
    this.positions = positions;
    this.activePlayer = String.valueOf(activePlayer);
  }
  /**
   * Tworzy nowy obiekt {@code UpdateMessage} z pełnymi danymi o grze, w tym pozycjami, rozmiarem planszy, wariantem gry,
   * liczbą graczy, aktywnym graczem i identyfikatorem użytkownika.
   *
   * @param positions pozycje na planszy
   * @param boardSize rozmiar planszy
   * @param variant wariant gry
   * @param playersNum liczba graczy
   * @param activePlayer aktywny gracz
   * @param yourId identyfikator użytkownika
   */
  public UpdateMessage(String positions, String boardSize, String variant, String playersNum, char activePlayer, String yourId){
    type = TypeEnum.UPDATE;
    this.positions = positions;
    this.boardSize = boardSize;
    this.variant = variant;
    this.playersNum = playersNum;
    this.activePlayer = String.valueOf(activePlayer);
    this.yourId = yourId;
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
   * Zwraca zawartość wiadomości w formacie:
   * - Jeśli {@code boardSize} jest dostępny: "activePlayer/positions/boardSize/variant/playersNum/yourId"
   * - W przeciwnym przypadku: "activePlayer/positions"
   *
   * @return zawartość wiadomości
   */
  @Override
  public String getContent() {
    if (boardSize!=null){
      return activePlayer + "/" + positions + "/" + boardSize + "/" + variant + "/" + playersNum + "/" + yourId;
    } else {
      return activePlayer + "/" + positions;
    }
  }
}
