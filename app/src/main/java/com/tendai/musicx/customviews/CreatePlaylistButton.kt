package com.tendai.musicx.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import com.tendai.musicx.databinding.ButtonCreatePlaylistBinding
import kotlin.math.max

class CreatePlaylistButton(context: Context, attributeSet: AttributeSet) :
    ViewGroup(context, attributeSet) {

    private lateinit var binding: ButtonCreatePlaylistBinding

    /**
     * Validates if a set of layout parameters is valid for a child of this ViewGroup.
     */
    override fun checkLayoutParams(p: LayoutParams?): Boolean {
        return p is LayoutParams
    }

    /**
     * @return A set of default layout parameters when given a child with no layout parameters.
     */
    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    /**
     * @return A set of layout parameters created from attributes passed in XML.
     */
    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        binding = ButtonCreatePlaylistBinding.bind(this)
    }

    override fun onLayout(changed: Boolean, left: Int, right: Int, top: Int, bottom: Int) {
        with(binding) {
            var layoutParams = textviewCreatePlaylist.layoutParams as MarginLayoutParams

            //figure out the textView's x and y co-ordinates
            val textViewX = paddingLeft + layoutParams.leftMargin
            val textViewY = paddingTop + layoutParams.topMargin

            //find the the midpoint of the textView's y co-ordinates.
            val textViewYMidpoint =
                (textViewY + (textViewY + textviewCreatePlaylist.measuredHeight)) / 2

            // Calculate the right x-coordinate of the textView: textView's right coordinate +
            // the textView's right margin.
            var iconX = textViewX + textviewCreatePlaylist.measuredWidth + layoutParams.rightMargin

            //add the icon's left margin
            layoutParams = iconCreatePlaylist.layoutParams as MarginLayoutParams
            iconX += layoutParams.leftMargin

            //find the y-coordinate of the icon
            val iconY = paddingTop + layoutParams.topMargin

            //
            val iconYMidpoint = (iconY + (iconY + iconCreatePlaylist.measuredHeight)) / 2

            //differences between the midpoints
            val diff = iconYMidpoint - textViewYMidpoint

            //layout the textView
            textviewCreatePlaylist.layout(
                textViewX,
                //this is to center align the textview and the icon.
                textViewY + diff,
                textViewX + textviewCreatePlaylist.measuredWidth,
                //this is to center align the textview and the icon.
                textViewY + diff + textviewCreatePlaylist.measuredHeight
            )

            //layout the icon
            iconCreatePlaylist.layout(
                iconX,
                iconY,
                iconX + iconCreatePlaylist.measuredWidth,
                iconY + iconCreatePlaylist.measuredHeight
            )
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        with(binding) {
            //measure the textview
            // Pass width and height constraints already used. Initially they are zero
            measureChildWithMargins(
                textviewCreatePlaylist, widthMeasureSpec, 0, heightMeasureSpec, 0
            )

            //figure how much width the textview has used since we want to place another view in front of the textview
            // that is why the height is zero as we are stacking views horizontally
            var layoutParams = textviewCreatePlaylist.layoutParams as MarginLayoutParams
            val textViewWidth =
                textviewCreatePlaylist.measuredWidth + layoutParams.leftMargin + layoutParams.rightMargin

            //measure the imageview /icon
            // Pass width and height constraints already used.
            measureChildWithMargins(
                iconCreatePlaylist, widthMeasureSpec, textViewWidth, heightMeasureSpec, 0
            )

            // Figure out how much total space the textview used.
            val textViewHeight =
                textviewCreatePlaylist.measuredHeight + layoutParams.topMargin + layoutParams.bottomMargin

            layoutParams = iconCreatePlaylist.layoutParams as MarginLayoutParams

            //Figure out how much total space the icon used
            val iconWidth =
                iconCreatePlaylist.measuredWidth + layoutParams.leftMargin + layoutParams.rightMargin
            val iconHeight =
                iconCreatePlaylist.measuredHeight + layoutParams.topMargin + layoutParams.bottomMargin

            // The width taken by the children and the padding
            val totalWidth = textViewWidth + iconWidth + paddingLeft + paddingRight
            val totalHeight = max(textViewHeight, iconHeight) + paddingTop + paddingBottom

            // Reconcile the measured dimensions with the this view's constraints and
            // set the final measured width and height.
            setMeasuredDimension(
                resolveSize(totalWidth, widthMeasureSpec),
                resolveSize(totalHeight, heightMeasureSpec)
            )
        }
    }

}
