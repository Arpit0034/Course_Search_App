package com.course_project.courseapp.service;

import com.course_project.courseapp.dto.CourseDocumentDto;
import org.springframework.data.domain.Page;

public interface CourseSearchService {
    public Page<CourseDocumentDto> searchCourses(
            String keyword , Integer minAge , Integer maxAge , String category ,
            String type , Double minPrice , Double maxPrice , String startDate , String sort ,
            int page , int size
    ) ;
}
