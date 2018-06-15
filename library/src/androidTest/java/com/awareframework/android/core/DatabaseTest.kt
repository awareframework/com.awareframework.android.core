package com.awareframework.android.core

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.awareframework.android.core.db.Engine
import com.awareframework.android.core.model.AwareData
import com.awareframework.android.core.model.AwareObject
import junit.framework.TestCase.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

/**
 * Class decription
 *
 * @author  sercant
 * @date 23/03/2018
 */
@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private var engine: Engine? = null
    private var encryptedEngine: Engine? = null

    val TABLE_NAME_MULTIPLE_ENTRY = "testTableMultiple"
    val TABLE_NAME_SINGLE_ENTRY = "testTableSingle"

    @Before
    @Throws(Exception::class)
    fun init() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()

        engine = Engine.Builder(appContext)
                .setType(Engine.DatabaseType.NONE)
                .build()

        assertNull(engine)

        engine = Engine.Builder(appContext)
                .setPath("test.db")
                .setType(Engine.DatabaseType.ROOM)
                .build()

        assertNotNull(engine)

        encryptedEngine = Engine.Builder(appContext)
                .setPath("test_encrypted.db")
                .setType(Engine.DatabaseType.ROOM)
                .setEncryptionKey(ENCRYPTION_KEY)
                .build()

        assertNotNull(engine)
    }

    @Test
    @Throws(Exception::class)
    fun testDeleteData() {
        val engine = engine!!

        // Create some default events
        val events = ArrayList<AwareObject>()
        for (i in 0..99) {
            events.add(AwareObject())
        }

        val data_buffer: Array<AwareObject> = events.toTypedArray()
        engine.removeAll().join()
        engine.save(data_buffer, TABLE_NAME_MULTIPLE_ENTRY).join()

        var data: List<AwareData>? = null
        engine.get(TABLE_NAME_MULTIPLE_ENTRY, 10, { data = it }).join()

        assertEquals(10, data!!.size.toLong())

        engine.remove(data!!).join()

        var afterRemoveData: List<AwareData>? = null
        engine.get(TABLE_NAME_MULTIPLE_ENTRY, 100, { afterRemoveData = it }).join()

        assertEquals(90, afterRemoveData!!.size.toLong())
    }

    @Test
    @Throws(Exception::class)
    fun testInsertAllEvents() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        // Create some default events
        val events = ArrayList<AwareObject>()
        for (i in 0..99) {
            events.add(AwareObject())
        }

        val data_buffer: Array<AwareObject> = events.toTypedArray()

        engine!!.removeAll().join()
        engine!!.save(data_buffer, TABLE_NAME_MULTIPLE_ENTRY).join()
        var data: List<AwareData>? = null
        engine!!.getAll(TABLE_NAME_MULTIPLE_ENTRY, { data = it }).join()
        assertEquals(events.size.toLong(), data!!.size.toLong())

        encryptedEngine!!.removeAll().join()
        encryptedEngine!!.save(data_buffer, TABLE_NAME_MULTIPLE_ENTRY).join()
        var data2: List<AwareData>? = null
        encryptedEngine!!.getAll(TABLE_NAME_MULTIPLE_ENTRY, { data2 = it }).join()
        assertEquals(events.size.toLong(), data2!!.size.toLong())
    }

    @Test
    @Throws(Exception::class)
    fun testUpdateDevice() {
        val device = AwareObject()

        engine!!.removeAll().join()
        engine!!.save(device, TABLE_NAME_SINGLE_ENTRY, 0L).join()

        var savedDevice: List<AwareData>? = null
        engine!!.getAll(TABLE_NAME_SINGLE_ENTRY, { savedDevice = it }).join()

        assertEquals(1, savedDevice!!.size.toLong())

        val device2 = savedDevice!![0]

        device2.alterData<AwareObject> {
            val uuid = UUID.randomUUID().toString()
            it.deviceId = uuid
        }

        engine!!.update(device2)

        engine!!.getAll(TABLE_NAME_SINGLE_ENTRY, { savedDevice = it }).join()
        assertEquals(1, savedDevice!!.size.toLong())
        assertEquals(device.deviceId, savedDevice!![0].deviceId)
    }

    @After
    fun tearDown() {
        engine!!.close()
        encryptedEngine!!.close()
    }

    companion object {

        private val ENCRYPTION_KEY = "custom_key"
    }
}
