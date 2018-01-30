package com.funny.entity;

public enum Type {
    GIF("动图"), IMAGE("图片"), JOKE("段子");

    public String value;

    Type(String value) {
        this.value = value;
    }
}
