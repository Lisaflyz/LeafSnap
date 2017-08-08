package tools;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Range;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.EM;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.util.Log;

import com.imageclassifier.user.leafsnap.DemoActivity;

import beans.Plant;

public class Tools {
	public static final int MAX_RADIUS = 26;
	public static final int MIN_RADIUS = 2;
	public static Mat curvMat;
	public static List<Mat> listHists;
	public static List<Plant> topList;
	public static Mat rawCurvMat;
	private static final String TAG = "Tools";

	private static final int RN = 10;
	private static final int CN = 10;

	public Tools() {
		super();
	}

	public static Bitmap resize(Bitmap bitmap, int height, int width) {
		Mat mat = new Mat();
		Utils.bitmapToMat(bitmap, mat);
		Mat dst = new Mat();
		Imgproc.resize(mat, dst, new Size(width, height), 0, 0,
				Imgproc.INTER_LINEAR);
		Bitmap bitmap2;
		Config config = Config.RGB_565;
		bitmap2 = Bitmap.createBitmap(width, height, config);
		Utils.matToBitmap(dst, bitmap2);
		return bitmap2;
	}

	public static Bitmap em(Bitmap bitmap, Context context, Handler handler) {

		Mat src = new Mat();
		Utils.bitmapToMat(bitmap, src);

		final Mat hsvMat = new Mat();

		Imgproc.cvtColor(src, hsvMat, Imgproc.COLOR_BGR2HSV);



		int nSamples = (src.width() / CN) * (src.height() / RN);
		Log.i(TAG, "nSample = " + nSamples);

		// 采集样本时纵向间隔
		int rSpace = src.width() / CN;
		// 采集样本时横向间隔
		int cSpace = src.height() / RN;
		// 训练样本,样本必须为单通道
		final Mat samples = new Mat(rSpace * cSpace, 2, CvType.CV_32FC1);
		// 两通道
		samples.reshape(2);

		for (int i = 0; i < rSpace; i++) {
			for (int j = 0; j < cSpace; j++) {
				int x = j * CN;
				int y = i * RN;
				samples.put(i * cSpace + j, 0, new double[] {
						hsvMat.get(x, y)[1], hsvMat.get(x, y)[2] });
			}
		}

		int nclusters = 2;
		TermCriteria termCrit = new TermCriteria();
		final EM myEm = new EM(nclusters, EM.COV_MAT_DIAGONAL, termCrit);

		final Mat dst = new Mat(src.rows(), src.cols(), CvType.CV_8UC1);

		// 图片上的任意一点
		final Mat preditSample = new Mat(1, 2, CvType.CV_32FC1);

		if (myEm.train(samples)) {
			Log.i(TAG, "EM训练成功~~~~~~");
			if (null != handler)
				handler.sendEmptyMessage(DemoActivity.MSG_TRAIN_SUCCESS);
		} else {
			if (null != handler)
				handler.sendEmptyMessage(DemoActivity.MSG_TRAIN_FAILED);
			Log.i(TAG, "Oops...EM训练失败。。。");

		}

		// 用训练过的EM对象预测整幅图片
		for (int i = 0; i < hsvMat.rows(); i++) {
			for (int j = 0; j < hsvMat.cols(); j++) {
				preditSample.put(0, 0, new double[] { hsvMat.get(i, j)[1],
						hsvMat.get(i, j)[2] });
				double[] v = myEm.predict(preditSample);
				if (v[1] == 1) {
					dst.put(i, j, 0);
				} else {
					dst.put(i, j, 255);
				}
			}
		}

		Log.i(TAG, "predict finish...");

		int x1 = (int) dst.get(0, 0)[0];
		int x2 = (int) dst.get(0, dst.cols() - 1)[0];
		int x3 = (int) dst.get(dst.rows() - 1, 0)[0];
		int x4 = (int) dst.get(dst.rows() - 1, dst.cols() - 1)[0];

		if (x1 == 255 && x2 == 255 & x3 == 255 & x4 == 255) {
			Core.bitwise_not(dst, dst);
		}
		// Core.

		Bitmap bitmap2 = Bitmap.createBitmap(dst.width(), dst.height(),
				Config.RGB_565);
		Utils.matToBitmap(dst, bitmap2);
		DemoActivity.binaryMat = dst;
		return bitmap2;
	}

	public static Bitmap dilation() {
		if(DemoActivity.binaryMat == null){
			return null;
		}
		Mat dst = new Mat();
		Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,
				new Size(3, 3));

