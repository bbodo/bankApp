package com.tencoding.bank.repository.model;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
	private Integer id;
	private String number;
	private String password;
	private Long balance;
	private Integer userId;
	private Timestamp createdAt;

	// 출금 기능
	public void widhdraw(Integer amount) {
		if (this.balance > amount)
			this.balance -= amount;
	}

	// 입금 기능
	public void deposit(Integer amount) {
		this.balance += amount;
	}
	
	// TODO
	// 패스워드 체크
	public boolean passwordCheck() {
		return true;
	}
	// 잔액 여부 확인
	public boolean balanceCheck() {
		return true;
	}
	// 계좌 소유자 확인
	public boolean accountOwnerCheck() {
		return true;
	}
}