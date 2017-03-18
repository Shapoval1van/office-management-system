package com.netcracker.exception.requestGroup;

import com.netcracker.exception.BaseException;

/**
 * Created by Max on 17.03.2017.
 */
public class RequestGroupAlreadyExist extends BaseException {

    private static final String ERROR_MESSAGE = "Already exist";

    public RequestGroupAlreadyExist(String description) {
        super(ERROR_MESSAGE, description);
    }
}
