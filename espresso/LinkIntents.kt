import android.app.Instrumentation
import android.content.Intent
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher

// Used to generate mock intents for any links
class LinkIntents() {
    companion object {
        fun generateLinkIntent(linkUri: String): Matcher<Intent> {
            val expectedIntent = allOf(hasAction(Intent.ACTION_VIEW), hasData(linkUri))
            intending(expectedIntent).respondWith(Instrumentation.ActivityResult(0, null))

            return expectedIntent
        }
    }
}