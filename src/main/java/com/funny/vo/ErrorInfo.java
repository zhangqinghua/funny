package com.funny.vo;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ErrorInfo<T> {

    private boolean status;
    private String msg;
    private String url;
    private T data;

}
