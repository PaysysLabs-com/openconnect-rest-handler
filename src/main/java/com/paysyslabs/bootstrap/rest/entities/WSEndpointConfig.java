package com.paysyslabs.bootstrap.rest.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.paysyslabs.bootstrap.rest.enums.WSRequestFormat;
import com.paysyslabs.bootstrap.rest.enums.WSResponseFormat;

@Entity
@Table(name = "ws_endpoint_config")
public class WSEndpointConfig {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "config_id", nullable = false)
    private WSConfig config;

    @Column(name = "type")
    private String type;

    @Column(name = "endpoint_template")
    private String endpointTemplate;

    @Column(name = "fields", length = 500)
    private String expectedFields;

    @Column(name = "variable_fields", length = 500)
    private String variableFields;

    @Column(name = "data_template", length = 4000)
    private String dataTemplate;

    @Column(name = "request_headers", length = 4000)
    private String requestHeaders;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "request_format")
    private WSRequestFormat requestFormat;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "response_format")
    private WSResponseFormat responseFormat;

    @Column(name = "response_code_path")
    private String responseCodePath;

    @Column(name = "response_include_paths")
    private String responseIncludePaths;

    @Column(name = "guaranteed")
    private Boolean guaranteed;

    @Column(name = "reversal_type")
    private String reversalType;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(joinColumns = {
        @JoinColumn(name = "endpoint_id", referencedColumnName = "id")
    }, inverseJoinColumns = {
        @JoinColumn(name = "dependent_endpoint_id", referencedColumnName = "id")
    })
    private List<WSEndpointConfig> dependents;
    
    @ManyToOne
    @JoinColumn(name = "token_request_id")
    private WSEndpointConfig tokenRequest;

    @ManyToOne
    @JoinColumn(name = "token_configuration_id")
    private WSTokenConfiguration tokenConfiguration;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WSConfig getConfig() {
        return config;
    }

    public void setConfig(WSConfig config) {
        this.config = config;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEndpointTemplate() {
        return endpointTemplate;
    }

    public void setEndpointTemplate(String endpointTemplate) {
        this.endpointTemplate = endpointTemplate;
    }

    public String getExpectedFields() {
        return expectedFields;
    }

    public void setExpectedFields(String expectedFields) {
        this.expectedFields = expectedFields;
    }

    public String getVariableFields() {
		return variableFields;
	}

	public void setVariableFields(String variableFields) {
		this.variableFields = variableFields;
	}

	public String getDataTemplate() {
        return dataTemplate;
    }

    public void setDataTemplate(String dataTemplate) {
        this.dataTemplate = dataTemplate;
    }

    public WSRequestFormat getRequestFormat() {
        return requestFormat;
    }

    public void setRequestFormat(WSRequestFormat requestFormat) {
        this.requestFormat = requestFormat;
    }

    public WSResponseFormat getResponseFormat() {
        return responseFormat;
    }

    public void setResponseFormat(WSResponseFormat responseFormat) {
        this.responseFormat = responseFormat;
    }

    public String getResponseCodePath() {
        return responseCodePath;
    }

    public void setResponseCodePath(String responseCodePath) {
        this.responseCodePath = responseCodePath;
    }

    public String getResponseIncludePaths() {
        return responseIncludePaths;
    }

    public void setResponseIncludePaths(String responseIncludePaths) {
        this.responseIncludePaths = responseIncludePaths;
    }

    public String getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(String requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public List<WSEndpointConfig> getDependents() {
        return dependents;
    }

    public void setDependents(List<WSEndpointConfig> dependents) {
        this.dependents = dependents;
    }

    public List<String> getExpectedFieldList() {
        if (expectedFields == null)
            return new ArrayList<>();

        return Arrays.asList(expectedFields.split(","));
    }

    public List<String> getVariableFieldList() {
        if (variableFields == null)
            return new ArrayList<>();

        return Arrays.asList(variableFields.split(","));
    }

    public Boolean getGuaranteed() {
        return guaranteed;
    }

    public void setGuaranteed(Boolean guaranteed) {
        this.guaranteed = guaranteed;
    }

    public Map<String, String> getRequestHeadersMap() throws Exception {
        Map<String, String> map = new HashMap<>();
        if (requestHeaders == null)
            return map;

        String[] headers = requestHeaders.split("[|][|]");
        for (String header : headers) {
            String[] headerData = header.split("[:]", 2);
            
            if (headerData.length < 2)
            	throw new Exception("Bad header value = " + header);
            
            map.put(headerData[0].toString().trim(), headerData[1].toString().trim());
        }

        return map;
    }

    public List<String> getRequestHeadersList() {
        List<String> headers = new ArrayList<>();

        if (requestHeaders == null)
            return headers;

        String[] array = requestHeaders.split("[|][|]");

        for (String header : array) {
            headers.add(header.trim());
        }

        return headers;
    }

    public WSEndpointConfig getTokenRequest() {
        return tokenRequest;
    }

    public void setTokenRequest(WSEndpointConfig tokenRequest) {
        this.tokenRequest = tokenRequest;
    }

    public WSTokenConfiguration getTokenConfiguration() {
        return tokenConfiguration;
    }

    public void setTokenConfiguration(WSTokenConfiguration tokenConfiguration) {
        this.tokenConfiguration = tokenConfiguration;
    }

    public String getReversalType() {
		return reversalType;
	}

	public void setReversalType(String reversalType) {
		this.reversalType = reversalType;
	}

	@Override
	public String toString() {
		return "WSEndpointConfig [id=" + id + ", config=" + config + ", type=" + type + ", endpointTemplate="
				+ endpointTemplate + ", expectedFields=" + expectedFields + ", variableFields=" + variableFields
				+ ", dataTemplate=" + dataTemplate + ", requestHeaders=" + requestHeaders + ", requestFormat="
				+ requestFormat + ", responseFormat=" + responseFormat + ", responseCodePath=" + responseCodePath
				+ ", responseIncludePaths=" + responseIncludePaths + ", guaranteed=" + guaranteed + ", reversalType="
				+ reversalType + ", dependents=" + dependents + ", tokenRequest=" + tokenRequest
				+ ", tokenConfiguration=" + tokenConfiguration + "]";
	}
}
