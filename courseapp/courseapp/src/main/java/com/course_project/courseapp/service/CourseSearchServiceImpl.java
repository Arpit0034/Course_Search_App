package com.course_project.courseapp.service;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.json.JsonData;
import com.course_project.courseapp.dto.CourseDocumentDto;
import com.course_project.courseapp.entity.CourseDocument;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
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

        // 1. Top‐level bool
        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();

        // 2. Query clauses
        if (keyword != null && !keyword.isBlank()) {
            BoolQuery.Builder qc = new BoolQuery.Builder();
            qc.should(MatchQuery.of(m -> m
                    .field("title")
                    .query(keyword)
                    .fuzziness("AUTO")
                    .prefixLength(1)
                    .maxExpansions(50)
                    .boost(2.0f)
            )._toQuery());
            qc.should(MatchQuery.of(m -> m
                    .field("description")
                    .query(keyword)
                    .boost(1.0f)
            )._toQuery());
            qc.minimumShouldMatch("1");
            boolBuilder.must(qc.build()._toQuery());
        } else {
            boolBuilder.must(MatchAllQuery.of(ma -> ma)._toQuery());
        }

        if (minAge != null) {
            boolBuilder.filter(RangeQuery.of(r -> r
                    .untyped(u -> u.field("minAge").gte(JsonData.of(minAge)))
            )._toQuery());
        }
        if (maxAge != null) {
            boolBuilder.filter(RangeQuery.of(r -> r
                    .untyped(u -> u.field("maxAge").lte(JsonData.of(maxAge)))
            )._toQuery());
        }
        if (minPrice != null) {
            boolBuilder.filter(RangeQuery.of(r -> r
                    .untyped(u -> u.field("price").gte(JsonData.of(minPrice)))
            )._toQuery());
        }
        if (maxPrice != null) {
            boolBuilder.filter(RangeQuery.of(r -> r
                    .untyped(u -> u.field("price").lte(JsonData.of(maxPrice)))
            )._toQuery());
        }
        if (category != null && !category.isBlank()) {
            boolBuilder.filter(TermQuery.of(t -> t
                    .field("category").value(category)
            )._toQuery());
        }
        if (type != null && !type.isBlank()) {
            boolBuilder.filter(TermQuery.of(t -> t
                    .field("type").value(type)
            )._toQuery());
        }
        if (startDate != null && !startDate.isBlank()) {
            boolBuilder.filter(RangeQuery.of(r -> r
                    .untyped(u -> u.field("nextSessionDate").gte(JsonData.of(startDate)))
            )._toQuery());
        }

        // 4. Build NativeQuery
        NativeQueryBuilder nq = NativeQuery.builder()
                .withQuery(boolBuilder.build()._toQuery());

        // 5. Pagination & sorting
        PageRequest pageable;
        if ("priceAsc".equalsIgnoreCase(sort)) {
            pageable = PageRequest.of(page, size, Sort.by("price").ascending());
        } else if ("priceDesc".equalsIgnoreCase(sort)) {
            pageable = PageRequest.of(page, size, Sort.by("price").descending());
        } else {
            pageable = PageRequest.of(page, size, Sort.by("nextSessionDate").ascending());
        }
        nq.withPageable(pageable);

        Query query = nq.build();

        // 6. Execute search
        SearchHits<CourseDocument> hits =
                elasticsearchOperations.search(query, CourseDocument.class);

        // 7. Map to DTOs
        List<CourseDocumentDto> dtos = hits.stream()
                .map(SearchHit::getContent)
                .map(cd -> modelMapper.map(cd, CourseDocumentDto.class))
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, hits.getTotalHits());
    }
}
