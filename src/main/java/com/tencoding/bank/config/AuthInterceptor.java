package com.tencoding.bank.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.tencoding.bank.handler.exception.UnAuthorizedException;
import com.tencoding.bank.repository.model.User;
import com.tencoding.bank.utils.Define;

@Component // IoC 대상 - 싱글톤으로 관리 된다.
public class AuthInterceptor implements HandlerInterceptor{
	
	// 컨트롤러 들어가기 전에 호출 되는 메서드
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		System.out.println("preHandle() 메서드 호출");
		HttpSession session  = request.getSession();
		User principal = (User)session.getAttribute(Define.PRINCIPAL);
		if(principal == null) {
			throw new UnAuthorizedException("로그인 먼저 해주세요",
					HttpStatus.UNAUTHORIZED);
		}
		
		return true;
		}
	
	// 뷰가 렌더링 되기 전에 호출 되는 메서드
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}
	
//  요청 처리가 완료된 후, 즉 뷰 렌더링이 완료된 후에 호출 되는 메서드
//	@Override
//	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
//			@Nullable Exception ex) throws Exception {
//	}
	
	
}
