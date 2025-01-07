package com.adam9e96.BlogStudy.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Builder
public class Member {

    private String name;
    private int age;

    public Member(String name, int age) {
        this.name = name;
        this.age = age;
    }

}