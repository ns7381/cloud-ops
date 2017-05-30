package com.cloud.ops.esc.local.model;

import com.cloud.ops.esc.Location;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Created by ningsheng on 2017/5/27.
 */
@Getter
@Setter
public class LocalLocation extends Location {
    private Map<String, Host> host;
}

