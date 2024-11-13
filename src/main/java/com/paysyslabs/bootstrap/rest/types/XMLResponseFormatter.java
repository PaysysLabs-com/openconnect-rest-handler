package com.paysyslabs.bootstrap.rest.types;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.mashape.unirest.http.HttpResponse;
import com.paysyslabs.bootstrap.rest.entities.WSEndpointConfig;
import com.paysyslabs.bootstrap.rest.model.ServiceBasicResponse;

public class XMLResponseFormatter implements ResponseFormatter {

    @Override
    public ServiceBasicResponse format(HttpResponse<String> response, WSEndpointConfig config) throws Exception {
        Document document = Jsoup.parse(response.getBody());
        document.outputSettings().indentAmount(0).prettyPrint(false);
        String code = "NA";

        if (!StringUtils.isEmpty(config.getResponseCodePath())) {
            if (document.select(config.getResponseCodePath()).first() != null) {
                code = document.select(config.getResponseCodePath()).first().text().trim();
            } else {
                code = Integer.toString(response.getStatus());
            }
        } else {
            code = Integer.toString(response.getStatus());
        }

        StringBuilder builder = new StringBuilder();

        Map<String, Object> data = new HashMap<>();

        try {
            if (config.getResponseIncludePaths() != null)
                for (String path : config.getResponseIncludePaths().split(",")) {
                    String[] parts = path.split(">");

                    String key = parts[parts.length - 1];
                    builder.append(document.select(path).first().toString());
                    data.put(key, document.select(path).first().html().trim());
                }
        } catch (Exception e) {

        }

        return new ServiceBasicResponse(code, builder.toString(), data);
    }

}
