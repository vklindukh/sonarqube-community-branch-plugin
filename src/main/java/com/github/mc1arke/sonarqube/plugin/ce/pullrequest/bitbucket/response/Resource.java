package com.github.mc1arke.sonarqube.plugin.ce.pullrequest.bitbucket.response;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Resource implements Serializable {
    private final boolean isLastPage;
    private final Integer nextPageStart;
    private final String id;
    private final List<Action> values;

    @JsonCreator
    public Resource(@JsonProperty("isLastPage") final boolean isLastPage,
                    @JsonProperty("nextPageStart") final Integer nextPageStart,
                    @JsonProperty("values") final List<Action> values) {
        this.isLastPage = isLastPage;
        this.id = Boolean.toString(isLastPage);
        this.nextPageStart = nextPageStart;
        this.values = values;
    }

    public String getId() { return id; }
    public boolean isLastPage() {
        return isLastPage;
    }
    public Integer getNextPageStart() { return nextPageStart; }
    public List<Action> getActivity() {
        return values;
    }
}
