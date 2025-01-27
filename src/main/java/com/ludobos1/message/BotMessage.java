package com.ludobos1.message;

public class BotMessage implements Message {
    TypeEnum type;

public BotMessage(){
    type=TypeEnum.BOT;
}

@Override
public TypeEnum getType(){
    return type;
}

@Override
public String getContent(){
    return null;
}

}