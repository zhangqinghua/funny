package com.funny.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

/**
 * 标签 描述图片的属性，比如搞笑，宠物
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString(of = {"id", "name"})
@Entity
public class Tag {
    /**
     * 主键
     * 自动增长
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * 搞笑，宠物
     */
    private String name;

    @ManyToMany(mappedBy = "tags")
    private List<Image> images;

    @ManyToMany(mappedBy = "tags")
    private List<Joke> Jokes;
}
