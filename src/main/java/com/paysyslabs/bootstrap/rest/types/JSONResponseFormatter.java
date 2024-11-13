package com.paysyslabs.bootstrap.rest.types;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.paysyslabs.bootstrap.rest.entities.WSEndpointConfig;
import com.paysyslabs.bootstrap.rest.model.ServiceBasicResponse;
import com.paysyslabs.bootstrap.rest.utils.XML;

public class JSONResponseFormatter implements ResponseFormatter {

    @Override
    public ServiceBasicResponse format(HttpResponse<String> response, WSEndpointConfig config) throws Exception {
        JSONObject json = new JSONObject(response.getBody());
        String code = "NA";

        if (!StringUtils.isEmpty(config.getResponseCodePath())) {
            JSONObject codeObject = json;
            String[] paths = config.getResponseCodePath().split(">");

            for (int i = 0; i < paths.length - 1; i++)
                codeObject = codeObject.getJSONObject(paths[i]);

            if (codeObject.has(paths[paths.length - 1])) {
                code = codeObject.get(paths[paths.length - 1]).toString();
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
                    Object object = json;

                    String subPaths[] = path.split(">");

                    for (String subPath : subPaths)
                        if (object instanceof JSONObject)
                            object = ((JSONObject) object).get(subPath);

                    JSONObject wrapper = new JSONObject();
                    wrapper.put(subPaths[subPaths.length - 1], object);

                    data.put(subPaths[subPaths.length - 1], object.toString());

                    builder.append(XML.toString(wrapper));

                }
        } catch (Exception e) {

        }

        return new ServiceBasicResponse(code, builder.toString(), data);
    }

}