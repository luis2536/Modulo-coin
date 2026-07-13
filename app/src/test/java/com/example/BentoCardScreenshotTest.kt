package com.example

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonGreen
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class BentoCardScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun bentocard_screenshot() {
    composeTestRule.setContent { 
        MyApplicationTheme { 
            BentoCard(
                titulo = "PRUEBA UNITARIA BENTO",
                icono = Icons.Default.Shield,
                colorAcunado = NeonGreen
            ) {
                Text("Contenido de prueba táctica", modifier = Modifier.padding(16.dp))
            }
        } 
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/bentocard.png")
  }
}
