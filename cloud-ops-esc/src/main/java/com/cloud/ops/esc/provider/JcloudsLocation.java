package com.cloud.ops.esc.provider;

/**
 * Created by ningsheng on 2017/5/26.
 */
public abstract class JcloudsLocation {
    private String endpoint;
    private String identity;
    private String credential;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }
}
