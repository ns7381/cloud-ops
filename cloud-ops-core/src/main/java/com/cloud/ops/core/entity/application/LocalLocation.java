package com.cloud.ops.core.entity.application;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class LocalLocation {
    List<String> hosts;
    String user;
    String password;
}
