package com.funny.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 段子
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Entity
public class Joke {

    /**
     * 自动增长主键
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * 内容
     */
    @Column(length = 20000)
    private String description;

    /**
     * 状态 0下架 1上架
     */
    @Column
    private Integer status;

    /**
     * 推荐星级 0不推荐 5 超级搞笑
     */
    @Column
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
