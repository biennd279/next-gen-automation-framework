package me.d3s34.sqlmap

import me.d3s34.sqlmap.restapi.ApiService

class SqlmapApiEngine(
    private val baseUrl: String
): SqlmapEngine() {
    val apiService by lazy { ApiService(baseUrl) }
}
