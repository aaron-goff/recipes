import android.app.Instrumentation
import android.view.View
import androidx.test.espresso.FailureHandler
import androidx.test.espresso.base.DefaultFailureHandler
import junit.framework.AssertionFailedError
import org.hamcrest.Matcher
import java.util.*

class FailureHandler(instrumentation: Instrumentation) {
    var delegate: DefaultFailureHandler = DefaultFailureHandler(instrumentation.targetContext)

    override fun handle(error: Throwable?, viewMatcher: Matcher<View>?) {
        // logic for changing the error, such as...
        val newError = AssertionFailedError("NEW ERROR" + error!!.message)
        delegate.handle(newError, viewMatcher)
    }
}