package com.cloud.ops.esc;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ningsheng on 2017/5/26.
 */
public class Location {
    private String locationType;
    private Map<String, Object> metaProperties;

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public Map<String, Object> getMetaProperties() {
        if (metaProperties == null) {
            metaProperties = new HashMap<>();
        }
        return metaProperties;
    }

    public void setMetaProperties(Map<String, Object> metaProperties) {
        this.metaProperties = metaProperties;
    }
}
