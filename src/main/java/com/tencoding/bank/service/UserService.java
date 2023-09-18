package com.tencoding.bank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tencoding.bank.dto.SignInFormDto;
import com.tencoding.bank.dto.SignUpFormDto;
import com.tencoding.bank.handler.exception.CustomRestfullException;
import com.tencoding.bank.repository.interfaces.UserRepository;
import com.tencoding.bank.repository.model.User;

@Service //Ioc 대상 - 싱글톤 패턴
public class UserService {
	
	// DAO - 데이터 베이스 연습
	@Autowired
	private UserRepository userRepository;
	
	// DI - 가지고 오다
//	public UserService(UserRepository userRepository) {
//		this.userRepository = userRepository;
//	} 
//  @Autowired랑 같은뜻
	
	// 트랜잭션 사용하는 이유는 정상 처리 되면 commit(반영)
	// 정상 처리가 안되면 Rollback 처리 됨
	@Transactional
	public void signUp(SignUpFormDto signUpFormDto) {
		int result = userRepository.insert(signUpFormDto);
		
		System.out.println("result : " + result);
		if(result != 1) {
			throw new CustomRestfullException("회원가입실패",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	// 로그인 서비스 처리
	public User signIn(SignInFormDto signInFormDto) {
		
		User userEntity = userRepository.findByUsernameAndPassword(signInFormDto);
		if(userEntity == null ) {
			throw new CustomRestfullException("아이디 혹은 비밀번호가 틀렸습니다.", 
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return userEntity;	
	}
	
}
