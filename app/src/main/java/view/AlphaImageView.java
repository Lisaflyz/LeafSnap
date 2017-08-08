package view;

import java.util.Timer;
import java.util.TimerTask;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.imageclassifier.user.leafsnap.R;

@SuppressLint("HandlerLeak")
public class AlphaImageView extends AppCompatImageView {
	private int alphaDeta;
	private int curAlpha ;
	private final int SPEED = 500;

	Handler handler = new Handler() {
		@SuppressWarnings("deprecation")
		@SuppressLint("NewApi")
		@Override
		//图片以一定的速率逐渐清晰呈现
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 0x123) {
				curAlpha += alphaDeta;
				if (curAlpha >= 255)
					curAlpha = 255;
				setAlpha(curAlpha);
			}
		}

	};

	@SuppressLint("Recycle")
	//初始化，构造函数
	public AlphaImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.AlphaImageView);
		//在attrs中设立属性值，在java类中为属性值设置值
		int duration = typedArray.getInt(R.styleable.AlphaImageView_myduration,
				10000);
		alphaDeta = 255 * SPEED / duration;

	}

	@SuppressWarnings("deprecation")
	@SuppressLint({ "NewApi", "DrawAllocation" })
	@Override
	//在画布上渲染，
	protected void onDraw(Canvas canvas) {
		this.setAlpha(curAlpha);
		super.onDraw(canvas);

		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (curAlpha >= 255) {
					timer.cancel();
				} else {
					handler.sendEmptyMessage(0x123);
				}
			}
		}, 0, SPEED);
	}
}
