package com.course_project.courseapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDocumentDto {
    private long id ;
    private String title ;
    private String description ;
    private String category ;
    private String type ;
    private String gradeRange ;
    private Integer minAge ;
    private Integer maxAge ;
    private Double price ;
    private String nextSessionDate ;
}
