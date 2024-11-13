package com.paysyslabs.bootstrap.rest.types;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.mashape.unirest.http.HttpResponse;
import com.paysyslabs.bootstrap.rest.entities.WSEndpointConfig;
import com.paysyslabs.bootstrap.rest.model.ServiceBasicResponse;

@SuppressWarnings("deprecation")
public class NBPXMLResponseFormatter implements ResponseFormatter {

    @Override
    public ServiceBasicResponse format(HttpResponse<String> response, WSEndpointConfig config) throws Exception {

        String responseBody = StringEscapeUtils.unescapeXml(response.getBody());
        Document document = Jsoup.parse(responseBody);
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

        if (!code.equals("NO ERROR"))
            return new ServiceBasicResponse(code, null);

        StringBuilder builder = new StringBuilder();

        try {
            if (config.getResponseIncludePaths() != null)
                for (String path : config.getResponseIncludePaths().split(","))
                    builder.append(document.select(path).first().toString());
        } catch (Exception e) {

        }

        return new ServiceBasicResponse(code, builder.toString());
    }

}
