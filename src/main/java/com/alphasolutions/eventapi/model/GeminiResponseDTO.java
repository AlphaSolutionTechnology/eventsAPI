package com.alphasolutions.eventapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeminiResponseDTO {

    @JsonProperty("candidates")
    private List<Candidate> candidates;

    @Setter
    @Getter
    public static class Candidate {
        @JsonProperty("content")
        private Content content;

    }

    @Setter
    @Getter
    public static class Content {
        @JsonProperty("parts")
        private List<Part> parts;

    }

    @Setter
    @Getter
    public static class Part {
        @JsonProperty("text")
        private String text;

    }
}
