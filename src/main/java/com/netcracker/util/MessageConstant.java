package com.netcracker.util;

public class MessageConstant {
    //USER ERROR
    public static final String USER_ERROR_NOT_PRESENT ="user.error.not.present";
    public static final String USER_WITH_ID_NOT_PRESENT="user.error.not.present.with.id";
    public static final String USER_WITH_EMAIL_NOT_PRESENT="user.error.email";

    //REQUEST ERROR
    public static final String REQUEST_ERROR_NOT_EXIST = "request.error.not.exist";
    public static final String REQUEST_ERROR_DELETE_CLOSED = "request.error.delete.closed";
    public static final String REQUEST_ERROR_ALREADY_ASSIGNED = "request.error.already.assigned";
    public static final String REQUEST_ERROR_MUST_FREE = "request.error.must.free";
    public static final String REQUEST_ERROR_UPDATE_ALREADY_ASSIGNED = "request.error.update.already.assigned";

    // SUB REQUEST ERROR
    public static final String SUB_REQUEST_ERROR_PARENT = "subrequest.error.parent";
    public static final String SUB_REQUEST_ERROR_PARENT_IS_SUB_REQUEST = "subrequest.error.parentIsSubrequest";
    public static final String SUB_REQUEST_ERROR_PARENT_CLOSED = "subrequest.error.parentClosed";
    public static final String SUB_REQUEST_ERROR_ILLEGAL_ACCESS = "subrequest.error.illegal.access";

    // STATUS ERROR
    public static final String STATUS_ERROR = "status.error";
    public static final String STATUS_ERROR_INCORRECT = "status.error.incorrect";
    public static final String STATUS_ERROR_NOT_AVAILABLE_FOR_GROUP = "status.error.not.available.for.group";
    public static final String STATUS_ERROR_NOT_AVAILABLE = "status.error.not.available";

    // MANAGER ERROR
    public static final String MANAGER_ERROR_MAIL = "manager.error.email";

    // EMPLOYEE ERROR
    public static final String EMPLOYEE_ERROR_MAIL = "employee.error.email";
    public static final String EMPLOYEE_ERROR_ID = "employee.error.id";

    // REQUEST GROUP ERROR
    public static final String REQUEST_GROUP_ILLEGAL_ACCESS = "request.group.error.illegal.access";
    public static final String REQUEST_GROUP_NOT_EXIST = "request.group.error.not.exist";

    //TOKEN
    public static final String TOKEN_ERROR_EXPIRED = "token.error.expired";

    //PRIORITY
    public static final String PRIORITY_ERROR_ID = "priority.error.id";
}