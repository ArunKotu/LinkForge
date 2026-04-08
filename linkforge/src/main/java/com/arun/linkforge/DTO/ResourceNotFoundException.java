package com.arun.linkforge.DTO;

import org.springframework.http.ResponseEntity;

public class ResourceNotFoundException extends RuntimeException{
    public  ResourceNotFoundException(String msg){
        super(msg);
    }
}
