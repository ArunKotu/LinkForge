package com.arun.linkforge.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {
   private String messsage;
   private String details;
   private LocalDateTime time;
}
