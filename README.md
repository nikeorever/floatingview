Floating View
=============

Add and manage floating views on the `Activity`.

Usage
-----
```kotlin
class MyActivity : AppCompatActivity() {

    private val floatingViewManager = FloatingViewManager(activity = this)

    private val floatingView: FloatingView by lazy {
        FloatingView.image(this) {
            setImageResource(R.drawable.ic_google)
        }
    }

    fun addFloatingView() {
        if (!floatingViewManager.isFloatingViewAdded(floatingView)) {
            floatingViewManager.addFloatingView(
                floatingView = floatingView,
                layoutParams = FrameLayout.LayoutParams(200, 200),
                onFloatingViewClick = {
                    // TODO do something
                }
            )
        }
    }

    fun removeFloatingView() {
        if (floatingViewManager.isFloatingViewAdded(floatingView)) {
            floatingViewManager.removeFloatingView(floatingView)
        }
    }
}
```

Download
--------

Artifacts are available in Maven Central at `cn.nikeo.floatingview:floatingview:0.1.0`.
 
In-development snapshots are available from
[Sonatype's snapshot repository](https://oss.sonatype.org/content/repositories/snapshots/).