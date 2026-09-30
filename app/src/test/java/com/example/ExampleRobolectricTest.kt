package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.compat.DeviceCompatibilityManager
import com.example.model.FontSupportLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("FontX", appName)
    }

    @Test
    fun `device compatibility manager detects hardware info`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val deviceInfo = DeviceCompatibilityManager.getDeviceInfo(context)
        assertNotNull(deviceInfo)
        assertTrue(deviceInfo.brand.isNotBlank())
        assertTrue(deviceInfo.model.isNotBlank())
        assertTrue(deviceInfo.guideSteps.isNotEmpty())
    }
}
