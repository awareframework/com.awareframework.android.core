Copyright (c) 2014 AWARE Mobile Context Instrumentation Middleware/Framework (http://www.awareframework.com)

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

# AWARE Android

[![jitpack-badge](https://jitpack.io/v/awareframework/com.aware.android.core.svg)](https://jitpack.io/#awareframework/com.aware.android.core)

This repository contains the the core classes to use while implementing an aware module.

## Example usage

In your root `build.gradle` add the jitpack repository.

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
        maven { url "https://s3.amazonaws.com/repo.commonsware.com" }
    }
}
```

In your app `build.gradle` add the dependency to the accelerometer.

```gradle
dependencies {
    compile 'com.github.awareframework:com.awareframework.android.core:master-SNAPSHOT'
}
```

## Extending to a new AWARE module

Aware-core provides you with many basic classes that you can extends and start your very own module.

### Extending a sensor controller

First of all, you may want to make a controller class to manage the service that will be run to
collect the data you want. This controller class will give the programmer an instance to interact
with. This way we aim to achieve the abstraction between the service itself and the interface.

We have defined the functions that are necessary to have for a controller class in `ISensorController` interface.

```kotlin
class Accelerometer (
        private val context: Context,
        config: AccelerometerConfig = AccelerometerConfig()
) : ISensorController {

    override fun start() {
        // start your AwareSensor service.
    }

    override fun stop() {
        // stop your AwareSensor service.
    }

    override fun sync(force: Boolean) { // force = false by default
        // send sync call to your AwareSensor service or to DbSyncManager directly.
    }

    override fun isEnabled() : Boolean {
        // return the related config field
    }

    override fun enable() {
        // alter the related config field
    }

    override fun disable() {
        // alter the related config field
    }
}
```

### Extending a sensor configuration

Further more, you are provided with a base configuration class called `SensorConfig` which you can then extend to add specific configurations you need for the new module.

```kotlin
data class AccelerometerConfig(
            /**
             * Accelerometer interval in hertz per second: e.g.,
             * 0 - fastest
             * 1 - sample per second
             * 5 - sample per second
             * 20 - sample per second
             */
            var interval: Int = 5,

            /**
             * Period to save data in minutes. (optional)
             */
            var period: Float = 1f,

            /**
             * Accelerometer threshold (float).  Do not record consecutive points if
             * change in value of all axes is less than this.
             */
            var threshold: Float = 0f,

            /**
             * For real-time observation of the sensor data collection.
             */
            var sensorObserver: SensorObserver? = null,

            /**
             * Should we keep a wake lock.
             * NOTE: Any related permission handling should be taken care of beforehand.
             */
            var wakeLockEnabled: Boolean = true
    ) : SensorConfig(dbPath = "aware_accelerometer", enabled = true)
```

We suggest you to follow a builder pattern to prepare the controller and it's configuration.

### Extending a sensor sevice

After this point you are ready to extend a `AwareSensor` if you module needs a service running to collect data. `AwareSensor` class provides you with a database engine `dbEngine`, an `onSync`
function called by the `DbSyncManager` for letting you know that it's time to send the data to the server, and a `SensorBroadcastReceiver` which you can extend and register as a broadcast receiver to
receive broadcasts such as `SENSOR_START_ENABLED`, and `SENSOR_STOP_ALL`. Here you can also add your
own actions to listen to, which may for example broadcasted from your sensor controller class.

```kotlin
class AccelerometerSensor : AwareSensor() {

    override fun onCreate() {
        super.onCreate()

        // You need to initialize the dbEngine instance here
        dbEngine = Engine.Builder(applicationContext)
                .setPath(CONFIG.dbPath)
                .setType(CONFIG.dbType)
                .setEncryptionKey(CONFIG.dbEncryptionKey)
                .setHost(CONFIG.dbHost)
                .build()

        // Your module logic here
    }

    // Override onSync to let the engine know what and how to sync the data to the server.
    override fun onSync(intent: Intent?) {
        // Your sync logic here, example in the later section.
    }

    override fun onDestroy() {
        super.onDestroy()
        dbEngine?.close()
    }

    // An example broadcast receiver for sensor events
    class AccelerometerBroadcastReceiver : AwareSensor.SensorBroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context == null)
                return

            when (intent?.action) {
                AwareSensor.SensorBroadcastReceiver.SENSOR_START_ENABLED -> {
                    if (CONFIG.enabled) {
                        startService(context)
                    }
                }

                Accelerometer.ACTION_AWARE_ACCELEROMETER_START -> {
                    startService(context)
                }

                AwareSensor.SensorBroadcastReceiver.SENSOR_STOP_ALL,
                Accelerometer.ACTION_AWARE_ACCELEROMETER_STOP -> {
                    stopService(context)
                }

                Accelerometer.ACTION_AWARE_ACCELEROMETER_LABEL -> {
                    AccelerometerSensor.CONFIG.label = intent.getStringExtra(Accelerometer.ACTION_AWARE_ACCELEROMETER_LABEL)
                }
            }
        }
    }
}
```

### Storing data using the provided database engine

Aware core provides you with a easy to use database engine for you basic data storage
and syncing needs. In order to store data in the database we first need to create our
own data model by extending `AwareObject`.

```kotlin
open class AccelerometerEvent(
        var x: Float = 0f,
        var y: Float = 0f,
        var z: Float = 0f,
        var eventTimestamp: Long = 0L,
        var accuracy: Int = 0,
        jsonVersion : Int = 1
) : AwareObject(jsonVersion = jsonVersion) {

    companion object {
        const val TABLE_NAME = "accelerometerEvent"
    }

}
```

Then in your implementation you can simply call `dbEngine.save(data, tableName)`
to store your data.

```kotlin
fun saveBuffer(dataBuffer: ArrayList<AccelerometerEvent>) {
    val data: Array<AccelerometerEvent> = dataBuffer.toTypedArray()
    dbEngine?.save(dataBuffer, AccelerometerEvent.TABLE_NAME)
}
``` 

When the `DbSyncManager` calls `onSync` method on the sensor implementation, you
should tell the engine which data and how they should be synced to the server.

```kotlin
override fun onSync(intent: Intent?) {
    dbEngine?.startSync(AccelerometerEvent.TABLE_NAME)
    dbEngine?.startSync(AccelerometerDevice.TABLE_NAME, DbSyncConfig(removeAfterSync = false))
}
```
