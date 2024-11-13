package com.project.dailylog.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResult {
    private boolean success;
    private int code;
    private String msg;
    private String errorDetails;
}
