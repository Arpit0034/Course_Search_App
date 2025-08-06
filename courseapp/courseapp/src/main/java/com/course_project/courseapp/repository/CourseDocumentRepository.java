package com.course_project.courseapp.repository;

import com.course_project.courseapp.entity.CourseDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseDocumentRepository extends ElasticsearchRepository<CourseDocument,Long> {
    Page<CourseDocument> findByTitleIn(List<String> titles, Pageable pageable);
}
