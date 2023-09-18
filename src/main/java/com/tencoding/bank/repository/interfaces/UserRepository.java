package com.tencoding.bank.repository.interfaces;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.tencoding.bank.dto.SignInFormDto;
import com.tencoding.bank.dto.SignUpFormDto;
import com.tencoding.bank.repository.model.User;

// ibatis -> 2.4 버전 이후로 mybatis 로 이름이 바뀜.
@Mapper  // Mapper 반드시 기술해줘야 동작한다.
public interface UserRepository {
	
	// 매개변수 수정
	public int insert(SignUpFormDto dto);
	public int updateById(User user);
	public int deleteById(User user);
	public User findById(Integer id);
	public User findByUsernameAndPassword(SignInFormDto signInFormDto);
	//관리자
	public List<User> findAll();
	
}
