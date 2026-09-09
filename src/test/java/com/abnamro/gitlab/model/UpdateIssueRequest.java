package com.abnamro.gitlab.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JSON payload for PUT /projects/:id/issues/:issue_iid.
 * Null fields are omitted, allowing partial updates.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateIssueRequest {
    private String title;
    private String description;
    /** GitLab state transition value: "reopen" or "close". */
    private String state_event;
    private String labels;
    private String assignee_ids;
    private String milestone_id;
}
