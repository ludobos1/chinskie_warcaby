package com.ludobos1.message;

import java.io.Serializable;

/**
 * Reprezentuje interfejs wiadomości używany w systemie komunikacji.
 * Wszystkie klasy implementujące ten interfejs muszą zapewniać informacje
 * o typie wiadomości oraz jej zawartości.
 */
public interface Message extends Serializable {
  /**
   * Zwraca typ wiadomości.
   *
   * @return typ wiadomości jako wartość wyliczeniowa {@link TypeEnum}
   */
  TypeEnum getType();
  /**
   * Zwraca zawartość wiadomości w formie tekstowej.
   *
   * @return zawartość wiadomości jako ciąg znaków
   */
  String getContent();
}
