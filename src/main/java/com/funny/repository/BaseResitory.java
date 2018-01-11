package com.funny.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

@NoRepositoryBean
public interface BaseResitory<T>  extends PagingAndSortingRepository<T, Long> {

    Page<T> findAll(Specification<T> spec, Pageable pageable);  //分页按条件查询

    List<T> findAll(Specification<T> spec);    //不分页按条件查询

}
