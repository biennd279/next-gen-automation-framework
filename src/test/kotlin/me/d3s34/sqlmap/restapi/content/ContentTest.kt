package me.d3s34.sqlmap.restapi.content

import kotlinx.serialization.json.Json
import me.d3s34.sqlmap.restapi.data.DumpTableData
import me.d3s34.sqlmap.restapi.data.TargetData
import me.d3s34.sqlmap.restapi.data.TechniqueData
import me.d3s34.sqlmap.restapi.model.ContentType
import me.d3s34.sqlmap.restapi.serializer.DataSerializer
import me.d3s34.sqlmap.restapi.serializer.InjectionSerializer
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ContentTest {

    @Test
    fun testTarget() {
        val rawJson =  """
        { "status": 1, "type": 0, "value": { "url": "http://localhost:80/", "query": null, "data": 
        "user=test&password=test&s=OK" } }
        """.trimIndent()

        val content = Json.decodeFromString(DataSerializer, rawJson)

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

        val content = Json.decodeFromString(DataSerializer, rawJson)

        assertEquals(ContentType.DUMP_TABLE.id, content.type)
        assertEquals(1, content.status)

        with(content.value as DumpTableData) {
            assertEquals("users", this.table)
            assertEquals("vuln", this.db)
            assertEquals(5, this.data.size)
        }
    }

    private val format = Json { ignoreUnknownKeys = true }

    @Test
    fun testTechnique() {
        val rawInjectionJson = """
            { "dbms": "MySQL", "suffix": " AND '[RANDSTR]'='[RANDSTR]",
             "clause": [ 1, 2, 3, 8, 9 ], "notes": [], "ptype": 2, "dbms_version": [ ">= 5.0.12" ], "prefix": "'", 
             "place": "POST", "os": null, "conf": { "code": null, "string": null, "notString": null, "titles": null, 
             "regexp": null, "textOnly": null, "optimize": null }, "parameter": "user", "data": { "5": 
             { "comment": "", "matchRatio": 0.023, "trueCode": 200, 
             "title": "MySQL >= 5.0.12 AND time-based blind (query SLEEP)", 
             "templatePayload": null, 
             "vector": "AND (SELECT [RANDNUM] FROM (SELECT(SLEEP([SLEEPTIME]-(IF([INFERENCE],0,[SLEEPTIME])))))[RANDSTR])",
              "falseCode": null, "where": 1,
               "payload": "user=test' AND (SELECT 5338 FROM (SELECT(SLEEP([SLEEPTIME])))oEOQ) AND 'bgWC'='bgWC&password=test&s=OK" }, 
               "6": { "comment": "[GENERIC_SQL_COMMENT]", "matchRatio": 0.023, "trueCode": null, "title": "Generic UNION query (NULL) - 1 to 20 columns", "templatePayload": null, 
               "vector": [ 0, 2, "[GENERIC_SQL_COMMENT]", "'", " AND '[RANDSTR]'='[RANDSTR]", "NULL", 1, false, null, null, null ], "falseCode": null,
                "where": 1, "payload": "user=test' UNION ALL SELECT CONCAT(0x71716a7171,0x4b6e694947614f774a50555a444b4c4567747261527a664c46504b6d516149677559427171504f61,0x716a6b6a71),NULL-- -&password=test&s=OK" } } }
        """.trimIndent()



        val injection = format.decodeFromString(InjectionSerializer, rawInjectionJson)

        assertEquals("MySQL", injection.dbms)
        assertEquals("user", injection.parameter)
        assertEquals("POST", injection.place)
        assertEquals(2, injection.size)

        val rawJson = """
            { "status": 1, "type": 1, "value": [ $rawInjectionJson ] }
        """.trimIndent()
        val data = format.decodeFromString(DataSerializer, rawJson)

        assertEquals(listOf(injection), data.value as TechniqueData)
    }
}