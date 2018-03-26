package com.awareframework.android.core.model

/**
 * Interface for the sensor controllers.
 *
 * @author  sercant
 * @date 21/03/2018
 */
interface ISensorController {
    // TODO: document

    fun start()
    fun sync(force: Boolean = false)
    fun isEnabled() : Boolean
    fun enable()
    fun disable()
    fun stop()
}