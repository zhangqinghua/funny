package com.funny.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 图片
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Entity
public class Image {

    /**
     * 自动增长主键
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * 图片描述
     */
    @Column(length = 20000)
    private String description;

    /**
     * 图片大小 byte单位
     */
    private Integer size;


    /**
     * 图片url
     * <p>
     * /file/2017/12/10/001.png
     */
    @Column(length = 1000)
    private String url;


    /**
     * 图片类型 gif png jepg
     */
    private Type type;

    /**
     * 状态
     */
    private Status status;

    /**
     * 点赞
     */
    private Integer upvote;

    /**
     *
     */
    private Integer downvote;


    /**
     * 创建时间，自动维护
     */
    @Column(insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createTime;

    /**
     * 修改时间，自动维护
     */
    @Column(insertable = false, updatable = true, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Date updateTime;

    /**
     * 所属标签
     */
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Tag> tags;

    public enum Status {
        UNCHECKED("未审核"), OFFLINE("下架"), ONLINE("上架");

        public String text;

        Status(String text) {
            this.text = text;
        }
    }

    public enum Type {
        GIF("动图"), IMAGE("图片"), JOKE("段子");

        public String text;

        Type(String text) {
            this.text = text;
        }
    }
}
