package global.msnthrp.whoo.ui.extensions

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.core.view.*

fun View.applyInsetVerticalPadding() =
        doOnApplyWindowInsets { view, insets, padding, _ ->
            view.updatePadding(
                    top = padding.top + insets.systemWindowInsetTop,
                    bottom = padding.bottom + insets.systemWindowInsetBottom
            )
            insets
        }

fun View.applyGestureInsetTopPadding() =
        doOnApplyWindowInsets { view, insets, padding, _ ->
            view.updatePadding(top = padding.top + insets.systemGestureInsets.top)
            insets
        }

fun View.applyInsetTopMargin() =
        doOnApplyWindowInsets { view, insets, _, margins ->
            (view.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
                updateMargins(top = margins.top + insets.systemWindowInsetTop)
            }
            insets
        }

fun View.applyInsetTopPadding() =
        doOnApplyWindowInsets { view, insets, padding, _ ->
            view.updatePadding(top = padding.top + insets.systemWindowInsetTop)
            insets
        }

fun View.applyInsetBottomMargin() =
        doOnApplyWindowInsets { view, insets, _, margins ->
            (view.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
                updateMargins(bottom = margins.bottom + insets.systemWindowInsetBottom)
            }
            insets
        }

fun View.applyInsetBottomPadding() =
        doOnApplyWindowInsets { view, insets, padding, _ ->
            view.updatePadding(bottom = padding.bottom + insets.systemWindowInsetBottom)
            insets
        }

/**
 * core method of all inset handlers
 * @param block lambda that is being invoked every time the view receives the insets
 *                  View -- the view
 *                  WindowInsetsCompat -- received insets to handle
 *                  Rect -- initial padding of the view
 *                  Rect -- initial margins of the view, or null if none
 */
fun View.doOnApplyWindowInsets(block: (View, WindowInsetsCompat, Rect, Rect) -> WindowInsetsCompat) {
    val initialPadding = recordInitialPaddingForView()
    val initialMargin = recordInitialMarginsForView()
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        block(v, insets, initialPadding, initialMargin)
    }
    requestApplyInsetsWhenAttached()
}

/**
 * insets may overwrite initial padding of view
 * so we have to check them first
 */
private fun View.recordInitialPaddingForView() =
        Rect(paddingLeft, paddingTop, paddingRight, paddingBottom)


/**
 * insets may overwrite initial margins of view
 * so we have to check them first
 */
private fun View.recordInitialMarginsForView(): Rect =
        (layoutParams as? ViewGroup.MarginLayoutParams)?.let {
            Rect(marginLeft, marginTop, marginRight, marginBottom)
        } ?: Rect(0, 0, 0, 0)

/**
 * for view created programmatically we have to
 * request insets directly
 */
private fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        requestApplyInsets()
    } else {
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}