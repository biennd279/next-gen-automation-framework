package me.d3s34.sqlmap.restapi.content

import kotlinx.serialization.json.Json
import me.d3s34.sqlmap.restapi.data.DumpTableData
import me.d3s34.sqlmap.restapi.data.TargetData
import me.d3s34.sqlmap.restapi.model.ContentType
import me.d3s34.sqlmap.restapi.serializer.ContentSerializer
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ContentTest {

    @Test
    fun testTarget() {
        val rawJson =  """
        { "status": 1, "type": 0, "value": { "url": "http://localhost:80/", "query": null, "data": 
        "user=test&password=test&s=OK" } }
        """.trimIndent()

        val content = Json.decodeFromString(ContentSerializer, rawJson)

        assertEquals(ContentType.TARGET.id, content.type)
        assertEquals(1, content.status)

        with(content.value as TargetData) {
            assertEquals(null, query)
            assertEquals("http://localhost:80/", url)
            assertEquals("user=test&password=test&s=OK", data)
        }
    }

    @Test
    fun testTable() {
        val rawJson = """
        { "status": 1, "type": 17, "value": { "salary": { "length": 6, "values":
        [ "25000", "99000", "45000", "39000", "1250", "3500", "2500" ] }, "username":
        { "length": 16, "values": [ "james_kirk", "mr_spock", "leonard_mccoy", "nyota_uhura", "montgomery_scott",
        "hiraku_sulu", "pavel_chekov" ] }, "first_name": { "length": 10, "values":
        [ "James", "Mr", "Leonard", "Nyota", "Montgomery", "Hikaru", "Pavel" ] }, "last_name":
        { "length": 9, "values": [ "Kirk", "Spock", "McCoy", "Uhura", "Scott", "Sulu", "Chekov" ] }, "__infos__":
         { "count": 7, "table": "users", "db": "vuln" }, "password": { "length": 16, "values": [ "kobayashi_maru",
          "0nlyL0g!c", "hesDEADjim!", "StarShine", "ScottyDoesntKnow", "parking-break-on", "99victorvictor2" ] } } }
        """.trimIndent()

        val content = Json.decodeFromString(ContentSerializer, rawJson)

        assertEquals(ContentType.DUMP_TABLE.id, content.type)
        assertEquals(1, content.status)

        with(content.value as DumpTableData) {
            assertEquals("users", this.table)
            assertEquals("vuln", this.db)
            assertEquals(5, this.data.size)
        }
    }
}