import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import com.google.android.material.textfield.TextInputLayout
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

class CustomMatchers {
    companion object {
        fun withItemCount(count: Int): BoundedMatcher<View, RecyclerView> {
            return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
                override fun describeTo(description: Description?) {
                    description?.appendText("RecyclerView with item count: $count")
                }

                override fun matchesSafely(item: RecyclerView?): Boolean {
                    return item?.adapter?.itemCount == count
                }
            }
        }

        // Function to match based on index
        fun withIndex(matcher: Matcher<View>, index: Int): TypeSafeMatcher<View> {
            return object : TypeSafeMatcher<View>() {
                var currentIndex = 0
                override fun describeTo(description: Description) {
                    description.appendText("with Index: ")
                    description.appendValue(index)
                    matcher.describeTo(description)
                }

                override fun matchesSafely(item: View?): Boolean {
                    return matcher.matches(item) && currentIndex++ == index
                }
            }
        }

        fun hasTextInputLayoutWithHint(expectedHint: String): Matcher<View> =
            object : TypeSafeMatcher<View>() {

                override fun describeTo(description: Description?) {
                    description?.appendText("Expected Hint: $expectedHint")
                }

                override fun matchesSafely(item: View?): Boolean {
                    if (item !is TextInputLayout) return false
                    val hint = item.hint.toString()
                    return expectedHint == hint
                }
            }

        fun hasTextInputLayoutWithText(expectedText: String): Matcher<View> =
            object : TypeSafeMatcher<View>() {

                override fun describeTo(description: Description?) {
                    description?.appendText("Expected Text: $expectedText")
                }

                override fun matchesSafely(item: View?): Boolean {
                    if (item !is TextInputLayout) return false
                    val text = item.editText?.text.toString()
                    return text == expectedText
                }
            }
    }
}