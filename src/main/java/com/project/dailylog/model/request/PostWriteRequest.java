package com.project.dailylog.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostWriteRequest {
    private String postContent;
    private String postVisible;
}
