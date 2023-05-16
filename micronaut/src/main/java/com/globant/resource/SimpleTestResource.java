package com.globant.resource;


import com.globant.db.entity.SimpleTest;
import com.globant.mapper.SimpleTestMapper;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Singleton
public class SimpleTestResource {

    @Inject
    private SqlSessionFactory sqlSessionFactory;

    public SimpleTest getTestItem(Long id) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            SimpleTestMapper mapper = sqlSession.getMapper(SimpleTestMapper.class);
            SimpleTest item = mapper.getTestItem(id);
            return item;
        }
    }

    public SimpleTest createTestItem(String name, String description) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            SimpleTestMapper mapper = sqlSession.getMapper(SimpleTestMapper.class);
            SimpleTest item = mapper.createTestItem(name, description);
            return item;
        }
    }

    public SimpleTest deleteTestItem(Long id) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            SimpleTestMapper mapper = sqlSession.getMapper(SimpleTestMapper.class);
            SimpleTest item = mapper.removeTestItem(id);
            return item;
        }
    }
}
