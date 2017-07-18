package com.jlouistechnology.magnawave.Fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextView_bld extends TextView {

	public TextView_bld(Context context) {
		super(context);
		setFont();
	}
	public TextView_bld(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFont();
	}
	public TextView_bld(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setFont();
	}

	private void setFont() {
		Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/OpenSans-Semibold.ttf");
		setTypeface(font, Typeface.NORMAL);
	}

}