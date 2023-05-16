package com.globant.test.benchmark;

import com.globant.db.mybatis.MyBatisConfig;
import com.globant.resource.SimpleTestResource;
import com.globant.utils.Utils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.BasicConfigurator;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@State(Scope.Benchmark)
public class SunHttpServerBenchmark {

    private HttpServer server;
    private SqlSessionFactory sessionFactory;

    @Setup
    public void setup() throws IOException {

        try {
            BasicConfigurator.configure();
            MyBatisConfig myBatisConfig = new MyBatisConfig();
            sessionFactory = myBatisConfig.getSqlSessionFactory();
            runServer(true, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @TearDown
    public void teardown() {
        server.stop(0);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void sunHttpServerBenchmark(Blackhole blackhole) throws IOException {
        for (int i = 0; i < 100; i++) {
            Map<String, String> param = new HashMap<>();
            String s = "simple "+i;
            param.put("name", s);
            param.put("description", "desc"+ s);
            String response = null;
            try {
                response = sendRequest("POST", "http://localhost:8080/simple/test", param);
                blackhole.consume(response);
                response = sendRequest("GET", "http://localhost:8080/simple/test/"+ (i+1), null);
                blackhole.consume(response);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            blackhole.consume(response);
        }
    }

    private String sendRequest(String requestMethod, String targetUrl, Map<String, String> parameters)
            throws IOException, URISyntaxException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        HttpRequest request = null;
        switch (requestMethod){
            case "GET":
                request =  builder.uri(new URI(targetUrl)).GET().build();
                break;
            case "POST":
                String form = parameters.entrySet()
                        .stream()
                        .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                        .collect(Collectors.joining("&"));
                request =  builder
                        .uri(new URI(targetUrl))
                        .POST(HttpRequest.BodyPublishers.ofString(form))
                        .GET().build();
                break;
            case "DELETE":
                request =  builder.uri(new URI(targetUrl)).DELETE().build();
                break;

        }
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
    private void runServer(boolean virtual, boolean withLock) throws IOException {
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

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}







