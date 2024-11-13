package com.paysyslabs.bootstrap.rest.helper;

import com.paysyslabs.bootstrap.rest.entities.WSResponseDefinition;
import com.paysyslabs.bootstrap.rest.model.ServiceBasicResponse;

public class ResponseMaker {

    public static String make(String ref, String code, String description) {
        return make(ref, null, null, code, description);
    }
    
    public static String make(String ref, String responseTag, ServiceBasicResponse response, WSResponseDefinition definition) {
        return make(ref, responseTag, response, definition.getOurCode(), definition.getOurDescription());
    }

    public static String make(String ref, String responseTag, ServiceBasicResponse response, String code, String description) {
        StringBuilder builder = new StringBuilder();
        builder.append("<response>");
        builder.append(String.format("<response_code>%s</response_code>", code));
        builder.append(String.format("<response_desc>%s</response_desc>", description));
        
        if (ref != null)
            builder.append(String.format("<tran_ref>%s</tran_ref>", ref));
        
        if (responseTag != null && response != null)
            builder.append(String.format("<%s>%s</%s>", responseTag.toLowerCase(), response.getBody(), responseTag.toLowerCase()));
        
        builder.append("</response>");
        return builder.toString();
    }
}
