package com.ludobos1.message;

/**
 * Enum reprezentujący różne typy wiadomości w systemie.
 * Każdy typ odpowiada za inną kategorię komunikacji, na przykład dołączenie do sesji, wykonanie ruchu, czy zgłoszenie błędu.
 */
public enum TypeEnum {
  JOIN,
  MOVE,
  UPDATE,
  CREATE,
  SESSIONS,
  ERROR,
  Winner,
  BOT,
  SAVE,
  LOAD
}
