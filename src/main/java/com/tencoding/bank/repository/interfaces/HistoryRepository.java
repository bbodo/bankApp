package com.tencoding.bank.repository.interfaces;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.tencoding.bank.dto.HistoryDto;
import com.tencoding.bank.repository.model.History;

@Mapper
public interface HistoryRepository {

	public int insert(History history);
	public int updateById(History history);
	public int deleteById(Integer id);
	public History findById(Integer id);
	public List<History> findAll();
	// !! 중요 - 매개변수가 2개 이상이면 반드시 파라미터 이름을 명시해 주어야 한다.
	public List<HistoryDto> findByHistoryType(@Param("id") Integer id, @Param("type") String type);
	
}