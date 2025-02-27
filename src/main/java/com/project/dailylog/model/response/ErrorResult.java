package com.project.dailylog.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResult {
    private boolean success;
    private int code;
    private String msg;
    private String errorCode;
}
