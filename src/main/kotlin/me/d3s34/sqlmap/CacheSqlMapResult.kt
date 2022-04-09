package me.d3s34.sqlmap

interface CacheSqlMapResult {
    fun getTaskId(taskId: String): Any
    fun saveTaskId(taskId: String, info: Any): Unit
    fun updateTaskId(taskId: String, newInfo: Any): Unit
    fun deleteTaskId(taskId: String): Unit
}
