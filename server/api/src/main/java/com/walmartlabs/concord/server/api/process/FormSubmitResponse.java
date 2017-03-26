package com.walmartlabs.concord.server.api.process;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Map;

@JsonInclude(Include.NON_NULL)
public class FormSubmitResponse implements Serializable {

    private final boolean ok;
    private final String processInstanceId;
    private final Map<String, String> errors;

    @JsonCreator
    public FormSubmitResponse(@JsonProperty("processInstanceId") String processInstanceId,
                              @JsonProperty("errors") Map<String, String> errors) {

        this.ok = errors == null || errors.isEmpty();
        this.processInstanceId = processInstanceId;
        this.errors = errors;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public boolean isOk() {
        return ok;
    }

    @Override
    public String toString() {
        return "FormSubmitResponse{" +
                "ok=" + ok +
                ", processInstanceId='" + processInstanceId + '\'' +
                ", errors=" + errors +
                '}';
    }
}
