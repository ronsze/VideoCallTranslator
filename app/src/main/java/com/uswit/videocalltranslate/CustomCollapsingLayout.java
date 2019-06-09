package com.uswit.videocalltranslate;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.AppBarLayout;

public class CustomCollapsingLayout extends FrameLayout {

    private static final String TAG = "CollapsingImageLayout";

    private WindowInsetsCompat mLastInsets;

    private int mTitleLeftExpanded;

    private int mTitleTopExpanded;

    private int mTitleLeftCollapsed;

    private int mTitleTopCollapsed;

    private OnOffsetChangedListener mOnOffsetChangedListener;

    public CustomCollapsingLayout(Context context) {
        this(context, null);
    }

    public CustomCollapsingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomCollapsingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mTitleLeftCollapsed = getResources().getDimensionPixelOffset(R.dimen.title_left_margin_collapsed);
        mTitleTopCollapsed = getResources().getDimensionPixelOffset(R.dimen.title_top_margin_collapsed);

        ViewCompat.setOnApplyWindowInsetsListener(this, (v, insets) -> setWindowInsets(insets));
    }

    @TargetApi(21)
    public CustomCollapsingLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        // Add an OnOffsetChangedListener if possible
        final ViewParent parent = getParent();
        if (parent instanceof AppBarLayout) {
            if (mOnOffsetChangedListener == null) {
                mOnOffsetChangedListener = new OnOffsetChangedListener();
            }
            ((AppBarLayout) parent).addOnOffsetChangedListener(mOnOffsetChangedListener);
        }

        // We're attached, so lets request an inset dispatch
        ViewCompat.requestApplyInsets(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        // Remove our OnOffsetChangedListener if possible and it exists
        final ViewParent parent = getParent();
        if (mOnOffsetChangedListener != null && parent instanceof AppBarLayout) {
            ((AppBarLayout) parent).removeOnOffsetChangedListener(mOnOffsetChangedListener);
        }

        super.onDetachedFromWindow();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // Update our child view offset helpers
        for (int i = 0, z = getChildCount(); i < z; i++) {
            final View child = getChildAt(i);

            if (mLastInsets != null && !ViewCompat.getFitsSystemWindows(child)) {
                final int insetTop = mLastInsets.getSystemWindowInsetTop();
                if (child.getTop() < insetTop) {
                    // If the child isn't set to fit system windows but is drawing within the inset
                    // offset it down
                    ViewCompat.offsetTopAndBottom(child, insetTop);
                }
            }

            getViewOffsetHelper(child).onViewLayout();

            switch (child.getId()) {
                case R.id.txt_vichat:
                    mTitleLeftExpanded = child.getLeft();
                    mTitleTopExpanded = child.getTop();
                    break;
            }
        }
    }

    private WindowInsetsCompat setWindowInsets(WindowInsetsCompat insets) {
        if (mLastInsets != insets) {
            mLastInsets = insets;
            requestLayout();
        }
        return insets.consumeSystemWindowInsets();
    }

    class OnOffsetChangedListener implements AppBarLayout.OnOffsetChangedListener {

        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

            final int insetTop = mLastInsets != null ? mLastInsets.getSystemWindowInsetTop() : 0;
            final int scrollRange = appBarLayout.getTotalScrollRange();
            float offsetFactor = (float) (-verticalOffset) / (float) scrollRange;
            final int heightDiff = getHeight() - getMinimumHeight();

            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                final ViewOffsetHelper offsetHelper = getViewOffsetHelper(child);

                if (child instanceof Toolbar) {
                    if (getHeight() - insetTop + verticalOffset >= child.getHeight()) {
                        offsetHelper.setTopAndBottomOffset(-verticalOffset); // pin
                    }
                }

                if (child.getId() == R.id.setting) {
                    if (getHeight() - insetTop + verticalOffset >= child.getHeight()) {
                        offsetHelper.setTopAndBottomOffset(-verticalOffset); // pin
                    }
                }

                if (child.getId() == R.id.txt_vichat) {

                    float scaleFactor = 1F - offsetFactor * .3F ;
                    child.setScaleX(scaleFactor);
                    child.setScaleY(scaleFactor);

                    int topOffset = (int) ((mTitleTopCollapsed - mTitleTopExpanded) * offsetFactor) - verticalOffset;
                    int leftOffset = (int) ((mTitleLeftCollapsed - mTitleLeftExpanded) * offsetFactor);
                    offsetHelper.setTopAndBottomOffset(topOffset);
                    offsetHelper.setLeftAndRightOffset(leftOffset);
                }
            }
        }
    }

    private static ViewOffsetHelper getViewOffsetHelper(View view) {
        ViewOffsetHelper offsetHelper = (ViewOffsetHelper) view.getTag(R.id.view_offset_helper);
        if (offsetHelper == null) {
            offsetHelper = new ViewOffsetHelper(view);
            view.setTag(R.id.view_offset_helper, offsetHelper);
        }
        return offsetHelper;
    }

    static class ViewOffsetHelper {

        private final View mView;

        private int mLayoutTop;
        private int mLayoutLeft;
        private int mOffsetTop;
        private int mOffsetLeft;

        public ViewOffsetHelper(View view) {
            mView = view;
        }

        public void onViewLayout() {
            // Now grab the intended top
            mLayoutTop = mView.getTop();
            mLayoutLeft = mView.getLeft();

            // And offset it as needed
            updateOffsets();
        }

        private void updateOffsets() {
            ViewCompat.offsetTopAndBottom(mView, mOffsetTop - (mView.getTop() - mLayoutTop));
            ViewCompat.offsetLeftAndRight(mView, mOffsetLeft - (mView.getLeft() - mLayoutLeft));

            // Manually invalidate the view and parent to make sure we get drawn pre-M
        }

        private static void tickleInvalidationFlag(View view) {
            final float y = ViewCompat.getTranslationY(view);
            ViewCompat.setTranslationY(view, y + 1);
            ViewCompat.setTranslationY(view, y);
        }

        /**
         * Set the top and bottom offset for this {@link ViewOffsetHelper}'s view.
         *
         * @param offset the offset in px.
         * @return true if the offset has changed
         */
        public boolean setTopAndBottomOffset(int offset) {
            if (mOffsetTop != offset) {
                mOffsetTop = offset;
                updateOffsets();
                return true;
            }
            return false;
        }

        /**
         * Set the left and right offset for this {@link ViewOffsetHelper}'s view.
         *
         * @param offset the offset in px.
         * @return true if the offset has changed
         */
        public boolean setLeftAndRightOffset(int offset) {
            if (mOffsetLeft != offset) {
                mOffsetLeft = offset;
                updateOffsets();
                return true;
            }
            return false;
        }

        public int getTopAndBottomOffset() {
            return mOffsetTop;
        }

        public int getLeftAndRightOffset() {
            return mOffsetLeft;
        }
    }
}
