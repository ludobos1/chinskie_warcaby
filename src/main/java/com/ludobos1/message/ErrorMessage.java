package com.ludobos1.message;

/**
 * Reprezentuje wiadomość o błędzie przesyłaną między klientem a serwerem.
 */
public class ErrorMessage implements Message {
  private String errorNum;
  private TypeEnum type;

  /**
   * Tworzy nowy obiekt {@code ErrorMessage} z określonym kodem błędu.
   *
   * @param errorNum kod błędu w formie tekstowej
   */
  public ErrorMessage(String errorNum) {
    this.errorNum = errorNum;
    type = TypeEnum.ERROR;
  }

  /**
   * Zwraca typ wiadomości.
   *
   * @return typ wiadomości
   */
  public TypeEnum getType() {
    return type;
  }
  /**
   * Zwraca kod błędu zawarty w wiadomości.
   *
   * @return kod błędu
   */
  public String getContent() {
    return errorNum;
  }
}
