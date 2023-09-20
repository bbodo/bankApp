package com.tencoding.bank.dto;

import lombok.Data;

@Data
public class DepositFormDto {
	
	private Long amount;  // 화면 nameTag 기준.
	private String wAccountNumber;
	private String dAccountNumber;
	
	// TODO - 추후 추가 예정
}
