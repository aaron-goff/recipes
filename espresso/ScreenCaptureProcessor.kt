import android.os.Environment.DIRECTORY_PICTURES
import android.os.Environment.getExternalStoragePublicDirectory
import androidx.test.runner.screenshot.BasicScreenCaptureProcessor
import androidx.test.runner.screenshot.Screenshot
import timber.log.Timber
import java.io.File
import java.io.IOException

class ScreenCaptureProcessor(subdirectory: String): BasicScreenCaptureProcessor() {
    init {
        // Processor will not work with >= API 29, as getExternalStoragePublicDirectory was deprecated
        // TODO: Figure out how to get this working on >= 29. Probably involves using Context?
        val pictureDirectory = getExternalStoragePublicDirectory(DIRECTORY_PICTURES)
        this.mDefaultScreenshotPath = File("$pictureDirectory/Automation", subdirectory)
    }

    fun takeScreenshot(screenshotName: String) {
        Timber.d("Taking screenshot of $screenshotName")
        // TODO: Figure out a way to not have to have a Thread.sleep
        Thread.sleep(1000)
        val capture = Screenshot.capture()
        val processors = setOf(this)
        try {
            capture.apply {
                name = screenshotName
                process(processors)
            }
            Timber.d("Screenshot taken of $screenshotName")
        } catch (exception: IOException) {
            Timber.e("Could not take screenshot.", exception)
        }
    }
}