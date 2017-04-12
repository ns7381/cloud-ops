package com.cloud.ops.entity.application;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nathan on 2017/4/8.
 */
@Getter
@Setter
public class LocalLocation {
    String hosts;
    String user;
    String password;
}
