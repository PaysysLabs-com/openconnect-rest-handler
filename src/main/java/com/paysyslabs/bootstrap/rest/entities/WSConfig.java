package com.paysyslabs.bootstrap.rest.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "ws_config")
public class WSConfig {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type")
    private String type;

    @Column(name = "base_url")
    private String baseUrl;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "config", fetch = FetchType.EAGER)
    private List<WSEndpointConfig> endpointConfigurations;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public List<WSEndpointConfig> getEndpointConfigurations() {
        return endpointConfigurations;
    }

    public void setEndpointConfigurations(List<WSEndpointConfig> endpointConfigurations) {
        this.endpointConfigurations = endpointConfigurations;
    }

}
