package com.cts.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignInResponse {
    
	private String message;
    private boolean success;
    private String email;
}
