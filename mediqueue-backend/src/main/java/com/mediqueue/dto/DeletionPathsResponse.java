package com.mediqueue.dto;

import java.util.List;

public class DeletionPathsResponse {
    private List<String> paths;

    public DeletionPathsResponse() {}

    public DeletionPathsResponse(List<String> paths) {
        this.paths = paths;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }
}
