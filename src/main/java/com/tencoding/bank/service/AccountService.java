package com.tencoding.bank.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tencoding.bank.dto.SaveFormDto;
import com.tencoding.bank.handler.exception.CustomRestfullException;
import com.tencoding.bank.repository.interfaces.AccountRepository;
import com.tencoding.bank.repository.model.Account;

@Service // IoC 대상 + 싱글톤 패턴으로 -> 스프링 컨테이너 메모리에 객체가 생성
public class AccountService {
	
	@Autowired // DI - 가지고 오다
	private AccountRepository accountRepository;
	
	@Transactional
	public void createAccount(SaveFormDto saveFormDto, Integer princiPalId) {
		// 등록 처리 - insert
		Account account = new Account();
		account.setNumber(saveFormDto.getNumber());
		account.setPassword(saveFormDto.getPassword());
		account.setBalance(saveFormDto.getBalance());
		account.setUserId(princiPalId);
		int resultRowCount = accountRepository.insert(account);
		if(resultRowCount != 1) {
			throw new CustomRestfullException("계좌 생성 실패", HttpStatus.INTERNAL_SERVER_ERROR);
		}	
	}
	
	/**
	 * 계좌 목록 보기 (로그인된 사용자)
	 * @param userId
	 * @return List<Account>
	 */
	@Transactional
	public List<Account> readAccountList(Integer userId) {
		
		List<Account> list = accountRepository.findByUserId(userId);
		return list;
	}
	
}
