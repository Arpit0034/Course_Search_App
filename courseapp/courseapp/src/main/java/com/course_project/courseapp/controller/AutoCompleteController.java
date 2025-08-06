package com.course_project.courseapp.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AutoCompleteController {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @GetMapping("/api/search/suggest")
    public List<String> suggest(@RequestParam("q") String prefix) {
        try {
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("courses_2")
                    .suggest(suggester -> suggester
                            .suggesters("course-suggest", cs -> cs
                                    .completion(completion -> completion
                                            .field("titleSuggest")
                                            .size(10)
                                    )
                                    .text(prefix)
                            )
                    )
            );

            SearchResponse<Object> response = elasticsearchClient.search(searchRequest, Object.class);

            List<String> suggestions = new ArrayList<>();
            var suggestResult = response.suggest().get("course-suggest");

            if (suggestResult != null && !suggestResult.isEmpty()) {
                suggestResult.get(0).completion().options().forEach(option -> {
                    suggestions.add(option.text());
                });
            }
            return suggestions;

        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    @GetMapping("/api/search/fuzzy")
    public List<String> fuzzySearch(@RequestParam("q") String keyword) {
        try {
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("courses")
                    .query(q -> q
                            .match(m -> m
                                    .field("title")
                                    .query(keyword)
                                    .fuzziness("2")
                                    .prefixLength(1)
                                    .maxExpansions(50)
                            )
                    )
                    .size(10)
            );

            SearchResponse<Object> response = elasticsearchClient.search(searchRequest, Object.class);

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .map(source -> {
                        if (source instanceof java.util.Map) {
                            return ((java.util.Map<?, ?>) source).get("title").toString();
                        } else {
                            return source.toString();
                        }
                    })
                    .collect(Collectors.toList());

        } catch (IOException e) {
            return List.of();
        }
    }
}
