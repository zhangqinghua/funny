package com.funny.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * 将本地用户与其在Github或者其他网站的用户信息一一对应起来
 * 存有该用户在其他网站的基本信息（在哪个网站，唯一标识是多少）以及该用户与本地用户的映射
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Entity
public class OAuth {

    @Id
    @GeneratedValue
    private Long id;

    private String oAuthId;

    private Type type;

    @OneToOne
    private User user;

    public enum Type {
        GITHUB("github");

        public String text;

        Type(String text) {
            this.text = text;
        }
    }
}
