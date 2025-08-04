package com.course_project.courseapp.controller;

import com.course_project.courseapp.dto.SearchResponse;
import com.course_project.courseapp.service.CourseSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
@Slf4j
public class SearchController {
    private final CourseSearchService courseSearchService ;

    @GetMapping("/search")
    public ResponseEntity<SearchResponse> searchCourses(
            @RequestParam(value="q",required = false) String keyword ,
            @RequestParam(value="minAge",required = false) Integer minAge ,
            @RequestParam(value="maxAge",required = false) Integer maxAge ,
            @RequestParam(value="category",required = false) String category ,
            @RequestParam(value="type",required = false) String type ,
            @RequestParam(value="minPrice",required = false) Double minPrice ,
            @RequestParam(value="maxPrice",required = false) Double maxPrice ,
            @RequestParam(value="startDate",required = false) String startDate ,
            @RequestParam(value="sort",defaultValue = "upcoming") String sort,
            @RequestParam(value="page",defaultValue = "0") int page ,
            @RequestParam(value="size",defaultValue = "10") int size
    ){
        String startingDate = null ;
        if(startDate != null && !startDate.isEmpty()){
            try{
                LocalDate.parse(startDate) ;
                startingDate = startDate ;
            }catch(Exception e){
                log.error("Invalid Date Format : {}",startDate,e);
                return ResponseEntity.badRequest().body(new SearchResponse(0L, List.of(),"Invalid start date format")) ;
            }
        }
        var result = courseSearchService.searchCourses(keyword,minAge,maxAge,category,type,minPrice
        ,maxPrice,startingDate,sort,page,size) ;
        return ResponseEntity.ok(new SearchResponse(result.getTotalElements(),result.getContent())) ;
    }
}
