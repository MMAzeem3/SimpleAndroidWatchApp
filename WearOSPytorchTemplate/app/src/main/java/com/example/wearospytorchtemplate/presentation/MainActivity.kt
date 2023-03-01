/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.wearospytorchtemplate.presentation

import android.content.Context
import java.util.*
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key.Companion.D
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.wearospytorchtemplate.R
import com.example.wearospytorchtemplate.presentation.theme.WearOSPytorchTemplateTheme
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import org.pytorch.Tensor
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp("Android")
        }

        // Test Pytorch
//        var modelPath = File(assetFilePath(application, "model_1.pt")).absolutePath
//        Log.d("Main", "$modelPath")
//        var model = Module.load(modelPath)
//        Log.d("Main", "$model")
        // Load file
        val assetName = "model.pt"
        var moduleFileAbsoluteFilePath: String = ""

        val moduleFile = File(applicationContext.filesDir, assetName)
        try {
            applicationContext.assets.open(assetName).use { `is` ->
                FileOutputStream(moduleFile).use { os ->
                    val buffer = ByteArray(4 * 1024)
                    while (true) {
                        val length = `is`.read(buffer)
                        if (length <= 0)
                            break
                        os.write(buffer, 0, length)
                    }
                    os.flush()
                    os.close()
                }
                moduleFileAbsoluteFilePath = moduleFile.absolutePath
            }
        } catch (e: IOException) {
            Log.e("Main", "Error process asset $assetName to file path")
        }

        var module = LiteModuleLoader.load(moduleFileAbsoluteFilePath)


        // Run a 300 array tensor through the network
        var inputList = FloatArray(300) { i -> 2.0f }
        var inputTensor = Tensor.fromBlob(inputList, longArrayOf(300))

        val outputTensor = module?.forward(IValue.from(inputTensor))?.toTensor()
        Log.d("Main", "Output: ${outputTensor?.dataAsFloatArray?.get(0)}")
    }
}

@Composable
fun WearApp(greetingName: String) {
    WearOSPytorchTemplateTheme {
        /* If you have enough items in your list, use [ScalingLazyColumn] which is an optimized
         * version of LazyColumn for wear devices with some added features. For more information,
         * see d.android.com/wear/compose.
         */
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center
        ) {
            Greeting(greetingName = greetingName)
        }
    }
}

@Composable
fun Greeting(greetingName: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = stringResource(R.string.hello_world, greetingName)
    )
}