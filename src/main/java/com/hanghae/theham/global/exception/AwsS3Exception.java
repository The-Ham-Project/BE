package com.hanghae.theham.global.exception;

import lombok.Getter;

@Getter
public class AwsS3Exception extends RuntimeException {

    public AwsS3Exception(String message) {
        super(message);
    }
}
