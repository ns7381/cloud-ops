package com.cloud.ops.dao.modal;

import org.springframework.data.domain.Sort;

/**
 * Created by Nathan on 2017/4/21.
 */
public class SortConstant {
    public static final Sort CREATED_AT = new Sort(Sort.Direction.DESC, "createdAt");
}
