package com.tencoding.bank.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tencoding.bank.dto.SignInFormDto;
import com.tencoding.bank.dto.SignUpFormDto;
import com.tencoding.bank.handler.exception.CustomRestfullException;
import com.tencoding.bank.repository.model.User;
import com.tencoding.bank.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired // DI 처리
	private UserService userService;

	@Autowired // DI 처리
	private HttpSession session;

	
	// 회원 가입 페이지 요청
	// http://localhost:80/user/sign-up
	@GetMapping("/sign-up")
	public String signUp() {
		return "user/signUp";
	}

	// 로그인 페이지 요청
	// http://localhost:80/user/sign-in
	@GetMapping("/sign-in")
	public String signIn() {
		return "user/signIn";
	}

	/**
	 * 회원 가입 처리
	 * @param signUpFormDto
	 * @return 리다이렉트 처리 - 로그인 페이지
	 */
	@PostMapping("/sign-up")
	public String signUpProc(SignUpFormDto signUpFormDto) {
		
		// 1. 유효성 검사
		if(signUpFormDto.getUsername() == null 
				|| signUpFormDto.getUsername().isEmpty() ) {
			throw new CustomRestfullException("username을 입력하세요.",
					HttpStatus.BAD_REQUEST);		
		}
		if(signUpFormDto.getPassword() == null
				|| signUpFormDto.getPassword().isEmpty() ) {
			throw new CustomRestfullException("password를 입력하세요.",
					HttpStatus.BAD_REQUEST);	
		}
		if(signUpFormDto.getFullname() == null
				|| signUpFormDto.getFullname().isEmpty() ) {
			throw new CustomRestfullException("fullname을 입력하세요.",
					HttpStatus.BAD_REQUEST);	
		}
		
		// 로직 추가 -- 서비스 호출
		userService.signUp(signUpFormDto);
		return "redirect:/user/sign-in";
	}
	/**
	 * 로그인 로직 처리
	 * @param signInFormDto
	 * @return 계좌 리스트 페이지로 리턴
	 */
	@PostMapping("/sign-in")
	public String signInProc(SignInFormDto signInFormDto) {
		
		// 1. 유효성 검사
		if(signInFormDto.getUsername() == null || 
				signInFormDto.getUsername().isEmpty()) {
			throw new CustomRestfullException("username을 입력하시오", HttpStatus.BAD_REQUEST);
		}
		if(signInFormDto.getPassword() == null || 
				signInFormDto.getPassword().isEmpty()) {
			throw new CustomRestfullException("password를 입력하시오", HttpStatus.BAD_REQUEST);
		}
		// 2. 서비스 -> 인증된 사용자 여부 확인
		// principal <-- 접근 주체
		User principal = userService.signIn(signInFormDto);
		principal.setPassword(null);
		// 3. 쿠키 + 세션 
		session.setAttribute("principal", principal);
		
		return "redirect:/account/list";
	}
	
	/**
	 * 로그아웃 처리
	 * @return 리다이렉트 - 로그인 페이지 이동
	 */
	@GetMapping("/logout")
	public String logout() {
		session.invalidate();
		return "redirect:/user/sign-in";
	}
	
}
