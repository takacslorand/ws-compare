package com.globant.resource;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import com.globant.db.mybatis.mapper.SimpleTestMapper;
import com.globant.db.entity.SimpleTest;

@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SimpleTestResource {

    private SqlSession session;

    public SimpleTest getTestItem(Long id) {

        SimpleTestMapper mapper = session.getMapper(SimpleTestMapper.class);
        SimpleTest item = mapper.getTestItem(id);
        return item;
    }

    public SimpleTest createTestItem(String name, String description) {

        SimpleTestMapper mapper = session.getMapper(SimpleTestMapper.class);
        SimpleTest item = mapper.createTestItem(name, description);
        return item;
    }

    public SimpleTest deleteTestItem(Long id) {
        SimpleTestMapper mapper = session.getMapper(SimpleTestMapper.class);
        SimpleTest item = mapper.removeTestItem(id);
        return item;
    }
}
