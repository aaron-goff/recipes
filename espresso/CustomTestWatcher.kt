import org.junit.rules.TestWatcher
import org.junit.runner.Description

class CustomTestWatcher : TestWatcher() {
    override fun starting(description: Description) {

    }

    override fun failed(e: Throwable?, description: Description) {

    }

    override fun succeeded(description: Description?) {

    }
}