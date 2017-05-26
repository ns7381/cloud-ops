package com.cloud.ops.esc;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Created by ningsheng on 2017/5/26.
 */
@Getter
@Setter
public class Location {
    private String locationType;
    private Map<String, String> metaProperties;
}
