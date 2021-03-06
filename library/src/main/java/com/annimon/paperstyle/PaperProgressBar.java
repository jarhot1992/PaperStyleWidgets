package com.annimon.paperstyle;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * Material horizontal progress bar.
 * @author aNNiMON
 */
public class PaperProgressBar extends ProgressBar {

    private static final int ANIMATION_DELAY = 20;
    private static final int GRAY_LINE_COLOR = 0xFFC8C8C8;

    private final Paint mColorPaint = new Paint();

    private int mProgressColor;
    private int mSecondaryProgressColor;
    private float mIndeterminateProgress;

    public PaperProgressBar(Context context) {
        this(context, null);
    }

    public PaperProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.progressBarStyleHorizontal);
    }

    public PaperProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.PaperProgressBar, defStyleAttr, 0);
        try {
            mProgressColor = a.getColor(R.styleable.PaperProgressBar_pw_progressColor, 0xFF0F9D58);
            // Secondary progress by default is same color, but half transparent
            mSecondaryProgressColor = a.getColor(R.styleable.PaperProgressBar_pw_secondaryProgressColor,
                    0x7F000000 | (mProgressColor & 0xFFFFFF));
        } finally {
            a.recycle();
        }

        mColorPaint.setColor(mProgressColor);
        mColorPaint.setStyle(Paint.Style.FILL);
        mColorPaint.setAntiAlias(true);

        mIndeterminateProgress = 0;
        if (isIndeterminate()) {
            mIndeterminateHandler.sendEmptyMessage(0);
        }
    }

    public int getProgressColor() {
        return mProgressColor;
    }

    public void setProgressColor(int progressColor) {
        mProgressColor = progressColor;
        invalidate();
    }

    public int getSecondaryProgressColor() {
        return mSecondaryProgressColor;
    }

    public void setSecondaryProgressColor(int secondaryProgressColor) {
        mSecondaryProgressColor = secondaryProgressColor;
        invalidate();
    }

    @Override
    public synchronized void setIndeterminate(boolean indeterminate) {
        super.setIndeterminate(indeterminate);
        if (mIndeterminateHandler == null) return;
        if (indeterminate) {
            mIndeterminateHandler.sendEmptyMessage(0);
        } else {
            mIndeterminateHandler.removeMessages(0);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mColorPaint.setStrokeWidth(h / 3f);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        final float width = getWidth(), h2 = getHeight() / 2;
        mColorPaint.setColor(GRAY_LINE_COLOR);
        canvas.drawLine(0, h2, width, h2, mColorPaint);

        if (isIndeterminate()) {
            final float scaleX = 0.3f * (50 - Math.abs(mIndeterminateProgress - 50)) / 50f;
            final float lineX = mIndeterminateProgress * width / 100f;
            final float lineWidth = width * scaleX / 2;
            mColorPaint.setColor(mProgressColor);
            canvas.drawLine(lineX - lineWidth, h2, lineX + lineWidth, h2, mColorPaint);
            return;
        }

        if (getSecondaryProgress() > 0) {
            final float lineWidth = getSecondaryProgress() * width / getMax();
            mColorPaint.setColor(mSecondaryProgressColor);
            canvas.drawLine(0, h2, lineWidth, h2, mColorPaint);
        }
        if (getProgress() > 0) {
            final float lineWidth = getProgress() * width / getMax();
            mColorPaint.setColor(mProgressColor);
            canvas.drawLine(0, h2, lineWidth, h2, mColorPaint);
        }
    }

    private final Handler mIndeterminateHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            mIndeterminateProgress++;
            if (mIndeterminateProgress >= 100) {
                mIndeterminateProgress = 0;
            }
            invalidate();
            if (isIndeterminate()) {
                mIndeterminateHandler.sendEmptyMessageDelayed(0, ANIMATION_DELAY);
            }
        }
    };
}
