package com.funny.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * 用户信息
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Entity
public class User {

    @Id
    @GeneratedValue
    private long id;

    private String oAuthId;

    private OAuthType oAuthType;

    @Size(min = 1, max = 10, message = "昵称不能未空，且不大于10个字符串")
    @Column(length = 10)
    private String nickname;

    @Size(min = 3, max = 10, message = "密码长度应在6-10之间")
    @Column(length = 10)
    private String password;

    @Size(min = 1, message = "邮箱不能未空")
    @Column(length = 20)
    private String email;

    @Size(min = 1, message = "手机不能未空")
    @Column(length = 11)
    private String mobile;

    private UserType userType = UserType.VISITOR;

    /**
     * 创建时间，自动维护
     */
    @Column(insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createTime;

    /**
     * 修改时间，自动维护
     */
    @Column(insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Date updateTime;


    public enum OAuthType {
        GITHUB("github");

        public String text;

        OAuthType(String text) {
            this.text = text;
        }
    }

    public enum UserType {
        VISITOR("游客"), OPERATOR("运营"), ADMINISTRATOR("管理");

        public String text;

        UserType(String text) {
            this.text = text;
        }
    }
}
