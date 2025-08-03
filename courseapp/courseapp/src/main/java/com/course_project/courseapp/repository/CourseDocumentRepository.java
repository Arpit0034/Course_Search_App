package com.course_project.courseapp.repository;

import com.course_project.courseapp.entity.CourseDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CourseDocumentRepository extends ElasticsearchRepository<CourseDocument,Long> {
}
