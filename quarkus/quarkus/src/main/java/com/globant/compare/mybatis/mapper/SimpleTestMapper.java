package com.globant.compare.mybatis.mapper;

import com.globant.compare.entity.SimpleTest;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SimpleTestMapper {

    @Select("SELECT * FROM simple_table WHERE id = #{id}")
    SimpleTest getTestItem(Long id);

    @Select("INSERT INTO simple_table (name, description) VALUES (#{name}, #{description})")
    @Options(useGeneratedKeys = true)
    SimpleTest createTestItem(String name, String description);

    @Select("DELETE FROM simple_table WHERE id = #{id}")
    SimpleTest removeTestItem(Long id);
}
