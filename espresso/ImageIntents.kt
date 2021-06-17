import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.content.FileProvider
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.platform.app.InstrumentationRegistry
import com.mastercard.spendmanagement.BuildConfig
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

// This class can be used to override pickers that would normally launch the image selector
// This specific example draws the Mastercard logo
// But can be used to draw anything -- the example has been left in to be modified as needed.
class ImageIntents() {
    companion object {
        fun generateImageOnSelection() {
            saveImage()
            val imgGalleryResult = createImageGallerySetResultsStub()
            Intents.intending(IntentMatchers.hasAction(Intent.ACTION_CHOOSER))
                .respondWith(imgGalleryResult)
        }

        private fun createImageGallerySetResultsStub(): Instrumentation.ActivityResult {
            val resultData = Intent()
            val dir = InstrumentationRegistry.getInstrumentation().targetContext.externalCacheDir
            val file = File(dir, "pickImageResult.jpeg")
            val uri = FileProvider.getUriForFile(
                InstrumentationRegistry.getInstrumentation().targetContext,
                "${BuildConfig.APPLICATION_ID}.provider",
                file
            )
            resultData.data = uri
            return Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
        }

        private fun drawMCLogo(): Bitmap {
            val w = 200
            val conf = Bitmap.Config.ARGB_8888
            val bmp = Bitmap.createBitmap(w, w, conf)
            val canvas = Canvas(bmp)
            val orangeCircle = Paint().apply {
                isAntiAlias = true
                color = Color.rgb(255, 95, 0)
                style = Paint.Style.FILL
            }
            val redCircle = Paint().apply {
                isAntiAlias = true
                color = Color.rgb(235, 0, 27)
                style = Paint.Style.FILL
            }


            canvas.drawColor(Color.WHITE)
            canvas.drawCircle(140.toFloat(), 100.toFloat(), 50.toFloat(), orangeCircle)
            canvas.drawCircle(60.toFloat(), 100.toFloat(), 50.toFloat(), redCircle)

            return bmp
        }

        private fun saveImage() {
            val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
            val bmp = drawMCLogo()

            val dir = targetContext.externalCacheDir
            val file = File(dir, "pickImageResult.jpeg")
            var outStream: FileOutputStream? = null
            try {
                outStream = FileOutputStream(file)
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
                with(outStream) {
                    flush()
                    close()
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}