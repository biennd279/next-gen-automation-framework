package me.d3s34.sqlmap.restapi.model

enum class ContentType(val id: Int) {
    TARGET(0),
    TECHNIQUES(1),
    DBMS_FINGERPRINT(2),
    BANNER(3),
    CURRENT_USER(4),
    CURRENT_DB(5),
    HOSTNAME(6),
    IS_DBA(7),
    USERS(8),
    PASSWORDS(9),
    PRIVILEGES(10),
    ROLES(11),
    DBS(12),
    TABLES(13),
    COLUMNS(14),
    SCHEMA(15),
    COUNT(16),
    DUMP_TABLE(17),
    SEARCH(18),
    SQL_QUERY(19),
    COMMON_TABLES(20),
    COMMON_COLUMNS(21),
    FILE_READ(22),
    FILE_WRITE(23),
    OS_CMD(24),
    REG_READ(25),
    STATEMENTS(26),
}