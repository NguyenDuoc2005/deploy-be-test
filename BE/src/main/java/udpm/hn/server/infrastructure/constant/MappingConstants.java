package udpm.hn.server.infrastructure.constant;

public final class MappingConstants {

    /* API VERSION PREFIX */
    public static final String API_VERSION_PREFIX = "/api/v1";

    /* API COMMON */
    public static final String API_COMMON = API_VERSION_PREFIX + "/common";

    /* AUTHENTICATION */
    public static final String API_AUTH_PREFIX = API_VERSION_PREFIX + "/auth";

    // Constants representing the base paths for various resources
    public static final String ADMIN = "/admin";

    public static final String MANAGE = "/manage";

    public static final String MEMBER = "/member";

    public static final String PROJECT_DETAILS = "/project-details";

    // Constants representing the full paths for various resources
    public static final String API_ADMIN_PREFIX = API_VERSION_PREFIX + ADMIN;
    public static final String API_MANAGE_PREFIX = API_VERSION_PREFIX + MANAGE;
    public static final String API_MEMBER_PREFIX = API_VERSION_PREFIX + MEMBER;
    public static final String API_PROJECT_DETAILS_PREFIX = API_VERSION_PREFIX + PROJECT_DETAILS;
    // Constants representing the full paths for various resources under admin
    public static final String API_ADMIN_DEPARTMENT = API_ADMIN_PREFIX + "/department";
    public static final String API_ADMIN_MAJOR = API_ADMIN_PREFIX + "/major";
    public static final String API_ADMIN_FACILITY = API_ADMIN_PREFIX + "/facility";
    public static final String API_ADMIN_DEPARTMENT_FACILITY = API_ADMIN_PREFIX + "/department-facility";
    public static final String API_ADMIN_MAJOR_FACILITY = API_ADMIN_PREFIX + "/major-facility";
    public static final String API_ADMIN_STAFF = API_ADMIN_PREFIX + "/staff";
    public static final String API_ADMIN_CATEGORY = API_ADMIN_PREFIX + "/category";
    public static final String API_ADMIN_STUDENT = API_ADMIN_PREFIX + "/student";

    public static final String API_ADMIN_PROJECT = API_ADMIN_PREFIX + "/project";

    public static final String API_ADMIN_PROJECT_STUDENT = API_ADMIN_PROJECT + "/student";

    public static final String API_ADMIN_ROLE = API_ADMIN_PREFIX + "/role";

    // API MEMBER
    public static final String API_MEMBER_MYPROJECT = API_MEMBER_PREFIX + "/my-project";
    public static final String API_MEMBER_TODO = API_MEMBER_PREFIX + "/todo";
    public static final String API_MEMBER_TODO_LIST = API_MEMBER_PREFIX + "/todo-list";
    public static final String API_MEMBER_TODO_VOTE = API_MEMBER_PREFIX + "/todo-vote";
    public static final String API_MEMBER_MEMBER_PROJECT = API_MEMBER_PREFIX + "/member-project";
    public static final String API_MEMBER_LABEL_PROJECT = API_MEMBER_PREFIX + "/label";
    public static final String API_MEMBER_LABEL_TODO = API_MEMBER_PREFIX + "/label-todo";
    public static final String API_MEMBER_PHASE_PROJECT = API_MEMBER_PREFIX + "/phase-project";


    // API PROJECT DETAILS
    public static final String API_PROJECT_DETAILS_MYPROJECT = API_PROJECT_DETAILS_PREFIX + "/my-project";
    public static final String API_PROJECT_DETAILS_TODO = API_PROJECT_DETAILS_PREFIX + "/todo";
    public static final String API_PROJECT_DETAILS_TODO_VOTE = API_PROJECT_DETAILS_PREFIX + "/todo-vote";
    public static final String API_PROJECT_DETAILS_TODO_LIST = API_PROJECT_DETAILS_PREFIX + "/todo-list";
    public static final String API_PROJECT_DETAILS_MEMBER_PROJECT = API_PROJECT_DETAILS_PREFIX + "/member-project";
    public static final String API_PROJECT_DETAILS_LABEL_PROJECT = API_PROJECT_DETAILS_PREFIX + "/label";
    public static final String API_PROJECT_DETAILS_LABEL_TODO = API_PROJECT_DETAILS_PREFIX + "/label-todo";
    public static final String API_PROJECT_DETAILS_COMMENT_TODO = API_PROJECT_DETAILS_PREFIX + "/comment";
    public static final String API_PROJECT_DETAILS_RESOURCE_TODO = API_PROJECT_DETAILS_PREFIX + "/resource";
    public static final String API_PROJECT_DETAILS_NOTIFICATION_TODO = API_PROJECT_DETAILS_PREFIX + "/notification";
    public static final String API_PROJECT_DETAILS_NOTIFICATION_MEMBER_TODO = API_PROJECT_DETAILS_PREFIX + "/notification-member";
    public static final String API_PROJECT_DETAILS_IMAGE = API_PROJECT_DETAILS_PREFIX + "/image";
    public static final String API_PROJECT_DETAILS_ACTIVITY = API_PROJECT_DETAILS_PREFIX + "/activity";
    public static final String API_PROJECT_DETAILS_TYPE_TODO = API_PROJECT_DETAILS_PREFIX + "/type-todo";
    //manage
    public static final String API_MANAGE_PROJECT = API_MANAGE_PREFIX + "/project";

    public static final String API_MANAGE_STAGE = API_MANAGE_PREFIX + "/stage";

    public static final String API_MANAGE_PROJECT_STUDENT = API_MANAGE_PROJECT + "/student";

    public static final String API_MANAGE_TODO = API_MANAGE_PREFIX + "/todo";

    public static final String API_MANAGE_TODO_LIST = API_MANAGE_PREFIX + "/todo-list";


    public static final String API_MANAGE_TODO_VOTE = API_MANAGE_TODO + "/vote";

    // Manage phase project

    public static final String API_MANAGE_PHASE_PROJECT = API_MANAGE_PREFIX + "/phase-project";


    // Manage report
    public static final String API_MANAGE_REPORT = API_MANAGE_PREFIX + "/report";
}
