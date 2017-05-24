package com.cloud.ops.entity.location;

import lombok.*;

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
