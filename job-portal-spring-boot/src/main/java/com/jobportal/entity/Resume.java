package com.jobportal.entity;

import jakarta.persistence.Embeddable;


@Embeddable
public class Resume {
    private String public_id;
    private String url;
    
    public String getPublic_id() { return public_id; }
    public void setPublic_id(String public_id) { this.public_id = public_id; }
    
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
