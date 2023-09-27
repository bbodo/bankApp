package com.tencoding.bank.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.tencoding.bank.dto.KakaoProfile;
import com.tencoding.bank.dto.OAuthToken;
import com.tencoding.bank.dto.SignInFormDto;
import com.tencoding.bank.dto.SignUpFormDto;
import com.tencoding.bank.handler.exception.CustomRestfullException;
import com.tencoding.bank.repository.model.User;
import com.tencoding.bank.service.UserService;
import com.tencoding.bank.utils.Define;

@Controller
@RequestMapping({"/user", ""})
public class UserController {
	
	@Value("{tenco.key}")
	private String tencoKey;
	
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
	@GetMapping({"/sign-in", ""})
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
		session.setAttribute(Define.PRINCIPAL, principal);
		
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
	
	// http://localhost:80/user/kakao/callback?code="ZESasa"
	@GetMapping("kakao/callback")
	// @ResponseBody // 이녀석은 data 반환 명시
	public String kakaoCallback(@RequestParam String code) { // 토큰이 여기 code로 닮김
		System.out.println("메서드 동작");
		
		// POST 방식 - exchange() 메서드 활용
		// Header 헤더 구성
		// body 구성
		
		RestTemplate rt = new RestTemplate();
		// 헤더 구성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");    // 문서 토큰란에헤더 형식 키,value 값 가져와서 복붙
		
		
		
		// body 구성
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");		
		params.add("client_id", "735aacbb448560cb2d171cf2767889bb");		// value 값에 아까 발급받아서 복사해둔 RESTAPI 넣으란거임
		params.add("redirect_uri", "http://localhost:80/user/kakao/callback");	// value 값에 아까 발급받아서 복사해둔 RESTURI 넣으란거임	
		params.add("code", code); 
		
		// HttpEntity 결합 (헤더 + 바디)
		HttpEntity<MultiValueMap<String, String>> reqMes 
							= new HttpEntity<>(params, headers);
		
		// Http 요청
		ResponseEntity<OAuthToken> response  = rt.exchange("https://kauth.kakao.com/oauth/token",
				HttpMethod.POST, reqMes, OAuthToken.class);     // 토큰 받기에 있던 적혀있던 복사한 URL 넣어주는 곳, POST로 받겠다고 되있었음
		
		// 1. DTO 파싱
		System.out.println("액세트 토근 확인 " + response.getBody().toString());
		// 액세스 토큰
		// 엑세스 토근 --> 카카오 서버 (정보)
		
		// 문서 확인 - 정보 요청 주소 
		System.out.println("--------------------------------");
		RestTemplate ret2 = new RestTemplate();
		
		// 헤더 생성
		HttpHeaders headers2 = new HttpHeaders();   // response.getBody().getAccessToken() --> 액세스 토큰
		headers2.add("Authorization", "Bearer " + response.getBody().getAccessToken()); // Bearer 뒤에 공백 한칸도 같이 복사해옴
		headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8"); 		
		// 바디 생성 - 생략 , GET방식은 바디 없어도 됨
		HttpEntity<MultiValueMap<String, String>> kokaoInfo
		= new HttpEntity<>(headers2); // 바디 없이 헤더만 추가
		
		// HTTP 요청
		ResponseEntity<KakaoProfile> response2 = ret2.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.POST, 
				kokaoInfo, KakaoProfile.class); 
		System.out.println("----------------------");
		System.out.println(response2.getBody().getKakaoAccount().getEmail());
		
		System.out.println("------------카카오 서버에 정보 받기 완료---------------");
		
		
		// 1. 회원 가입 여부 확인 - 최초 사용자 라면
		// - 최초 사용자 라면 우리 회원 가입 맞는 형식을 만들어서 회원 가입 처리
		// DB -> user_tb --> username, password, fullname
		// password <-- 직접 만들어서 넣어야 합니다.
		// 소셜 로그인 사용자는 모든 패스워드가 동일 합니다.
		
		// username -> 동일한 값이 저장되지 않도록 처리
		
		KakaoProfile kakaoProfile = response2.getBody();
		
		SignUpFormDto signUpFormDto = SignUpFormDto
				.builder()
				.username(kakaoProfile.getKakaoAccount().getEmail() + "_" + kakaoProfile.getId())
				.fullname("OAuth_" + kakaoProfile.getKakaoAccount().getEmail())
				.password(tencoKey)
				.build();
		
		
		User olderUser = userService.searchUsername(signUpFormDto.getUsername());
		if(olderUser == null) {
			// 사용자가 최초 소셜 로그인 사용자면 자동 회원 가입처리
			userService.signUp(signUpFormDto);
			olderUser.setUsername(signUpFormDto.getUsername());
			olderUser.setFullname(signUpFormDto.getFullname());
			
		}
		olderUser.setPassword(null);
		// null 아닌 경우 처리
		// 그게 아니라면 바로 세션에 데이터 등록 로그인 처리
		session.setAttribute(Define.PRINCIPAL,olderUser); // 로그인 처리
		
		 
		System.out.println("signUpFormDto : " + signUpFormDto);
				
		return "redirect:/account/list";
		
	}
	
	
	
	
}
