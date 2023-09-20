package com.tencoding.bank.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tencoding.bank.dto.DepositFormDto;
import com.tencoding.bank.dto.SaveFormDto;
import com.tencoding.bank.dto.TransferFromDto;
import com.tencoding.bank.dto.WithdrawFormDto;
import com.tencoding.bank.handler.exception.CustomRestfullException;
import com.tencoding.bank.repository.interfaces.AccountRepository;
import com.tencoding.bank.repository.interfaces.HistoryRepository;
import com.tencoding.bank.repository.model.Account;
import com.tencoding.bank.repository.model.History;

@Service // IoC 대상 + 싱글톤 패턴으로 -> 스프링 컨테이너 메모리에 객체가 생성
public class AccountService {

	@Autowired // DI - 가지고 오다
	private AccountRepository accountRepository;

	@Autowired // DI 처리
	private HistoryRepository historyRepository;

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

	// 출금 기능 로직을 고민해보기
	// 1. 계좌 존재 여부 확인 -- select query
	// 2. 본인 계좌 여부 확인 
	// 3. 계좌 비번 확인
	// 4. 잔액 여부 확인
	// 5. 출금 처리 --> update query
	// 6. 거래 내역 --> insert query
	// 7. 트랜잭션 처리
	@Transactional
	public void updateAccountWithdraw(WithdrawFormDto withdrawFormDto, Integer id) {
		Account accountEnitity =  accountRepository.findByNumber(withdrawFormDto.getWAccountNumber());
		// 1
		if(accountEnitity == null ) { 
			throw new CustomRestfullException("해당 계좌가 없습니다", HttpStatus.BAD_REQUEST);
		}
		// 2
		if(accountEnitity.getUserId() != id) { 
			throw new CustomRestfullException("본인 소유 계좌가 아닙니다", HttpStatus.BAD_REQUEST);
		}
		// 3
		if(accountEnitity.getPassword().equals(withdrawFormDto.getWAccountPassword()) == false) {
			throw new CustomRestfullException("출금 계좌 비밀번호가 틀렸습니다.", HttpStatus.BAD_REQUEST);
		}
		// 4
		if(accountEnitity.getBalance() < withdrawFormDto.getAmount()) {
			throw new CustomRestfullException("계좌 잔액이 부족합니다",HttpStatus.BAD_REQUEST);
		}
		// 5 - > update 쿼리 (모델 객체 상태 변경 --> 객체를 다시 던지기)
		accountEnitity.withdraw(withdrawFormDto.getAmount());
		accountRepository.updateById(accountEnitity);
		// 6 - 거래내역 등록 History 객체 생성
		History history = new History();
		history.setAmount(withdrawFormDto.getAmount());
		// 출금 시점에 해당 계좌에 잔액을 입력
		history.setWBalance(accountEnitity.getBalance());
		history.setDBalance(null);
		history.setWAccountId(accountEnitity.getId());
		history.setDAccountId(null);

		int resultRowCount = historyRepository.insert(history);
		if( resultRowCount != 1 ) {
			throw new CustomRestfullException("정상 처리 되지 않았습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// 입금 기능 로직 생각 해보기
	// 1. 계좌 존재 여부 확인 --select query
	// 2. 입금 처리 --> update query
	// 3. 거래 내역 --> insert query
	public void updateAccountDeposit(DepositFormDto depositFormDto) {
		// 1
		Account accountEntity = accountRepository.findByNumber(depositFormDto.getDAccountNumber());

		if(accountEntity == null ) { 
			throw new CustomRestfullException("해당 계좌가 존재하지 않습니다", HttpStatus.BAD_REQUEST);
		}
		// 2
		// 객체 상태값 변경 처리
		accountEntity.deposit(depositFormDto.getAmount());
		accountRepository.updateById(accountEntity);       // update 처리


		// 3
		// 거래 내역 등록 <-- DB 분석

		History history = new History();
		history.setAmount(depositFormDto.getAmount()); 

		history.setWBalance(null);
		// 현재 입금 되었을 때 잔액을 기록
		history.setDBalance(accountEntity.getBalance());		
		history.setWAccountId(null);
		history.setDAccountId(accountEntity.getId());

		int resultRowCount = historyRepository.insert(history);
		if( resultRowCount != 1 ) {
			throw new CustomRestfullException("정상 처리 되지 않았습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// 이체 로직 고민해보기
	// 1. 출금 계좌 존재 여부 확인 - select
	// 2. 입금 계좌 존재 여부 확인 - select
	// 3. 출금 계좌 본인 소유 확인 - 객체 상태값 확인 (id)  /객체(1- select) 
	// 4. 출금 계좌 비번 확인      - TransferFromDto(비번) / 모델 (비번)
	// 5. 출금 계좌 잔액 여부 확인 - DTO / 모델 객체
	// 6. 출금 계좌 잔액 - update
	// 7. 입금 계좌 잔액 - update
	// 8. 거래 내역 등록
	// 9. 트랜잭션 처리
	@Transactional
	public void updateAccountTransfer(TransferFromDto transferFromDto, Integer id) {
		// 1
		Account withdrawAccountEntity = accountRepository.findByNumber(transferFromDto.getWAccountNumber());
		if(withdrawAccountEntity == null) {
			throw new CustomRestfullException("출금 계좌가 존재하지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		// 2
		Account depositAccountEntity = accountRepository.findByNumber(transferFromDto.getDAccountNumber());
		if(depositAccountEntity == null) {
			throw new CustomRestfullException("입금 계좌가 존재하지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		// 3. 출금 계좌 본인 소유 확인
		withdrawAccountEntity.checkOwner(id);
		// 4. 출금 계좌 비번 확인
		withdrawAccountEntity.checkPassword(transferFromDto.getWAccountPassword());
		// 5. 출금 계좌 잔액 여부 확인
		withdrawAccountEntity.checkBalance(transferFromDto.getAmount());
		// 6. 출금 계좌 잔액 상태 값 변경 처리
		withdrawAccountEntity.withdraw(transferFromDto.getAmount());
		// update 처리
		accountRepository.updateById(withdrawAccountEntity);
		// 7. 입금 계좌 잔액 상태 값 변경 처리
		depositAccountEntity.deposit(transferFromDto.getAmount());
		// update 처리
		accountRepository.updateById(depositAccountEntity);
		// 8. 거래 내역 등록
		History history = new History();
		history.setAmount(transferFromDto.getAmount());
		history.setWAccountId(withdrawAccountEntity.getId());
		history.setDAccountId(depositAccountEntity.getId());
		history.setWBalance(withdrawAccountEntity.getBalance());
		history.setDBalance(depositAccountEntity.getBalance());
		
		int resultRowCount = historyRepository.insert(history);
		if(resultRowCount != 1 ) {
			throw new CustomRestfullException("정상 처리 되지 않았습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
}

