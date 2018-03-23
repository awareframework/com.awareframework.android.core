package com.awareframework.android.core

/**
 * Class decription
 *
 * @author  sercant
 * @date 22/03/2018
 */
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.awareframework.android.core.manager.DbSyncManager
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DbSyncManagerTest {

    var invokeCount: Int = 0

    @Test
    fun testSyncInvocation() {
        val appContext = InstrumentationRegistry.getTargetContext()

        val sensor = object: AwareSensor() {
            override fun onBind(p0: Intent?): IBinder {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onSync(intent: Intent?) {
                invokeCount++
            }
        }

        val syncReceiver = AwareSensor.SensorSyncReceiver(sensor)

        val syncFilter = IntentFilter()
        syncFilter.addAction(AwareSensor.SensorSyncReceiver.SYNC)
        appContext.registerReceiver(syncReceiver, syncFilter)

        val syncManager = DbSyncManager.Builder(appContext).setSyncInterval(0.1f).setWifiOnly(false).setDebug(true).build()
        syncManager.start()

        Thread.sleep(10000)

        check(invokeCount > 0)
    }

}