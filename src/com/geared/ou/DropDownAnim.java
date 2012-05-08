/*
 * Author: Seth @ stackoverflow.com
 * see: http://stackoverflow.com/users/544007/seth
 */
package com.geared.ou;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class DropDownAnim extends Animation {
    public static final int COLLAPSED = 0;
    public static final int EXPANDED = 1;
    int targetHeight;
    View view;
    boolean down;

    public DropDownAnim(View view, int targetHeight, int direction) {
        this.view = view;
        this.targetHeight = targetHeight;
        if (direction == EXPANDED)
            down = true;
        else
            down = false;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int newHeight;
        if (down) {
            newHeight = (int) (targetHeight * interpolatedTime);
        } else {
            newHeight = (int) (targetHeight * (1 - interpolatedTime));
        }
        view.getLayoutParams().height = newHeight;
        view.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth,
            int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