		Imgproc.dilate(DemoActivity.binaryMat, dst, kernel);
		DemoActivity.binaryMat = dst;
		Bitmap bitmap2 = Bitmap.createBitmap(DemoActivity.binaryMat.width(),
				DemoActivity.binaryMat.height(), Config.RGB_565);
		Utils.matToBitmap(dst, bitmap2);
		return bitmap2;
	}

	public static Bitmap topHat() {
		if(DemoActivity.binaryMat == null){
			return null;
		}
		Mat dst = new Mat();
		Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS,
				new Size(20, 20));
		Imgproc.morphologyEx(DemoActivity.binaryMat, dst, Imgproc.MORPH_TOPHAT,
				kernel);

		Bitmap bitmap2 = Bitmap.createBitmap(DemoActivity.binaryMat.width(),
				DemoActivity.binaryMat.height(), Config.RGB_565);
		Utils.matToBitmap(dst, bitmap2);
		return bitmap2;
	}

	public static List<MatOfPoint> getContours() {
		if(DemoActivity.binaryMat == null){
			return null;
		}

		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(DemoActivity.binaryMat, contours, hierarchy,
				Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
		Log.i(TAG, "*******contours size = " + contours.size());

		int index = 0;
		int maxRows = 0;
		for (int i = 0; i < contours.size(); i++) {
			Mat mat = contours.get(i);
			if (mat.rows() > maxRows) {
				index = i;
				maxRows = mat.rows();
			}

		}
		List<MatOfPoint> list = new ArrayList<MatOfPoint>();
		list.add(contours.get(index));
		return list;
	}

	public static List<Mat> getHistograms(Mat curvMat) {
		// Mat normlizedMat = getCurvImg(contours);

		List<Mat> histImags = new ArrayList<Mat>();
		Tools.listHists = new ArrayList<Mat>();
		// 依次求每个尺寸的曲率直方图
		for (int r = 0; r < curvMat.rows(); r++) {
			List<Mat> images = new ArrayList<Mat>();
			images.add(curvMat.row(r));
			// 求曲率直方图的结果
			Mat hist = new Mat();

			float[] range = new float[] { 0, 255 };
			MatOfFloat ranges = new MatOfFloat(range);

			int[] channel = new int[] { 0 };
			MatOfInt channels = new MatOfInt(channel);

			int[] size = new int[] { 50 };

			MatOfInt histSize = new MatOfInt(size);

			Imgproc.calcHist(images, channels, new Mat(), hist, histSize,
					ranges);

			Tools.listHists.add(hist);

			double minVal, maxVal;
			Core.MinMaxLocResult mmLocR = Core.minMaxLoc(hist);
			maxVal = mmLocR.maxVal;
			minVal = mmLocR.minVal;

			final int scale = 10;
			final int ROWS = 300;
			final int COLS = hist.rows() * scale;
			Mat histImg = Mat.zeros(ROWS, COLS, CvType.CV_8UC1);

			for (int i = 0; i < hist.rows(); i++) {

				Core.rectangle(
						histImg,
						new Point(i * scale, ROWS - 1),
						new Point((i + 1) * scale - 1, ROWS - ROWS
								* (hist.get(i, 0)[0] - minVal)
								/ (maxVal - minVal)), new Scalar(255, 255, 0));
			}

			histImags.add(histImg);

		}
		return histImags;
	}

	public static Mat getCurvImg(List<MatOfPoint> contours) {
		MatOfPoint mat = contours.get(0);

		Mat curvMat = new Mat(MAX_RADIUS - MIN_RADIUS + 1, mat.rows(),
				CvType.CV_32FC1);

		final int ROWS = DemoActivity.binaryMat.rows();
		final int COLS = DemoActivity.binaryMat.cols();

		for (int radius = MIN_RADIUS; radius <= Tools.MAX_RADIUS; radius++) {
			for (int row = 0; row < mat.rows(); row++) {
				double[] point = mat.get(row, 0);

				// ===============================================================

				int row1 = (int) (point[1] - radius <= 0 ? 0 : point[1]
						- radius);
				int row2 = (int) (point[1] + radius >= ROWS ? ROWS - 1
						: point[1] + radius);

				int col1 = (int) (point[0] - radius <= 0 ? 0 : point[0]
						- radius);
				int col2 = (int) (point[0] + radius >= COLS ? COLS - 1
						: point[0] + radius);

				Mat subMat = DemoActivity.binaryMat.submat(new Range(
								row1, row2),
						new Range(col1, col2));
				Mat mask = Mat.zeros(row2-row1, col2-col1, CvType.CV_8UC1);

				// ============================================================
				Core.circle(mask, new Point(radius, radius), radius,
						new Scalar(255, 255, 255), -1);
				// ============================================================

				Mat dst = new Mat();
				Core.bitwise_and(subMat, mask, dst);
				int curv = Core.countNonZero(dst);
				curvMat.put(radius, row, curv);
			}
		}

		// =====================================================
		Tools.rawCurvMat = curvMat;
		Mat normlizedMat = new Mat();
		Core.normalize(curvMat, normlizedMat, 0, 255, Core.NORM_MINMAX);
		Tools.curvMat = normlizedMat;
		return normlizedMat;
	}

	public static void test() {
		List<Mat> histImags = new ArrayList<Mat>();

		// 依次求每个尺寸的曲率直方图
		List<Mat> images = new ArrayList<Mat>();
		for (int r = 0; r < 2; r++) {
			images.add(curvMat.row(r));

		}
		// 求曲率直方图的结果
		Mat hist = new Mat();

		float[] range = new float[] { 0, 255, 0, 255 };
		MatOfFloat ranges = new MatOfFloat(range);

		int[] channel = new int[] { 0, 0 };
		MatOfInt channels = new MatOfInt(channel);

		int[] size = new int[] { 50, 50 };

		MatOfInt histSize = new MatOfInt(size);

		Imgproc.calcHist(images, channels, new Mat(), hist, histSize, ranges);

		double minVal, maxVal;
		Core.MinMaxLocResult mmLocR = Core.minMaxLoc(hist);
		maxVal = mmLocR.maxVal;
		minVal = mmLocR.minVal;

		final int scale = 10;
		final int ROWS = 300;
		final int COLS = hist.rows() * scale;
		Mat histImg = Mat.zeros(ROWS, COLS, CvType.CV_8UC1);

		for (int i = 0; i < hist.rows(); i++) {

			Core.rectangle(
					histImg,
					new Point(i * scale, ROWS - 1),
					new Point((i + 1) * scale - 1, ROWS - ROWS
							* (hist.get(i, 0)[0] - minVal) / (maxVal - minVal)),
					new Scalar(255, 255, 0));
		}

		histImags.add(histImg);

	}

	public static Mat histgramsToOne(List<Mat> list) {
		Mat mat = list.get(0);
		Core.normalize(mat, mat, 1, 0, Core.NORM_L1);
		for (int i = 1; i < list.size(); i++) {
			Mat temp = list.get(i);
			Core.normalize(temp, temp, 1, 0, Core.NORM_L1);
			mat.push_back(temp);
		}
		return mat;

	}

	public static StringBuffer histGramsToString(List<Mat> list) {

		StringBuffer sb = new StringBuffer();
		/*
		 * for (int i = 0; i < list.size(); i++) { Mat mat = list.get(i);
		 * Core.normalize(mat, mat, 1, 0, Core.NORM_L1); double sum = 0; for
		 * (int j = 0; j < mat.rows(); j++) { double[] val = mat.get(j, 0); sum
		 * += val[0]; sb = sb.append(val[0] + ","); } Log.i(TAG, "sum:" + sum);
		 * }
		 */

		Mat mat = histgramsToOne(list);
		for (int i = 0; i < mat.rows(); i++) {
			double[] vals = mat.get(i, 0);
			sb.append(vals[0] + ",");
		}

		return sb;
	}

	public static Mat stringToHistgrams(String string) {
		String[] strs = string.split(",");
		double[] histVals = new double[strs.length];
		Log.i(TAG, "hist length: " + histVals.length);

		double sum = 0;
		for (int i = 0; i < histVals.length; i++) {
			histVals[i] = Double.valueOf(strs[i]);
			sum += histVals[i];
		}

		Log.i(TAG, "out database sum = " + sum);

		Mat mat = new MatOfDouble(histVals);
		//
		// Log.i(TAG, "hist mat channels:" + mat.channels());
		// Log.i(TAG, "hist mat width:" + mat.width());
		// Log.i(TAG, "hist mat heigth:" + mat.height());
		// Log.i(TAG, "hist mat depth:" + mat.depth());

		return mat;
	}

	@SuppressLint("UseSparseArrays")
	public static List<Plant> getTopSortMatchedPlants(List<Plant> plantLists,
													  int topNum) {

		//=======================================
//		plantLists.
//		List list=new ArrayList();
//		list.add(1);
//		list.add("sssss");

		//=======================================

		if(listHists == null){
			return null;
		}
		Mat src1 = histgramsToOne(listHists);
		Log.i(TAG, "*******plantsLists size = " + plantLists.size());
		src1.convertTo(src1, CvType.CV_32FC1);

		LinkedList<Plant> linkedList = new LinkedList<Plant>();
		for (int i = 0; i < plantLists.size(); i++) {
			Plant plant = plantLists.get(i);
			Mat src2 = stringToHistgrams(plant.getPhists());
			src2.convertTo(src2, CvType.CV_32FC1);
			double val = Imgproc.compareHist(src1, src2,
					Imgproc.CV_COMP_INTERSECT);
			plant.setVal(val);
			if (linkedList.isEmpty()) {
				linkedList.add(0, plant);
			} else {
				int j = 0;
				for (j = 0; j < linkedList.size(); j++) {
					if (linkedList.get(j).getVal() <= val) {
						linkedList.add(j, plant);
						break;
					}
				}
				if (j == linkedList.size()) {
					linkedList.add(j, plant);
				}
			}
		}
		return linkedList;
	}
}
