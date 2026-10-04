package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ui.MindlyViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("MINDLY", appName)
  }

  @Test
  fun `verify admin pin functionality`() {
    val context = ApplicationProvider.getApplicationContext<android.app.Application>()
    val viewModel = MindlyViewModel(context)

    // Wrong pin
    val wrongResult = viewModel.verifyAndLoginAdminPin("0000")
    assertFalse("Incorrect PIN should return false", wrongResult)

    // Another wrong pin
    val wrongResult2 = viewModel.verifyAndLoginAdminPin("1234")
    assertFalse("Incorrect PIN should return false", wrongResult2)

    // Correct secret admin pin 1314
    val correctResult = viewModel.verifyAndLoginAdminPin("1314")
    assertTrue("Correct PIN 1314 should return true", correctResult)
  }
}

