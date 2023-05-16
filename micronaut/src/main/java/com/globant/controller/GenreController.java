package com.globant.controller;

import com.globant.db.entity.SimpleTest;
import com.globant.resource.SimpleTestResource;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;


@Controller("/simple/test")
public class GenreController {

    private final SimpleTestResource resource;

    public GenreController(SimpleTestResource resource) {
        this.resource = resource;
    }

    @Get("/{id}") 
    public HttpResponse<SimpleTest> get(long id) {
        return HttpResponse.created(resource.getTestItem(id));
    }

    @Post(consumes =  MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<SimpleTest> create(@Body SimpleTest input) {
        resource.createTestItem(input.getName(), input.getDescription());
        return HttpResponse.created(input);
    }

    @Delete("/{id}") 
    public HttpResponse<?> delete(long id) {
        resource.deleteTestItem(id);
        return HttpResponse.noContent();
    }
}