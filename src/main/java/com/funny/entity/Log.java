package com.funny.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 日记
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Entity
public class Log {
    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 200)
    private String method;

    @Column(length = 10000)
    private String reqBody;

    @Column(length = 10000)
    private String respBody;

    /**
     * 创建时间，自动维护
     */
    @Column(insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createTime;

    /**
     * 花费时间
     */
    private long spendTime;

    @ManyToOne
    private Log parent;

    @OneToMany(mappedBy = "parent")
    private List<Log> children;


}
