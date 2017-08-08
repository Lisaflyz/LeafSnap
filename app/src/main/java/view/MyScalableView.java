package view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyScalableView extends AppCompatImageView {
	private float scale = 0.01f;


	private float beforeLenght;
	private float afterLenght;

	private float afterX, afterY;
	private float beforeX, beforeY;

	public MyScalableView(Context context) {
		super(context);
	}
	
	public MyScalableView(Context context,AttributeSet attrs) {
		super(context,attrs);
	}
	

	private void setLocation(int x, int y) {
		this.setFrame(this.getLeft() + x, this.getTop() + y, this.getRight()
				+ x, this.getBottom() + y);
	}

	private void setScale(float temp, int flag) {

		if (flag == 0) {
			this.setFrame(this.getLeft() - (int) (temp * this.getWidth()),
					this.getTop() - (int) (temp * this.getHeight()),
					this.getRight() + (int) (temp * this.getWidth()),
					this.getBottom() + (int) (temp * this.getHeight()));
		} else {
			this.setFrame(this.getLeft() + (int) (temp * this.getWidth()),
					this.getTop() + (int) (temp * this.getHeight()),
					this.getRight() - (int) (temp * this.getWidth()),
					this.getBottom() - (int) (temp * this.getHeight()));
		}
	}


	@SuppressLint("DrawAllocation") 
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	

	public void moveWithFinger(MotionEvent event) {

		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:
			beforeX = event.getX();
			beforeY = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			afterX = event.getX();
			afterY = event.getY();

			this.setLocation((int) (afterX - beforeX)/2, (int) (afterY - beforeY)/2);

			beforeX = afterX;
			beforeY = afterY;
			break;

		case MotionEvent.ACTION_UP:
			break;
		}
	}
	 
	public void scaleWithFinger(MotionEvent event) {
		float moveX = event.getX(1) - event.getX(0);
		float moveY = event.getY(1) - event.getY(0);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			beforeLenght = (float) Math.sqrt((moveX * moveX) + (moveY * moveY));
			break;
		case MotionEvent.ACTION_MOVE:
			afterLenght = (float) Math.sqrt((moveX * moveX) + (moveY * moveY));

			float gapLenght = afterLenght - beforeLenght;

			if (gapLenght == 0) {
				break;
			}
			if (gapLenght > 0) {
				this.setScale(scale, 0);
			} else {
				this.setScale(scale, 1);
			}
			beforeLenght = afterLenght;
			break;
		}
	}

}
