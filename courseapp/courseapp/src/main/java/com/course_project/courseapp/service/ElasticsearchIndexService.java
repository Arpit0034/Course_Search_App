package com.course_project.courseapp.service;

import com.course_project.courseapp.entity.NewCourseDocument;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ElasticsearchIndexService {

    private final ElasticsearchOperations elasticsearchOperations ;

    @PostConstruct
    public void createCourses2Index(){
        IndexOperations indexOperations = elasticsearchOperations.indexOps(NewCourseDocument.class);
        if(!indexOperations.exists()){
            indexOperations.create();
            indexOperations.putMapping(indexOperations.createMapping());
        }
    }
}
