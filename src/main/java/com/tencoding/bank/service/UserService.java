package com.tencoding.bank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
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
	
	@Autowired
	private PasswordEncoder passwordEncoder;
		
	
	// 트랜잭션 사용하는 이유는 정상 처리 되면 commit(반영)
	// 정상 처리가 안되면 Rollback 처리 됨
	
	/** 
	 * 회원가입
	 * 비밀번호 암호화 처리
	 * @param signUpFormDto
	 */
	@Transactional
	public void signUp(SignUpFormDto signUpFormDto) {
		
		String rawPwd = signUpFormDto.getPassword();
		String hashPwd = passwordEncoder.encode(rawPwd);
		System.out.println("rawPwd : " + rawPwd);
		System.out.println("hashPwd : " + hashPwd);		
		signUpFormDto.setPassword(hashPwd);
		int result = userRepository.insert(signUpFormDto);
		System.out.println("result : " + result);
		if(result != 1) {
			throw new CustomRestfullException("회원가입실패",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	// 로그인 서비스 처리
	public User signIn(SignInFormDto signInFormDto) {
		

		// 계정 이름만 확인으로 변경 처리
		User userEntity = userRepository.findByUsername(signInFormDto.getUsername());
		
		
		// 계정 확인
		// 계정이 존재 하지 않습니다.
		if(userEntity == null || 
				userEntity
				.getUsername().equals(signInFormDto.getUsername()) == false) {
			throw new CustomRestfullException("존재하지 않는 계정입니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	
		// 비번 확인
		// 비밀번호가 틀렸습니다.
		boolean isPwdMatched = passwordEncoder
				.matches(signInFormDto.getPassword(), userEntity.getPassword());
		
		if(isPwdMatched == false) {
			throw new CustomRestfullException("너 잘못했어.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
						
	
		return userEntity;	
	}
	
	/**
	 * username 사용자 검색
	 * @param username
	 * @return User, null
	 */
	public User searchUsername(String username) {
	  return userRepository.findByUsername(username);
	}

	
	
}
