package com.globant.utils;

import com.globant.db.entity.SimpleTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.pathtemplate.PathTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

public class Utils {


    public static Map<String, List<String>> splitQuery(String query) {
        if (query == null || "".equals(query)) {
            return Collections.emptyMap();
        }

        return Pattern.compile("&").splitAsStream(query)
                .map(s -> Arrays.copyOf(s.split("="), 2))
                .collect(groupingBy(s -> decode(s[0]), mapping(s -> decode(s[1]), toList())));
    }

    public static Map<String, String> getPathParameters(String uri, String strUrlTemplate) {
        Map<String, String> params = new HashMap<>();
        PathTemplate template =  PathTemplate.create(strUrlTemplate);
        try {
//            TemplatedResourceName templatedResourceName = TemplatedResourceName.create(template, uri);
//            templatedResourceName.keySet().stream().forEach(k -> params.put(k, templatedResourceName.get(k)));
            params = template.match(uri.substring(1));
        }catch (Exception e){
            e.printStackTrace();
        }
        return params;
    }

    public static String asJsonString(SimpleTest item) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(item);
    }

    private static String decode(final String encoded) {
        try {
            return encoded == null ? null : URLDecoder.decode(encoded, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 is a required encoding", e);
        }
    }


    public static String getBodyAsString(InputStream stream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(stream, "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        StringBuilder stringBuilder = new StringBuilder();
        int b;
        while ((b=bufferedReader.read()) !=-1){
            stringBuilder.append((char) b);
        }
        bufferedReader.close();
        inputStreamReader.close();
        return stringBuilder.toString();
    }
}
