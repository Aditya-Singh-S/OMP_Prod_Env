//package com.cts.exception;
//
//import org.springframework.http.HttpStatus;
//public class MissingFieldsException extends RuntimeException {
//
//    public MissingFieldsException(String message) {
//        super(message);
//    }
//}

package com.cts.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class MissingFieldsException extends ResponseStatusException {

    public MissingFieldsException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}