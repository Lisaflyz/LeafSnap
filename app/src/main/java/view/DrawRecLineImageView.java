package view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class DrawRecLineImageView extends AppCompatImageView {

	public DrawRecLineImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	Paint paint = new Paint();
	{
		paint.setAntiAlias(true);
		paint.setColor(Color.RED);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(5f);
		paint.setAlpha(100);
		PathEffect effects = new DashPathEffect(new float[] { 4, 8, 8, 16}, 1);
		paint.setPathEffect(effects);
	};

	@SuppressLint("DrawAllocation") 
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.drawRect(new Rect(50, 100, 670, 950), paint);

	}

}
