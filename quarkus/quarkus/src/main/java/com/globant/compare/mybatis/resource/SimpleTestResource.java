package com.globant.compare.mybatis.resource;

import com.globant.compare.entity.SimpleTest;
import com.globant.compare.mybatis.mapper.SimpleTestMapper;

import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/simple")
public class SimpleTestResource {

    @Inject
    SimpleTestMapper mapper;

    @Path("/test/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public SimpleTest getTestItem(@PathParam("id") Long id){
        return mapper.getTestItem(id);
    }

    @Path("/test")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public SimpleTest createTestItem(@FormParam("name") String name, @FormParam("description") String description){
        return mapper.createTestItem(name, description);
    }

    @Path("/test/{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public SimpleTest deleteTestItem(@PathParam("id") Long id){
        return mapper.removeTestItem(id);
    }
}
