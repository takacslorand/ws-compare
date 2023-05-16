package com.globant.compare.spring.boot.mybatis.resource;


import com.globant.compare.spring.boot.entity.SimpleTest;
import com.globant.compare.spring.boot.mybatis.mapper.SimpleTestMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/simple/test")
public class SimpleTestResource {

    @Autowired
    SimpleTestMapper mapper;

    @GetMapping("/{id}")
    public SimpleTest getTestItem(@PathVariable("id") Long id){
        return mapper.getTestItem(id);
    }

    @PostMapping
    public SimpleTest createTestItem(@RequestParam("name") String name, @RequestParam("description") String description){
        return mapper.createTestItem(name, description);
    }

    @DeleteMapping("/{id}")
    public SimpleTest deleteTestItem(@PathVariable("id") Long id){
        return mapper.removeTestItem(id);
    }
}
