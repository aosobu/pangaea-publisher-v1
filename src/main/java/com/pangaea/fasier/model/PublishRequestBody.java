package com.pangaea.fasier.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class PublishRequestBody {

    private Map<String, Object> values;

    public static PublishRequestBody getInstance() {
        return new PublishRequestBody();
    }
}
