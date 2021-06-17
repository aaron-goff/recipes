class Utils {
    companion object {

        fun screenshot(name: String = testMethodName, parentDirectory: String = EMPTY_STRING) {
            val subdirectory = if (parentDirectory == EMPTY_STRING) {
                "Ad Hoc/$testClassName"
            } else {
                "$parentDirectory/$testClassName"
            }

            val screenshotName = if (name != testMethodName) {
                sanitizeScreenshotName(name = name) + "_" + testMethodName
            } else {
                sanitizeScreenshotName(name = name)
            }

            ScreenCaptureProcessor(subdirectory = subdirectory).takeScreenshot(screenshotName = screenshotName)

        }

        private fun sanitizeScreenshotName(name: String): String {
            return name.replace("\$", EMPTY_STRING)
                .replace("\'", EMPTY_STRING)
                .replace("\"", EMPTY_STRING)
                .replace("&", EMPTY_STRING)
                .replace(" ", "_")
                .replace("?", EMPTY_STRING)
                .replace(".", EMPTY_STRING)
                .replace("\n", EMPTY_STRING)
                .replace("%", EMPTY_STRING)
                .replace(":", EMPTY_STRING)
                .toLowerCase()
        }

        // Function to return translated string provided by String Resource Id. Should only be used with `containsString()`
        fun textResolver(@IdRes textId: Int): String {
            return ApplicationProvider.getApplicationContext<Context>().resources.getString(textId)
        }

        fun waitForView(maxAttempts: Int = 3, sleepTime: Long = 3000, validationFunction: () -> Unit) {
            waitWithRetry(
                maxAttempts = maxAttempts,
                sleepTime = sleepTime,
                validationFunction = validationFunction,
                exceptionTypes = listOf(NoMatchingViewException::class.java)
            )
        }

        fun waitForPerform(maxAttempts: Int = 3, sleepTime: Long = 3000, validationFunction: () -> Unit) {
            waitWithRetry(
                maxAttempts = maxAttempts,
                sleepTime = sleepTime,
                validationFunction = validationFunction,
                exceptionTypes = listOf(PerformException::class.java)
            )
        }

        private fun waitWithRetry(
            maxAttempts: Int = 3,
            sleepTime: Long = 3000,
            validationFunction: () -> Unit,
            exceptionTypes: List<Class<*>>
        ) {
            for (attempts in 0..maxAttempts) {
                try {
                    validationFunction()
                    break
                } catch (exception: Exception) {
                    // Check if the exception is allowed
                    if (!exceptionTypes.contains(exception::class.java)) {
                        throw exception
                    }
                    // sleep if we aren't at max attempts
                    if (attempts < maxAttempts) {
                        Thread.sleep(sleepTime)
                    }
                    // throw the exception if we have reached max attempts
                    if (attempts == maxAttempts) {
                        throw exception
                    }
                }
            }
        }

        private fun hasCertainAnnotation(
            annotations: Collection<Annotation>?,
            annotationToMatch: String,
        ): Boolean {
            if (annotations != null) {
                for (annotation in annotations) {
                    if (annotation.toString().contains(annotationToMatch)) {
                        return true
                    }
                }
            }
            return false
        }

    }
}