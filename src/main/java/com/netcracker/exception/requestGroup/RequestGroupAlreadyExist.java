package com.netcracker.exception.requestGroup;

public class RequestGroupAlreadyExist extends CannotCreateRequestGroupException {

    public RequestGroupAlreadyExist(String description) {
        super(description);
    }
}
