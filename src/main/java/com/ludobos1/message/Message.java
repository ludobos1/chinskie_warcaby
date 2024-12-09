package com.ludobos1.message;

import java.io.Serializable;

public interface Message extends Serializable {
  TypeEnum getType();
  String getContent();
}
