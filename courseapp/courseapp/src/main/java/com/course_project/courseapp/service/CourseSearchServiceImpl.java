package com.course_project.courseapp.service;

import com.course_project.courseapp.dto.CourseDocumentDto;
import com.course_project.courseapp.entity.CourseDocument;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseSearchServiceImpl implements CourseSearchService {

    private final ModelMapper modelMapper;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public Page<CourseDocumentDto> searchCourses(
            String keyword,
            Integer minAge,
            Integer maxAge,
            String category,
            String type,
            Double minPrice,
            Double maxPrice,
            String startDate,
            String sort,
            int page,
            int size) {

        Criteria criteria = new Criteria();
        if (keyword != null && !keyword.isBlank()) {
            criteria = criteria.and("title").fuzzy(keyword) ;
            criteria = criteria.and("description").matches(keyword);
        }

        if (minAge != null) {
            criteria = criteria.and("minAge").greaterThanEqual(minAge);
        }
        if (maxAge != null) {
            criteria = criteria.and("maxAge").lessThanEqual(maxAge);
        }
        if (minPrice != null) {
            criteria = criteria.and("price").greaterThanEqual(minPrice);
        }
        if (maxPrice != null) {
            criteria = criteria.and("price").lessThanEqual(maxPrice);
        }
        if (category != null && !category.isBlank()) {
            criteria = criteria.and("category").is(category);
        }
        if (type != null && !type.isBlank()) {
            criteria = criteria.and("type").is(type);
        }
        if (startDate != null && !startDate.isBlank()) {
            criteria = criteria.and("nextSessionDate").greaterThanEqual(startDate);
        }

        CriteriaQuery query = new CriteriaQuery(criteria);
        PageRequest pageable;

        if ("priceAsc".equalsIgnoreCase(sort)) {
            pageable = PageRequest.of(page, size, Sort.by("price").ascending());
        } else if ("priceDesc".equalsIgnoreCase(sort)) {
            pageable = PageRequest.of(page, size, Sort.by("price").descending());
        } else {
            pageable = PageRequest.of(page, size, Sort.by("nextSessionDate").ascending());
        }

        query.setPageable(pageable);

        SearchHits<CourseDocument> searchHits = elasticsearchOperations.search(query, CourseDocument.class);

        List<CourseDocumentDto> dtos = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(course -> modelMapper.map(course, CourseDocumentDto.class))
                .collect(Collectors.toList());

        long totalHits = searchHits.getTotalHits();

        return new PageImpl<>(dtos, pageable, totalHits);
    }
}
