import android.view.MotionEvent
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.MotionEvents
import androidx.test.espresso.action.Press
import androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.hamcrest.Matcher

// Kotlin translation from here: https://stackoverflow.com/a/32052854/11625850
class LowLevelActions {
    companion object {
        object MotionEventDownHeldView {
            var heldDown: MotionEvent? = null
            var view: View? = null
        }

        fun pressAndHold(): PressAndHoldAction {
            return PressAndHoldAction()
        }

        fun release(): ReleaseAction {
            return ReleaseAction()
        }

        fun tearDown() {
            if (MotionEventDownHeldView.heldDown != null && MotionEventDownHeldView.view != null) {
                onView(withId(MotionEventDownHeldView.view!!.id)).perform(release())
            }
        }

    }

    class PressAndHoldAction : ViewAction {
        override fun getDescription(): String {
            return "Press and hold action"
        }

        override fun getConstraints(): Matcher<View> {
            return isDisplayingAtLeast(90)
        }

        override fun perform(uiController: UiController?, view: View?) {
            if (MotionEventDownHeldView.heldDown != null) {
                throw AssertionError("Only one view can be held at a time")
            }

            val precision: FloatArray? = Press.FINGER.describePrecision()
            val coordinates: FloatArray? = GeneralLocation.CENTER.calculateCoordinates(view)

            // Set objects
            MotionEventDownHeldView.heldDown =
                MotionEvents.sendDown(uiController, coordinates, precision).down
            MotionEventDownHeldView.view = view
        }
    }


    class ReleaseAction : ViewAction {
        override fun getDescription(): String {
            return "Release action"
        }

        override fun getConstraints(): Matcher<View> {
            return isDisplayingAtLeast(90)
        }

        override fun perform(uiController: UiController?, view: View?) {
            val newView = if (view == null && MotionEventDownHeldView.view != null) {
                MotionEventDownHeldView.view
            } else view

            if (MotionEventDownHeldView.heldDown == null || newView == null) {
                throw AssertionError("Before calling release(), you must call pressAndHold() on a view")
            }

            // Perform the release action
            val coordinates: FloatArray = GeneralLocation.CENTER.calculateCoordinates(newView)
            MotionEvents.sendUp(uiController, MotionEventDownHeldView.heldDown, coordinates)

            // Clean up objects
            MotionEventDownHeldView.heldDown = null
            MotionEventDownHeldView.view = null
        }

    }
}