package com.tencoding.bank.handler;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tencoding.bank.handler.exception.CustomRestfullException;

/**
 *  예외 발생 시 (Json, Xml)
 *  데이터를 가공해서 내려 줄 수 있다.
 *   
 */
@RestControllerAdvice // IoC 대상 + Aop 대상
@Order(1)
public class MyRestfullExceptionHandler {
	
	@ExceptionHandler(Exception.class)
	public void exception(Exception e) {
		System.out.println("==== 예외 발생 확인 ====");
		System.out.println(e.getMessage());
		System.out.println("------------------------");
	}
	
	// 사용자 정의 예외 클래스 활용
	@ExceptionHandler(CustomRestfullException.class)
	public String basicException(CustomRestfullException e) {
		StringBuffer sb = new StringBuffer();
		sb.append("<script>");
		sb.append("alert( ' "  + e.getMessage() +" ' );");  // 문자열 안에 반드시 ; 클론 붙이기
		sb.append("history.back();");
		sb.append("</script>");
		return sb.toString();
	}
	
	
}
