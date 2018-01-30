package com.funny.entity;

public enum Status {
    UNCHECKED("未审核"), OFFLINE("下架"), ONLINE("上架");


    public String value;

    Status(String value) {
        this.value = value;
    }


}
