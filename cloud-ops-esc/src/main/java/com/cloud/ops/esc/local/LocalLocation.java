package com.cloud.ops.esc.local;

import com.cloud.ops.esc.Location;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Created by ningsheng on 2017/5/27.
 */
@Getter
@Setter
@Builder
public class LocalLocation extends Location {
    private Map<String, Host> host;
}

