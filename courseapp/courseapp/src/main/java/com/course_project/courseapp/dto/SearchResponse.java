package com.course_project.courseapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResponse {
    private Long total ;
    private List<CourseDocumentDto> courses ;
    private String error ;
    public SearchResponse(Long total , List<CourseDocumentDto> courses){
        this.total = total ;
        this.courses = courses ;
        this.error = null ;
    }
}
