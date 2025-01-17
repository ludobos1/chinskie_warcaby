package com.ludobos1;

/**
 * Builder do tworzenia obiektu {@link Board}.
 * Umożliwia ustawienie liczby graczy oraz wariantu gry przed stworzeniem planszy.
 */
public class BoardBuilder {
  private int playerNum;
  private int variant;

  /**
   * Ustawia liczbę graczy w grze.
   *
   * @param playerNum liczba graczy
   * @return obiekt {@code BoardBuilder} do dalszego konfigurowania
   */
  public BoardBuilder setPlayerNum(int playerNum) {
    this.playerNum = playerNum;
    return this;
  }
  /**
   * Ustawia wariant gry.
   *
   * @param variant wariant gry
   * @return obiekt {@code BoardBuilder} do dalszego konfigurowania
   */
  public BoardBuilder setVariant(int variant) {
    this.variant = variant;
    return this;
  }
  /**
   * Tworzy obiekt {@link Board} na podstawie ustawionych parametrów.
   *
   * @return nowy obiekt {@code Board} z ustawioną liczbą graczy i wariantem
   */
  public Board build() {
    return new Board(variant, playerNum);
  }
}
