package com.cloud.ops.entity.application;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by Nathan on 2017/4/8.
 */
@Getter
@Setter
@Builder
public class LocalLocation {
    List<String> hosts;
    String user;
    String password;
}
