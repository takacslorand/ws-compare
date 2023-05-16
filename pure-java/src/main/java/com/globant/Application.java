package com.globant;

import com.globant.resource.SimpleTestResource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.BasicConfigurator;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import com.globant.db.mybatis.MyBatisConfig;
import com.globant.utils.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@State(Scope.Benchmark)
public class Application {

    private final static Executor EXECUTOR = Executors.newFixedThreadPool(5);
    private static SqlSessionFactory sessionFactory;

    public static void main(String[] args) {

        try {
            BasicConfigurator.configure();
            MyBatisConfig myBatisConfig = new MyBatisConfig();
            sessionFactory = myBatisConfig.getSqlSessionFactory();
            runServer(true, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static void runServer(boolean virtual, boolean withLock) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        MyBatisConfig myBatisConfig = new MyBatisConfig();
        httpServer.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {

                String method = exchange.getRequestMethod();
                String response;

                try (SqlSession session = sessionFactory.openSession()) {
                    SimpleTestResource resource = SimpleTestResource
                            .builder()
                            .session(session)
                            .build();
                    switch (method) {
                        case "GET":
                            writeResponse(exchange, Utils.asJsonString(resource.getTestItem(getId(exchange))));
                            //                            CompletableFuture.supplyAsync(() -> {
                            //                                try {
                            //                                    return Utils.asJsonString(resource.getTestItem(getId(exchange)));
                            //                                } catch (JsonProcessingException e) {
                            //                                    e.printStackTrace();
                            //                                    return null;
                            //                                }
                            //                            }, EXECUTOR).thenAccept(r -> {
                            //                                try {
                            //                                    writeResponse(exchange, r);
                            //                                } catch (IOException e) {
                            //                                    e.printStackTrace();
                            //                                }
                            //                            });
                            break;
                        case "POST":
                            String query = Utils.getBodyAsString(exchange.getRequestBody());
                            Map<String, List<String>> params = Utils.splitQuery(query);
                            resource.createTestItem(params.get("name").get(0), params.get("description").get(0));
                            writeResponse(exchange, "saved");
                            break;
                        case "DELETE":
                            resource.deleteTestItem(getId(exchange));
                            response = String.format("Simple item removed");
                            writeResponse(exchange, response);
                            break;
                    }
                } catch (Exception e) {
                    writeResponse(exchange, String.format("Exception: %s", e.getLocalizedMessage()));
                }
            }
        });
        httpServer.setExecutor(Executors.newFixedThreadPool(10));
        httpServer.start();
    }

    private static void writeResponse(HttpExchange exchange, String response) throws IOException {
        exchange.sendResponseHeaders(200, response.length());
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private static Long getId(HttpExchange exchange) {
        Map<String, String> pathParams =
                Utils.getPathParameters(exchange.getRequestURI().toString(), "simple/test/{id}");
        return Long.parseLong(pathParams.get("id"));
    }
}