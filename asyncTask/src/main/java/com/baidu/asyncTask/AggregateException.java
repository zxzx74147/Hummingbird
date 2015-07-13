/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.asyncTask;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Aggregate Exception (多个错误集合)
 * Created by chenrensong on 15/02/04.
 */
public class AggregateException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE = "There were multiple errors.";

    private List<Throwable> innerThrowables;

    public AggregateException(String detailMessage, Throwable[] innerThrowables) {
        this(detailMessage, Arrays.asList(innerThrowables));
    }

    public AggregateException(String detailMessage, List<? extends Throwable> innerThrowables) {
        super(detailMessage,
                innerThrowables != null && innerThrowables.size() > 0 ? innerThrowables.get(0) : null);
        this.innerThrowables = Collections.unmodifiableList(innerThrowables);
    }

    public AggregateException(List<? extends Throwable> innerThrowables) {
        this(DEFAULT_MESSAGE, innerThrowables);
    }


}

