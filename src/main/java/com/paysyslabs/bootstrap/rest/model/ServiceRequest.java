package com.paysyslabs.bootstrap.rest.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class ServiceRequest {

	private String realm;
	private String type;
	private String ref;
	private Map<String, String> parameters;

	public static ServiceRequest fromXML(String xml) {
		ServiceRequest request = new ServiceRequest();
		request.realm = StringUtils.substringBetween(xml, "<realm>", "</realm>");
		request.type = StringUtils.substringBetween(xml, "<type>", "</type>");
		request.ref = StringUtils.substringBetween(xml, "<tran_ref>", "</tran_ref>");
		request.parameters = new HashMap<>();
		return request;
	}

	public ServiceRequest() {
        super();
    }

    public ServiceRequest(String realm, String type, String ref, Map<String, String> parameters) {
        super();
        this.realm = realm;
        this.type = type;
        this.ref = ref;
        this.parameters = parameters;
    }

    public void parseParameters(String xml, List<String> tags) {
		for (String tag : tags) {
			String open = String.format("<%s>", tag);
			String close = String.format("</%s>", tag);

			if (xml.contains(open) && xml.contains(close))
				parameters.put(tag, StringUtils.substringBetween(xml, open, close));
		}
	}

	public String getRealm() {
		return realm;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	@Override
	public String toString() {
		return "ServiceRequest [realm=" + realm + ", type=" + type + ", ref=" + ref + ", parameters=" + parameters
				+ "]";
	}

}
