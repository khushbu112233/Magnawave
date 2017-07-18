package com.jlouistechnology.magnawave.utils;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.jlouistechnology.magnawave.R;

/**
 * Created by aipxperts on 10/5/17.
 */

public class Constants {

    public static Context _context;

    public Constants(Context context) {
        this._context = context;
    }


    public static final String[] Login_param=
            {
                    "email",
                    "password"

            };
    public static final String[] Forgot_param=
            {
                    "email"

            };
    public static final String[] Signin_param=
            {
                    "name",
                    "email",
                    "password"

            };
    public static void setTextWatcher(final Context context, final EditText editText, final TextView textView) {

        TextWatcher textWatcher = new TextWatcher() {
            boolean flag = false;

            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                if (arg0.length() == 0) {
                    flag = true;
                } else {
                    flag = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int a, int b, int c) {




                if (s.length() == 0) {
                    Animation slidedown = AnimationUtils.loadAnimation(context, R.anim.hint_slide_down);
                    textView.startAnimation(slidedown);
                    textView.setVisibility(View.INVISIBLE);

                } else {
                    if (s.length() == 1 && flag) {
                        Animation slideup = AnimationUtils.loadAnimation(context, R.anim.hint_slide_up);
                        textView.startAnimation(slideup);

                    }
                    editText.setError(null);
                    textView.setVisibility(View.VISIBLE);
                }
            }
        };
        editText.addTextChangedListener(textWatcher);
    }
}
