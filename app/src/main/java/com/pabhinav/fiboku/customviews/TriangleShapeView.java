package com.pabhinav.fiboku.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.pabhinav.fiboku.R;

/**
 * @author pabhinav
 */
public class TriangleShapeView extends View {

    public TriangleShapeView(Context context) {
        super(context);
    }

    public TriangleShapeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TriangleShapeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TriangleShapeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(getWidth(), getHeight());
        path.lineTo(0, getHeight());
        path.lineTo(0, 0);
        path.close();

        Paint p = new Paint();
        p.setColor(getResources().getColor(R.color.colorPrimary));

        canvas.drawPath(path,p);
    }
}
