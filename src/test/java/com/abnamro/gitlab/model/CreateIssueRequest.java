package com.abnamro.gitlab.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JSON payload for POST /projects/:id/issues.
 * Null fields are omitted so callers can send only the fields they need.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateIssueRequest {
    private String title;
    private String description;
    private String labels;
    private String assignee_ids;
    private String milestone_id;
    private Boolean confidential;
}
