package com.globant.db.mybatis.mapper;

import com.globant.db.entity.SimpleTest;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SimpleTestMapper {

    @Select("SELECT * FROM simple_table WHERE id = #{id}")
    SimpleTest getTestItem(Long id);

    @Select("INSERT INTO simple_table (name, description) VALUES (#{name}, #{description})")
    @Options(useGeneratedKeys = true)
    SimpleTest createTestItem(@Param("name")  String name,@Param("description") String description);

    @Select("DELETE FROM simple_table WHERE id = #{id}")
    SimpleTest removeTestItem(Long id);
}
