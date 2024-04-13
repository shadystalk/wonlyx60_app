/*
 *  UVCCamera
 *  library and sample to access to UVC web camera on non-rooted Android device
 *
 * Copyright (c) 2014-2017 saki t_saki@serenegiant.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *  All files in the folder are under this Apache License, Version 2.0.
 *  Files in the libjpeg-turbo, libusb, libuvc, rapidjson folder
 *  may have a different license, see the respective files.
 */

package com.wl.wlflatproject.MView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.View;

/**
 * change the view size with keeping the specified aspect ratio.
 * if you set this view with in a FrameLayout and set property "android:layout_gravity="center",
 * you can show this view in the center of screen and keep the aspect ratio of content
 * XXX it is better that can set the aspect raton a a xml property
 */
public class SimpleUVCCameraTextureView extends TextureView	// API >= 14
	implements AspectRatioViewInterface {

    private double mRequestedAspect = -1.0;

	public SimpleUVCCameraTextureView(final Context context) {
		this(context, null, 0);
	}

	public SimpleUVCCameraTextureView(final Context context, final AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SimpleUVCCameraTextureView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onResume() {
	}

	@Override
	public void onPause() {
	}

	@Override
    public void setAspectRatio(final double aspectRatio) {
        if (aspectRatio < 0) {
            throw new IllegalArgumentException();
        }
        if (mRequestedAspect != aspectRatio) {
            mRequestedAspect = aspectRatio;
            requestLayout();
        }
    }


    int mRatioWidth;
    int mRatioHeight;
	public void setAspectRatio(int width, int height) {
		if (width >= 0 && height >= 0) {
			this.mRatioWidth = width;
			this.mRatioHeight = height;
		}
	}


	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = View.MeasureSpec.getSize(widthMeasureSpec);
		int height = View.MeasureSpec.getSize(heightMeasureSpec);
		if (0 != this.mRatioWidth && 0 != this.mRatioHeight) {
			if (width > height * this.mRatioWidth / this.mRatioHeight) {
				this.setMeasuredDimension(width, width * this.mRatioHeight / this.mRatioWidth);
			} else {
				int i = height * this.mRatioWidth / this.mRatioHeight;
				this.setMeasuredDimension(height * this.mRatioWidth / this.mRatioHeight+380, height);
			}
		} else {
			this.setMeasuredDimension(width, height);
		}

	}
}
