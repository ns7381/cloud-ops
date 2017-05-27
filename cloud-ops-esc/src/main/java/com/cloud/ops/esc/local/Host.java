package com.cloud.ops.esc.local;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by ningsheng on 2017/5/27.
 */
@Getter
@Setter
@Builder
public class Host {
    List<String> ips;
    String user;
    String password;
}
