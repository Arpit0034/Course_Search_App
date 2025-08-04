package com.course_project.courseapp.service;

import com.course_project.courseapp.dto.CourseDocumentDto;
import com.course_project.courseapp.entity.CourseDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class CourseSearchServiceImpl implements CourseSearchService{

    private final ModelMapper modelMapper ;
    private final ElasticsearchOperations elasticsearchOperations ;

    @Override
    public Page<CourseDocumentDto> searchCourses(String keyword, Integer minAge, Integer maxAge, String category, String type, Double minPrice, Double maxPrice, String startDate, String sort, int page, int size) {
        try{
            Criteria criteria = new Criteria() ;
            if(keyword != null && !keyword.trim().isEmpty()){
                Criteria title =  new Criteria("title").matches(keyword) ;
                Criteria description = new Criteria("description").matches(keyword) ;
                criteria = criteria.and(new Criteria().or(title).or(description)) ;
            }
            if(minAge != null && maxAge != null && minAge < maxAge){
                Criteria min = new Criteria("minAge").greaterThanEqual(minAge) ;
                Criteria max = new Criteria("maxAge").lessThanEqual(maxAge) ;
                criteria = criteria.and(new Criteria().or(min).or(max)) ;
            }
            if(minPrice != null && maxPrice != null && minPrice < maxPrice){
                criteria = criteria.and(new Criteria("price").between(minPrice,maxPrice)) ;
            }
            if(category != null && !category.isEmpty()){
                criteria = criteria.and(new Criteria("category").is(category)) ;
            }
            if(type != null && !type.isEmpty()){
                criteria = criteria.and(new Criteria("type").is(type)) ;
            }
            if(startDate != null && !startDate.isEmpty()){
                LocalDate.parse(startDate) ;
                criteria = criteria.and(new Criteria("nextSessionDate").greaterThanEqual(startDate)) ;
            }
            CriteriaQuery criteriaQuery = new CriteriaQuery(criteria) ;

            Sort sortObj ;
            if("priceAsc".equalsIgnoreCase(sort)) sortObj = Sort.by(Sort.Direction.ASC,"price") ;
            else if("priceDesc".equalsIgnoreCase(sort)) sortObj = Sort.by(Sort.Direction.DESC,"price") ;
            else sortObj = Sort.by(Sort.Direction.DESC,"nextSessionDate") ;

            Pageable pageable = PageRequest.of(page,size,sortObj) ;
            criteriaQuery.setPageable(pageable) ;

            SearchHits<CourseDocument> searchHits = elasticsearchOperations.search(criteriaQuery,CourseDocument.class) ;
            List<CourseDocumentDto> data = searchHits
                    .getSearchHits()
                    .stream()
                    .map((ele) -> ele.getContent())
                    .map((ele) -> modelMapper.map(ele,CourseDocumentDto.class))
                    .toList();
            long totalHits = searchHits.getTotalHits() ;
            return new PageImpl<>(data,pageable,totalHits) ;
        }catch(Exception e){
            log.error("Error occur while fetching courses : ",e);
            return Page.empty() ;
        }
    }
}
