package com.course_project.courseapp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName="courses")
public class CourseDocument {
    @Id
    private long id ;

    @MultiField(mainField = @Field(type=FieldType.Text),
    otherFields = {@InnerField(suffix="keyword" , type=FieldType.Keyword)}
    )
    private String title ;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Keyword)
    private String category;

    @Field(type = FieldType.Keyword)
    private String type;

    @Field(type = FieldType.Keyword)
    private String gradeRange;

    @Field(type = FieldType.Integer)
    private Integer minAge;

    @Field(type = FieldType.Integer)
    private Integer maxAge;

    @Field(type = FieldType.Double)
    private Double price;

    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd'T'HH:mm:ssX")
    private String nextSessionDate;
}


