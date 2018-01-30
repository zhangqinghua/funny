package com.funny.repository;

import com.funny.entity.Image;
import com.funny.entity.Status;
import com.funny.entity.Tag;
import com.funny.entity.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ImageResitory extends BaseResitory<Image> {

    Page<Image> findByTypeAndStatusOrderByUpdateTimeDesc(Type type, Status status, Pageable pageable);

    Page<Image> findByDescriptionContainsAndTypeAndStatusOrderByUpdateTimeDesc(String description, Type type, Status status, Pageable pageable);

    Page<Image> findByTagsAndTypeAndStatusOrderByUpdateTimeDesc(Tag tag, Type type, Status status, Pageable pageable);


}
