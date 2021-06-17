open class BaseRobot {
    // This class contains some useful but weird BaseRobot functionality

    // validate that the clipboard text matches the expected
    // https://stackoverflow.com/a/55557486/11625850
    fun validateClipboard(expected: Regex) {
        // Clipboard is only accessible from the main thread
        val mainThreadHandler = Handler(Looper.getMainLooper())
        mainThreadHandler.post {
            val clipboardManager =
                InstrumentationRegistry.getInstrumentation().targetContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipboard = clipboardManager.primaryClip!!.getItemAt(0).text
            // We can't return the clipboard value outside of this `.post`, so we have to assert in here
            Assert.assertTrue(
                "Expected: \n$expected\n but received: \n$clipboard", expected.matches(clipboard)
            )
        }
    }

    // Overload method for the above that allows us to use string comparison without adding complexity
    fun validateClipboard(expected: String) {
        validateClipboard(expected = Regex(expected))
    }
}