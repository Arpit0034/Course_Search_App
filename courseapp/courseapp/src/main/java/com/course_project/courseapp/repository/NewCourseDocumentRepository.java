package com.course_project.courseapp.repository;

import com.course_project.courseapp.entity.NewCourseDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewCourseDocumentRepository extends ElasticsearchRepository<NewCourseDocument,Long> {
}
