package me.d3s34.sqlmap.restapi

import me.d3s34.sqlmap.restapi.data.*
import kotlin.reflect.KClass

enum class TaskDataContentType(val id: Int) {
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
    STATEMENTS(26);

    companion object {
        private val map = values().associateBy(TaskDataContentType::id)
        fun findById(id: Int): TaskDataContentType? = map[id]
    }
}

fun TaskDataContentType.getClassType(): KClass<out AbstractData> {
    return when (this.id) {
        TaskDataContentType.TARGET.id -> TargetData::class
        TaskDataContentType.DUMP_TABLE.id -> DumpTableData::class
        TaskDataContentType.TECHNIQUES.id -> TechniqueData::class
        TaskDataContentType.CURRENT_USER.id,
        TaskDataContentType.CURRENT_DB.id,
        TaskDataContentType.HOSTNAME.id -> StringData::class
        TaskDataContentType.DBS.id -> ListStringData::class
        TaskDataContentType.PRIVILEGES.id,
        TaskDataContentType.ROLES.id,
        TaskDataContentType.TABLES.id -> MapStringListStringData::class
        TaskDataContentType.COLUMNS.id,
        TaskDataContentType.SCHEMA.id -> MapDbToTableToString::class
        TaskDataContentType.COUNT.id -> MapDbToCountToListTable::class
        TaskDataContentType.SQL_QUERY.id -> ListToListStringData::class
        else -> AbstractData::class
    }
}
