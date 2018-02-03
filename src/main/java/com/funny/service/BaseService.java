package com.funny.service;

import com.funny.repository.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class BaseService<T> {

    @Autowired
    private BaseRepository<T> baseRepository;

    public Iterable<T> findAll(Sort sort) throws Exception {
        return baseRepository.findAll(sort);
    }

    public Page<T> findAll(Pageable pageable) throws Exception {
        return baseRepository.findAll(pageable);
    }

    public <S extends T> S save(S s) throws Exception {
        return baseRepository.save(s);
    }

    public <S extends T> Iterable<S> save(Iterable<S> iterable) throws Exception {
        return baseRepository.save(iterable);
    }

    public T findOne(Object aLong) throws Exception {
        try {
            if (aLong != null) {
                return baseRepository.findOne(Long.parseLong(aLong + ""));
            }
        } catch (Exception e) {
        }
        return null;
    }

    public boolean exists(Long aLong) throws Exception {
        return baseRepository.exists(aLong);
    }

    public Iterable<T> findAll() throws Exception {
        return baseRepository.findAll();
    }

    public Iterable<T> findAll(Iterable<Long> iterable) throws Exception {
        return baseRepository.findAll(iterable);
    }

    public long count() throws Exception {
        return baseRepository.count();
    }

    public void delete(Long aLong) throws Exception {
        baseRepository.delete(aLong);
    }

    public void delete(T t) throws Exception {
        baseRepository.delete(t);
    }

    public void delete(Iterable<? extends T> iterable) throws Exception {
        baseRepository.delete(iterable);
    }

    public void deleteAll() throws Exception {
        baseRepository.deleteAll();
    }

    public Page<T> findAll(Specification<T> spec, Pageable pageable) throws Exception {
        return baseRepository.findAll(spec, pageable);
    }

    public List<T> findAll(Specification<T> spec) throws Exception {
        return baseRepository.findAll(spec);
    }
}
