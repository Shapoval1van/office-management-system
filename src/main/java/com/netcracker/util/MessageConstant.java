package com.netcracker.util;

public class MessageConstant {
    //USER ERROR
    public static final String USER_ERROR_NOT_PRESENT = "user.error.not.present";
    public static final String USER_WITH_ID_NOT_PRESENT = "user.error.not.present.with.id";
    public static final String USER_ALREADY_DELETED ="user.deleted.error";
    public static final String USER_SUCCESFULLY_DELETED="admin.success.removing.message";
    public static final String USER_WITH_EMAIL_NOT_PRESENT = "user.error.email";
    public static final String USER_ERROR_UPDATE_CURRENT_ADMIN = "user.error.update.current.admin";
    public static final String USER_ERROR_UPDATE_FROM_MANAGER_TO_EMPLOYEE = "user.error.update.from.manager.to.employee";
    public static final String USER_ERROR_UPDATE_FROM_ADMIN_TO_EMPLOYEE = "user.error.update.from.admin.to.employee";


    //REQUEST ERROR
    public static final String REQUEST_ERROR_NOT_EXIST = "request.error.not.exist";
    public static final String REQUEST_ERROR_DELETE_CANCELED = "request.error.delete.canceled";
    public static final String REQUEST_ERROR_DELETE_CLOSED = "request.error.delete.closed";
    public static final String REQUEST_ERROR_DELETE_NOT_PERMISSION = "request.error.delete.not.permission";
    public static final String REQUEST_ERROR_DELETE_NOT_FREE = "request.error.delete.not.free";
    public static final String REQUEST_ERROR_ALREADY_ASSIGNED = "request.error.already.assigned";
    public static final String REQUEST_ERROR_MUST_FREE = "request.error.must.free";
    public static final String REQUEST_ERROR_UPDATE_CANCELED = "request.error.update.canceled";
    public static final String REQUEST_ERROR_UPDATE_CLOSED = "request.error.update.closed";
    public static final String REQUEST_ERROR_UPDATE_NOT_PERMISSION = "request.error.update.not.permission";
    public static final String REQUEST_ERROR_UPDATE_NON_FREE = "request.error.update.non.free";
    public static final String REQUEST_ERROR_NOT_EXIST_PERSON_OR_REQUEST = "request.error.not.exist.person.or.request";
    public static final String REQUEST_NOT_ASSIGNED = "request.error.not.assigned";
    public static final String REQUEST_ERROR_UPDATE_STATUS = "request.error.update.status";

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

    //AMINISTRATOR ERROR
    public static final String ADMINISTRATOR_REMOVING_ERROR = "admin.error.removing.user";

    // MANAGER ERROR
    public static final String MANAGER_ERROR_MAIL = "manager.error.email";
    public static final String MANAGER_HAS_REQUESTS_ERROR = "manager.error.has.available.requests";


    // EMPLOYEE ERROR
    public static final String EMPLOYEE_ERROR_MAIL = "employee.error.email";
    public static final String EMPLOYEE_ERROR_ID = "employee.error.id";

    // REQUEST GROUP ERROR
    public static final String REQUEST_GROUP_ILLEGAL_ACCESS = "request.group.error.illegal.access";
    public static final String REQUEST_GROUP_NOT_EXIST = "request.group.error.not.exist";
    public static final String REQUEST_GROUP_ALREADY_EXIST = "request.group.error.already.exist";
    public static final String CANNOT_CREATE_REQUEST_GROUP = "request.group.error.cannot.create";

    //TOKEN
    public static final String TOKEN_ERROR_EXPIRED = "token.error.expired";

    //PRIORITY
    public static final String PRIORITY_ERROR_ID = "priority.error.id";

    //REPORT
    public static final String NOT_DATA_FOR_THIS_ROLE = "report.not.data.for.this.role";

    //UPDATE REQUEST HIGHLIGHTING
    public static final String REQUEST_UPDATE_MESSAGE_SUBJECT = "request.update.message.subject";
    public static final String REQUEST_UPDATE_MESSAGE_BODY = "request.update.message.body";
    public static final String DELETE_NOTIFICATION_EXCEPTION = "cannot.delete.notification.exception";
    public static final String CHANGE_STATUS_SUBJECT = "change.status.subject";
    public static final String CHANGE_MANGER_SUBJECT = "change.manager.subject";
    public static final String CHANGE_REQUEST = "change.request";
    public static final String CHANGE_GROUP = "change.group";
    public static final String CHANGE_STATUS_TO_FREE= "change.status.to.free";
    public static final String CHANGE_STATUS_TO_CLOSED= "change.status.to.closed";
    public static final String CHANGE_MANGER_UNASSIGNED = "change.manager.unassigned";
    public static final String CHANGE_GROUP_DELETED = "change.group.deleted";

    //MAIL
    public static final String MAIL_SENDING_ERROR = "mail.sending.error";
    public static final String SENDING_TO_DELETED_USER = "user.account.deleted";

    //SORT
    public static final String IS_COLUMN_EXIST = "is.column.exist";
}