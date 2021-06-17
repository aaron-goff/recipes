import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import com.wdullaer.materialdatetimepicker.date.DatePickerController
import com.wdullaer.materialdatetimepicker.date.DayPickerView
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher
import org.hamcrest.core.AllOf.allOf
import java.lang.reflect.Field

class CustomViewActions {
    companion object {
        // Function to set the date on the calendar date picker
        fun setDate(year: Int, monthOfYear: Int, dayOfMonth: Int): ViewAction {
            // Translated from Java to Kotlin from https://stackoverflow.com/a/34673382/11625850
            return object: ViewAction {
                override fun getConstraints(): Matcher<View> {
                    return allOf(isAssignableFrom(DayPickerView::class.java), isDisplayed())
                }

                override fun getDescription(): String {
                    return "set date"
                }

                override fun perform(uiController: UiController?, view: View?) {
                    try {
                        var f: Field? = null;
                        f = DayPickerView::class.java.getDeclaredField("mController")
                        f.isAccessible = true
                        val controller: DatePickerController = f.get(view) as DatePickerController
                        controller.onDayOfMonthSelected(year, monthOfYear, dayOfMonth)
                    } catch (e: NoSuchFieldException) {
                        e.printStackTrace()
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                    }
                }

            }
        }

        private fun scrollToRecyclerViewExtreme(isBottom: Boolean = true): ViewAction? {
            return object: ViewAction {
                override fun getDescription(): String {
                    return "scroll RecyclerView to ${if (isBottom) "bottom" else "top" }"
                }

                override fun getConstraints(): Matcher<View> {
                    return CoreMatchers.allOf(
                        isAssignableFrom(RecyclerView::class.java),
                        isDisplayed()
                    )
                }

                override fun perform(uiController: UiController?, view: View?) {
                    val recyclerView = view as RecyclerView
                    val itemCount = recyclerView.adapter?.itemCount
                    val position = if (isBottom) itemCount?.minus(1) ?: 0 else 0
                    recyclerView.scrollToPosition(position)
                    uiController?.loopMainThreadUntilIdle()
                }
            }
        }

        fun scrollToTopOfRecyclerView(): ViewAction? {
            return scrollToRecyclerViewExtreme(isBottom = false)
        }

        fun scrollToBottomOfRecyclerView(): ViewAction? {
            return scrollToRecyclerViewExtreme(isBottom = true)
        }

        // Function that overrides espresso's default display constraints when testing with the UI Controller
        fun withCustomConstraints(
            action: ViewAction,
            constraints: Matcher<View>
        ): ViewAction? {
            return object : ViewAction {
                override fun getConstraints(): Matcher<View> {
                    return constraints
                }

                override fun getDescription(): String {
                    return action.description
                }

                override fun perform(
                    uiController: UiController?,
                    view: View?
                ) {
                    action.perform(uiController, view)
                }
            }
        }
    }
}