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
    @Column(length = 500)
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
    @Column(length = 10)
    private String suffix;

    /**
     * 状态 0未审核 1下架 2上架
     */
    private Integer status;

    /**
     * 推荐星级 1最小 5最大
     */
    private Integer star;


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
    @ManyToMany
    private List<Tag> tags;
}
