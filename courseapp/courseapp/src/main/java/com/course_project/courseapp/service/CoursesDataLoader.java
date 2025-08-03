package com.course_project.courseapp.service;

import com.course_project.courseapp.entity.CourseDocument;
import com.course_project.courseapp.repository.CourseDocumentRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CoursesDataLoader implements CommandLineRunner {

    private final CourseDocumentRepository courseDocumentRepository ;

    @Override
    public void run(String... args) throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream inputStream = new ClassPathResource("sample-courses.json").getInputStream();
        List<CourseDocument> courses = objectMapper.readValue(
                inputStream,
                new TypeReference<List<CourseDocument>>() {}
        );
        courseDocumentRepository.saveAll(courses) ;
        System.out.println("Indexed " + courses.size() + " courses into Elasticsearch");
    }
}
