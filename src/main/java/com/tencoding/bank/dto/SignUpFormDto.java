package com.tencoding.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SignUpFormDto {
	
	private String username;  
	private String password;
	private String fullname;
	
	// TODO - 추후 추가 예정
}
