package com.course_project.courseapp.service;

import com.course_project.courseapp.entity.CourseDocument;
import com.course_project.courseapp.entity.NewCourseDocument;
import com.course_project.courseapp.repository.CourseDocumentRepository;
import com.course_project.courseapp.repository.NewCourseDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.suggest.Completion;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReindexService {

    private final CourseDocumentRepository oldRepo;
    private final NewCourseDocumentRepository newRepo;

    public void reindexAll() {

        Iterable<CourseDocument> oldCourses = oldRepo.findAll();

        for (CourseDocument old : oldCourses) {
            NewCourseDocument updated = new NewCourseDocument();

            updated.setId(old.getId());
            updated.setTitle(old.getTitle());
            updated.setDescription(old.getDescription());
            updated.setCategory(old.getCategory());
            updated.setType(old.getType());
            updated.setGradeRange(old.getGradeRange());
            updated.setMinAge(old.getMinAge());
            updated.setMaxAge(old.getMaxAge());
            updated.setPrice(old.getPrice());
            updated.setNextSessionDate(old.getNextSessionDate());
            updated.setTitleSuggest(new Completion(new String[]{old.getTitle()}));

            newRepo.save(updated);
        }
    }
}
