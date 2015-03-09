package com.openpeer.sample.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;
import com.openpeer.sample.R;

public class ClearableSearchEditText extends EditText implements TextWatcher {
    private static final int LEFT = 0, TOP = 1, RIGHT = 2, BOTTOM = 3;

    public ClearableSearchEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        addTextChangedListener(this);
    }

    public ClearableSearchEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Drawable dRight = getCompoundDrawables()[RIGHT];
        if (event.getAction() == MotionEvent.ACTION_UP && dRight != null) {
            Rect rBounds = dRight.getBounds();
            final int x = (int) event.getX();
            final int y = (int) event.getY();

            //check to make sure the touch event was within the bounds of the drawable
            if (x >= (this.getRight() - rBounds.width()) && x <= (this.getRight() - this.getPaddingRight())
                    && y >= this.getPaddingTop() && y <= (this.getHeight() - this.getPaddingBottom())) {
                //System.out.println("touch");
                this.setText("");
                event.setAction(MotionEvent.ACTION_CANCEL);//use this to prevent the keyboard from coming up
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 0) {
            Drawable drawable;
            if (getCompoundDrawables()[RIGHT] == null) {
                drawable = getResources().getDrawable(R.drawable.ic_search_cancel);
            } else{
                drawable = getCompoundDrawables()[RIGHT];
            }
            setCompoundDrawablesWithIntrinsicBounds(getCompoundDrawables()[LEFT], getCompoundDrawables()[TOP], drawable, getCompoundDrawables()[BOTTOM]);
        } else {
            setCompoundDrawablesWithIntrinsicBounds(getCompoundDrawables()[LEFT], getCompoundDrawables()[TOP], null, getCompoundDrawables()[BOTTOM]);
        }
    }
}
