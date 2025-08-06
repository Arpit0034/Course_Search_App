package com.course_project.courseapp.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
}
