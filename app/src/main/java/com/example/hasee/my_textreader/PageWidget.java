package com.example.hasee.my_textreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Region;
import android.graphics.drawable.GradientDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

/**
 * Created by hasee on 2016/4/8.
 */
public class PageWidget extends View {
    private static final String TAG = "Book_Turn";
    private int mWidth = 480;
    private int mHeight = 800;
    private int mCornerX = 0;//拖曳点对应页脚
    private int mCornerY = 0;
    private Path mPath0;
    private Path mPath1;
    Bitmap mCurPageBitmap = null;//当前页
    Bitmap mNextPageBitmap = null;
    //PointF: PointF holds two float coordinates
    PointF mTouch = new PointF();//拖曳点
    PointF mBezierStart1 = new PointF();//贝塞尔曲线起始点
    PointF mBezierControl1 = new PointF();//贝塞尔曲线控制点
    PointF mBeziervertex1 = new PointF();//贝塞尔曲线顶点
    PointF mBezierEnd1 = new PointF();//贝塞尔曲线结束点

    PointF mBezierStart2 = new PointF();//另一条贝塞尔曲线
    PointF mBezierControl2 = new PointF();
    PointF mBeziervertex2 = new PointF();
    PointF mBezierEnd2 = new PointF();

    float mMiddleX;
    float mMiddleY;
    float mDegrees;
    float mTouchToCornerDis;
    ColorMatrixColorFilter mColorMatrixFilter;
    Matrix mMatrix;
    float[] mMatrixArray = {0,0,0,0,0,0,0,0,1.0f};
    boolean mIsRTandLB;//是否属于右上左下
    float mMaxLength = (float)Math.hypot(mWidth, mHeight);
    int[] mBackShadowColors;
    int[] mFrontShadowColors;
    GradientDrawable mBackShadowDrawableLR;
    GradientDrawable mBackShadowDrawableRL;
    GradientDrawable mFolderShadowDrawableLR;
    GradientDrawable mFolderShadowDrawableRL;

    GradientDrawable mFolderShadowDrawableHBT;
    GradientDrawable mFolderShadowDrawableHTB;

    GradientDrawable mFolderShadowDrawableVLR;
    GradientDrawable mFolderShadowDrawableVRL;

    Paint mPaint;
    Scroller mScroller;
    public PageWidget(Context context){
        super(context);
        // TODO Auto-generated constructor stub
        /**
         61.          * Paint类介绍
         62.          *
         63.          * Paint即画笔，在绘图过程中起到了极其重要的作用，画笔主要保存了颜色，
         64.          * 样式等绘制信息，指定了如何绘制文本和图形，画笔对象有很多设置方法，
         65.          * 大体上可以分为两类，一类与图形绘制相关，一类与文本绘制相关。
         66.          *
         67.          * 1.图形绘制
         68.          * setARGB(int a,int r,int g,int b);
         69.          * 设置绘制的颜色，a代表透明度，r，g，b代表颜色值。
         70.          *
         71.          * setAlpha(int a);
         72.          * 设置绘制图形的透明度。
         73.          *
         74.          * setColor(int color);
         75.          * 设置绘制的颜色，使用颜色值来表示，该颜色值包括透明度和RGB颜色。
         76.          *
         77.         * setAntiAlias(boolean aa);
         78.          * 设置是否使用抗锯齿功能，会消耗较大资源，绘制图形速度会变慢。
         79.          *
         80.          * setDither(boolean dither);
         81.          * 设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
         82.          *
         83.          * setFilterBitmap(boolean filter);
         84.          * 如果该项设置为true，则图像在动画进行中会滤掉对Bitmap图像的优化操作，加快显示
         85.          * 速度，本设置项依赖于dither和xfermode的设置
         86.          *
         87.          * setMaskFilter(MaskFilter maskfilter);
         88.          * 设置MaskFilter，可以用不同的MaskFilter实现滤镜的效果，如滤化，立体等       *
         89.          * setColorFilter(ColorFilter colorfilter);
         90.          * 设置颜色过滤器，可以在绘制颜色时实现不用颜色的变换效果
         91.          *
         92.          * setPathEffect(PathEffect effect);
         93.          * 设置绘制路径的效果，如点画线等
         94.          *
         95.          * setShader(Shader shader);
         96.          * 设置图像效果，使用Shader可以绘制出各种渐变效果
         97.          *
         98.          * setShadowLayer(float radius ,float dx,float dy,int color);
         99.          * 在图形下面设置阴影层，产生阴影效果，radius为阴影的角度，dx和dy为阴影在x轴和y轴上的距离，color为阴影的颜色
         100.          *
         101.          * setStyle(Paint.Style style);
         102.          * 设置画笔的样式，为FILL，FILL_OR_STROKE，或STROKE
         103.          *
         104.          * setStrokeCap(Paint.Cap cap);
         105.          * 当画笔样式为STROKE或FILL_OR_STROKE时，设置笔刷的图形样式，如圆形样式
         106.          * Cap.ROUND,或方形样式Cap.SQUARE
         107.          *
         108.          * setSrokeJoin(Paint.Join join);
         109.          * 设置绘制时各图形的结合方式，如平滑效果等
         110.          *
         111.          * setStrokeWidth(float width);
         112.          * 当画笔样式为STROKE或FILL_OR_STROKE时，设置笔刷的粗细度
         113.          *
         114.          * setXfermode(Xfermode xfermode);
         115.          * 设置图形重叠时的处理方式，如合并，取交集或并集，经常用来制作橡皮的擦除效果
         116.          *
         117.          * 2.文本绘制
         118.          * setFakeBoldText(boolean fakeBoldText);
         119.          * 模拟实现粗体文字，设置在小字体上效果会非常差
         120.          *
         121.          * setSubpixelText(boolean subpixelText);
         122.          * 设置该项为true，将有助于文本在LCD屏幕上的显示效果
         123.          *
         124.        * setTextScaleX(float scaleX);
         125.         * 设置绘制文字x轴的缩放比例，可以实现文字的拉伸的效果
         126.          *
         127.          * setTextSkewX(float skewX);
         128.          * 设置斜体文字，skewX为倾斜弧度
         129.          *
         130.          * setTypeface(Typeface typeface);
         131.          * 设置Typeface对象，即字体风格，包括粗体，斜体以及衬线体，非衬线体等
         132.          *
         133.          * setUnderlineText(boolean underlineText);
         134.          * 设置带有下划线的文字效果
         135.          *
         136.          * setStrikeThruText(boolean strikeThruText);
         137.          * 设置带有删除线的效果
         138.          *
                  */
        mPath0 = new Path();//path路径对象
        mPath1 = new Path();
        createDrawable();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        //颜色矩阵（ColorMatrix）和坐标变换矩阵（Matrix），对图片进行变换，以拉伸， 扭曲等
        ColorMatrix cm = new ColorMatrix();
        //颜色矩阵，颜色矩阵式一个5x4的矩阵，可以用来方便的修改图片中RGBA各分量的值，
        //颜色矩阵以一维数组的方式存储如下
        float array[] = {0.55f, 0, 0, 0, 80.0f, 0, 0.55f,
                0, 0, 80.0f, 0, 0, 0.55f, 0, 80.0f, 0, 0, 0, 0.2f, 0};
        cm.set(array);
        //颜色滤镜，就像qq的在线和离线图片，同一张图片通过颜色滤镜处理，显示不同的效果，可减少图片资源
        mColorMatrixFilter = new ColorMatrixColorFilter(cm);
        mMatrix = new Matrix();
        mScroller = new Scroller(getContext());
        mTouch.x = 0.01f;//不让x，y为0.否则在点计算时会有问题
        mTouch.y = 0.01f;

    }

    /***
     * 计算拖曳点对应的拖曳角
     */
    public void calcCornerXY(float x, float y){
        //将手机屏幕分为四个象限，判断手指落在那个象限内
        if (x <= mWidth/2)
            mCornerX = 0;
        else
            mCornerX = mWidth;
        if (y <= mHeight/2)
            mCornerY = 0;
        else
            mCornerY = mHeight;

        if ((mCornerX == 0 && mCornerY == mHeight) ||(mCornerX == mWidth && mCornerY == 0))
            mIsRTandLB = true;
        else
            mIsRTandLB = false;
    }

    public boolean doTouchEvent(MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_MOVE){
            mTouch.x = event.getX();
            mTouch.y = event.getY();
            /** Android提供了Invalidate和postInvalidate方法实现界面刷新，但是Invalidate不能直接在线程中调用，因为他是违背了单线程模型：
              * Android UI操作并不是线程安全的，并且这些操作必须在UI线程中调用。
              * invalidate()的调用是把之前的旧的view从主UI线程队列中pop掉
            * 而postInvalidate()在工作者线程中被调用
            */
            this.postInvalidate();
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            mTouch.x = event.getX();
            mTouch.y = event.getY();
        }
        if (event.getAction() == MotionEvent.ACTION_UP){
            //是否触发翻页
            if (canDragOver()){
                startAnimation(1200);
            }else{
                mTouch.x = mCornerX - 0.09f;//如果不能翻页就让mTouch返回没有静止时的状态
                mTouch.y = mCornerY - 0.009f;//-0.09f防止mTouch = 800 或者mTouch = 0，在这些值是会出BUG
            }
            this.postInvalidate();
        }
        return true;
    }

    /**
     * 求解直线P1P2和直线P3P4的交点坐标
      */

    public PointF getCross(PointF P1, PointF P2, PointF P3, PointF P4){
        PointF CrossP = new PointF();
        float a1 = (P2.y - P1.y)/(P2.x - P1.x);
        float b1 = ((P1.x*P2.y) - (P2.x*P1.y))/(P1.x - P2.x);
        float a2 = (P4.y - P3.y)/(P4.x - P3.x);
        float b2 = ((P3.x*P4.y) - (P4.x*P3.y))/(P3.x - P4.x);
        CrossP.x = (b2 - b2)/(a1 - a2);
        CrossP.y = a1*CrossP.x + b1;
        return CrossP;
    }

    /////////////////////
    public void calcPoints(){
        mMiddleX = (mTouch.x + mCornerX)/2;
        mMiddleY = (mTouch.y + mMiddleY)/2;
        mBezierControl1.x = mMiddleX - (mCornerY - mMiddleY)
                *(mCornerY - mMiddleY)/(mCornerX - mMiddleX);
        mBezierControl1.y = mCornerY;
        mBezierControl2.x = mCornerX;
        mBezierControl2.y = mMiddleY - (mCornerX - mMiddleX)
                *(mCornerX - mMiddleX)/(mCornerY - mMiddleY);
        mBezierStart1.x = mBezierControl1.x - (mCornerX - mBezierControl1.x)/2;
        mBezierStart1.y = mCornerY;
        // 当mBezierStart1.x < 0或者mBezierStart1.x > 480时
        // 如果继续翻页，会出现BUG故在此限制
        if (mTouch.x > 0 && mTouch.x < mWidth){
            if (mBezierStart1.x < 0 || mBezierStart1.x > mWidth){
                if (mBezierStart1.x < 0)
                    mBezierStart1.x = mWidth - mBezierStart1.x;
                float f1 = Math.abs(mCornerX - mTouch.x);
                float f2 = mWidth*f1/mBezierStart1.x;
                mTouch.x = Math.abs(mCornerX - f2);
                float f3 = Math.abs(mCornerX - mTouch.x)
                        *Math.abs(mCornerY - mTouch.y)/f1;
                mTouch.y = Math.abs(mCornerY - f3);
                mMiddleX = (mTouch.x + mCornerX)/2;
                mMiddleY = (mTouch.y + mCornerY)/2;
                mBezierControl1.x = mMiddleX - (mCornerY - mMiddleY)
                        *(mCornerY - mMiddleY)/(mCornerX - mMiddleX);
                mBezierControl1.y = mCornerY;
                mBezierControl2.x = mCornerX;
                mBezierControl2.y = mMiddleY - (mCornerX - mMiddleX)
                        *(mCornerX - mMiddleX)/(mCornerY - mMiddleY);
                mBezierStart1.x = mBezierControl1.x
                        - (mCornerX - mBezierControl1.x)/2;
            }
        }
        mBezierStart2.x = mCornerX;
        mBezierStart2.y = mBezierControl2.y - (mCornerY - mBezierControl2.y)/2;
        mTouchToCornerDis = (float) Math.hypot((mTouch.x - mCornerX),
                (mTouch.y - mCornerY));
        mBezierEnd1 = getCross(mTouch, mBezierControl1,
                mBezierStart1, mBezierStart2);
        mBezierEnd2 = getCross(mTouch, mBezierControl2,
                mBezierStart1, mBezierStart2);

        /**
        270.          * mBeziervertex1.x 推导
        271.          * ((mBezierStart1.x+mBezierEnd1.x)/2+mBezierControl1.x)/2 
        272.          * (mBezierStart1.x+ 2*mBezierControl1.x+mBezierEnd1.x) / 4
         */
        mBeziervertex1.x = (mBezierStart1.x + 2*mBezierControl1.x + mBezierEnd1.x)/4;
        mBeziervertex1.y = (2*mBezierControl1.y + mBezierStart1.y + mBezierEnd1.y)/4;
        mBeziervertex2.x = (mBezierStart2.x + 2*mBezierControl2.x + mBezierEnd2.x)/4;
        mBeziervertex2.y = (2*mBezierControl2.y + mBezierStart2.y + mBezierEnd2.y)/4;


    }

    private  void drawCurrentPageArea(Canvas canvas , Bitmap bitmap, Path path){
        mPath0.reset();
        mPath0.moveTo(mBezierStart1.x, mBezierStart1.y);
        mPath0.quadTo(mBezierControl1.x, mBezierControl1.y,
                mBezierEnd1.x, mBezierEnd1.y);
        mPath0.lineTo(mTouch.x, mTouch.y);
        mPath0.lineTo(mBezierEnd2.x, mBezierEnd2.y);
        mPath0.quadTo(mBezierControl2.x, mBezierControl2.y,
                mBezierStart2.x, mBezierStart2.y);
        mPath0.lineTo(mCornerX, mCornerY);
        mPath0.close();
        canvas.save();
        canvas.clipPath(path, Region.Op.XOR);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.restore();
    }

    private void  drawNextPageAreaAndShadow(Canvas canvas , Bitmap bitmap){
        mPath1.reset();
        mPath1.moveTo(mBezierStart1.x, mBezierStart1.y);
        mPath1.lineTo(mBeziervertex1.x, mBeziervertex1.y);
        mPath1.lineTo(mBeziervertex2.x, mBeziervertex2.y);
        mPath1.lineTo(mBezierStart2.x, mBezierStart2.y);
        mPath1.lineTo(mCornerX, mCornerY);
        mPath1.close();
        mDegrees = (float)Math.toDegrees(Math.atan2(mBezierControl1.x
                -mCornerX, mBezierControl2.y - mCornerY));
        int leftx;
        int rightx;

        GradientDrawable mBackShadowDrawable;
        if (mIsRTandLB){
            leftx = (int)(mBezierStart1.x);
            rightx = (int)(mBezierStart1.x + mTouchToCornerDis/4);
            mBackShadowDrawable = mBackShadowDrawableLR;
        }else {
            leftx = (int)(mBezierStart1.x - mTouchToCornerDis/4);
            rightx = (int)mBezierStart1.x;
            mBackShadowDrawable = mBackShadowDrawableRL;
        }

        canvas.save();
        canvas.clipPath(mPath0);
        canvas.clipPath(mPath1, Region.Op.INTERSECT);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.rotate(mDegrees, mBezierStart1.x, mBezierStart1.y);
        mBackShadowDrawable.setBounds(leftx, (int) mBezierStart1.y, rightx,
                (int) (mMaxLength + mBezierStart1.y));
        mBackShadowDrawable.draw(canvas);
        canvas.restore();
    }

    public void setBitmaps(Bitmap bm1, Bitmap bm2){
        mCurPageBitmap = bm1;
        mNextPageBitmap = bm2;
    }

    public void setScreen(int w, int h){
        mWidth = w;
        mHeight = h;
    }

   // @Override
    protected void Fix_onDraw(Canvas canvas){
        canvas.drawColor(0xFFAAAAAA);
        calcPoints();
        drawCurrentPageArea(canvas, mCurPageBitmap, mPath0);
        drawNextPageAreaAndShadow(canvas, mNextPageBitmap);
        drawCurrentPageShadow(canvas);
        drawCurrentBackArea(canvas, mCurPageBitmap);
    }

    /**
     345.      * 创建阴影的GradientDrawable
         */
    private void createDrawable(){
        /**
349.          * GradientDrawable 支持使用渐变色来绘制图形，通常可以用作Button或是背景图形。
350.         * GradientDrawable允许指定绘制图形的种类：LINE，OVAL，RECTANGLE或是RING ，颜色渐变支持LINEAR_GRADIENT，RADIAL_GRADIENT 和 SWEEP_GRADIENT。
351.         * 其中在使用RECTANGLE（矩形），还允许设置矩形四个角为圆角，每个圆角的半径可以分别设置：
352.         * public void setCornerRadii(float[] radii)
353.         * radii 数组分别指定四个圆角的半径，每个角可以指定[X_Radius,Y_Radius]，四个圆角的顺序为左上，右上，右下，左下。如果X_Radius,Y_Radius为0表示还是直角。
354.         * 颜色渐变的方向由GradientDrawable.Orientation定义,共八种
355.         * GradientDrawable的构造函数：public GradientDrawable(GradientDrawable.Orientation orientation, int[] colors)
356.         * orientation指定了渐变的方向，渐变的颜色由colors数组指定，数组中的每个值为一个颜色。
357.         * 本例定义一个渐变方向从组左上到右下，渐变颜色为红，绿，蓝三色：
358.         * mDrawable = new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[] { 0xFFFF0000, 0xFF00FF00,0xFF0000FF });
359.         * 分别使用Liner,Radial 和Sweep三种渐变模式，并可配合指定矩形四个角圆角半径
        * */

        int[] color = {0x333333, 0x333333};
        //从右向左由颜色1渐变到2
        mFolderShadowDrawableRL = new GradientDrawable(
                GradientDrawable.Orientation.RIGHT_LEFT, color);
        mFolderShadowDrawableRL.setGradientType(
                GradientDrawable.LINEAR_GRADIENT);//线性渐变，radial 径向渐变，sweep 角度渐变
        mFolderShadowDrawableLR = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, color
        );
        mFolderShadowDrawableLR.setGradientType(
                GradientDrawable.LINEAR_GRADIENT
        );
        mBackShadowColors = new int[]{0xff111111, 0x111111};
        mBackShadowDrawableRL = new GradientDrawable(
                GradientDrawable.Orientation.RIGHT_LEFT, mBackShadowColors
        );
        mBackShadowDrawableRL.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        mBackShadowDrawableLR = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, mBackShadowColors);
        mBackShadowDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        mFolderShadowDrawableVLR = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, mFrontShadowColors
        );
        mFolderShadowDrawableVLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        mFolderShadowDrawableVRL = new GradientDrawable(
                GradientDrawable.Orientation.RIGHT_LEFT, mFrontShadowColors
        );
        mFolderShadowDrawableVRL.setGradientType(
                GradientDrawable.LINEAR_GRADIENT
        );
        mFolderShadowDrawableHTB = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, mFrontShadowColors
        );
        mFolderShadowDrawableHTB.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        mFolderShadowDrawableHBT = new GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP, mFrontShadowColors
        );
        mFolderShadowDrawableHBT.setGradientType(GradientDrawable.LINEAR_GRADIENT);

    }

     /**
398.          * GradientDrawable 支持使用渐变色来绘制图形，通常可以用作Button或是背景图形。
399.         * GradientDrawable允许指定绘制图形的种类：LINE，OVAL，RECTANGLE或是RING ，颜色渐变支持LINEAR_GRADIENT，RADIAL_GRADIENT 和 SWEEP_GRADIENT。
400.         * 其中在使用RECTANGLE（矩形），还允许设置矩形四个角为圆角，每个圆角的半径可以分别设置：
401.         * public void setCornerRadii(float[] radii)
402.         * radii 数组分别指定四个圆角的半径，每个角可以指定[X_Radius,Y_Radius]，四个圆角的顺序为左上，右上，右下，左下。如果X_Radius,Y_Radius为0表示还是直角。
403.         * 颜色渐变的方向由GradientDrawable.Orientation定义,共八种
404.         * GradientDrawable的构造函数：public GradientDrawable(GradientDrawable.Orientation orientation, int[] colors)
405.         * orientation指定了渐变的方向，渐变的颜色由colors数组指定，数组中的每个值为一个颜色。
406.         * 本例定义一个渐变方向从组左上到右下，渐变颜色为红，绿，蓝三色：
407.         * mDrawable = new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[] { 0xFFFF0000, 0xFF00FF00,0xFF0000FF });
408.         * 分别使用Liner,Radial 和Sweep三种渐变模式，并可配合指定矩形四个角圆角半径
409.         * */

    public void drawCurrentPageShadow(Canvas canvas){
        double degree;
        //计算两点间连线的倾斜角
        //还可旋转饼图
        if (mIsRTandLB){
            degree = Math.PI/4
                    - Math.atan2(mBezierControl1.y - mTouch.y,
                    mTouch.x - mBezierControl1.x);
        }else {
            degree = Math.PI/4
                    - Math.atan2(mTouch.y - mBezierControl1.y,
                    mTouch.x- - mBezierControl1.x);
        }

        //翻起页阴影顶点和touch点的距离
        double d1 = (float)25*1.414*Math.cos(degree);
        double d2 = (float)25*1.414*Math.sin(degree);
        float x = (float)(mTouch.x + d1);
        float y ;
        if (mIsRTandLB){
            y = (float)(mTouch.y + d2);
        }else {
            y = (float)(mTouch.y - d2);
        }

        mPath1.reset();
        mPath1.moveTo(x, y);
        mPath1.lineTo(mTouch.x, mTouch.y);
        mPath1.lineTo(mBezierControl1.x, mBezierControl1.y);
        mPath1.lineTo(mBezierStart1.x, mBezierStart1.y);
        mPath1.close();
        float rotateDegrees;
        canvas.save();
        canvas.clipPath(mPath0, Region.Op.XOR);
        canvas.clipPath(mPath1, Region.Op.INTERSECT);
        int leftx;
        int rightx;
        GradientDrawable mCurrentPageShadow;
        if (mIsRTandLB){
            leftx = (int)(mBezierControl1.x);
            rightx = (int)mBezierControl1.x + 25;
            mCurrentPageShadow = mFolderShadowDrawableVLR;
        }else {
            leftx = (int)(mBezierControl1.x - 25);
            rightx = (int)mBezierControl1.x + 1;
            mCurrentPageShadow = mFolderShadowDrawableVRL;
        }

        rotateDegrees = (float)Math.toDegrees(Math.atan2(mTouch.x
            - mBezierControl1.x, mBezierControl1.y - mTouch.y));
        canvas.rotate(rotateDegrees, mBezierControl1.x, mBezierControl1.y);
        mCurrentPageShadow.setBounds(leftx,
                (int) (mBezierControl1.y - mMaxLength), rightx,
                (int) (mBezierControl1.y));
        mCurrentPageShadow.draw(canvas);
        canvas.restore();
        mPath1.reset();
        mPath1.moveTo(x, y);
        mPath1.lineTo(mTouch.x, mTouch.y);
        mPath1.lineTo(mBezierControl2.x, mBezierControl2.y);
        mPath1.lineTo(mBezierStart2.x, mBezierStart2.y);
        mPath1.close();
        canvas.save();
        canvas.clipPath(mPath0, Region.Op.XOR);
        canvas.clipPath(mPath1, Region.Op.INTERSECT);
        if (mIsRTandLB){
            leftx = (int)(mBezierControl2.y);
            rightx = (int)(mBezierControl2.y + 25);
            mCurrentPageShadow = mFolderShadowDrawableHTB;
        }else {
            leftx = (int)(mBezierControl2.y - 25);
            rightx = (int)(mBezierControl2.y + 1);
            mCurrentPageShadow = mFolderShadowDrawableHBT;
        }

        rotateDegrees = (float)Math.toDegrees(Math.atan2(mBezierControl2.y
        - mTouch.y, mBezierControl2.x - mTouch.x));
        canvas.rotate(rotateDegrees, mBezierControl2.x, mBezierControl2.y);
        float temp;
        if (mBezierControl2.y < 0){
            temp = mBezierControl2.y - mHeight;
        }else {
            temp = mBezierControl2.y;
        }
        int hmg = (int)Math.hypot(mBezierControl2.x, temp);
        if (hmg > mMaxLength){
            mCurrentPageShadow.setBounds((int)(mBezierControl2.x - 25) - hmg, leftx,
                    (int)(mBezierControl2.x + mMaxLength) - hmg, rightx);
        }else {
            mCurrentPageShadow.setBounds((int)(mBezierControl2.x - mMaxLength), leftx,
                    (int)(mBezierControl2.x), rightx);
        }
        mCurrentPageShadow.draw(canvas);
        canvas.restore();
    }
    /**
 505.      * 绘制翻起页背面
      */
    private void drawCurrentBackArea(Canvas canvas, Bitmap bitmap){
        int i = (int)(mBezierStart1.x + mBezierControl1.x)/2;
        float f1 = Math.abs(i - mBezierControl1.x);
        int i1 = (int)(mBezierStart2.y - mBezierControl2.y)/2;
        float f2 = Math.abs(i1 - mBezierControl2.y);
        float f3 = Math.min(f1, f2);
        mPath1.reset();
        mPath1.moveTo(mBeziervertex2.x, mBeziervertex2.y);
        mPath1.lineTo(mBeziervertex1.x, mBeziervertex1.y);
        mPath1.lineTo(mBezierEnd1.x, mBezierEnd1.y);
        mPath1.lineTo(mTouch.x, mTouch.y);
        mPath1.lineTo(mBezierEnd2.x, mBezierEnd2.y);
        mPath1.close();
        GradientDrawable mFolderShadowDrawable;
        int left;
        int right;
        if (mIsRTandLB){
            left = (int)(mBezierStart1.x - 1);
            right = (int)(mBezierStart1.x + f3 + 1);
            mFolderShadowDrawable = mFolderShadowDrawableLR;
        }else {
            left = (int)(mBezierStart1.x - f3 - 1);
            right = (int)(mBezierStart1.x + 1);
            mFolderShadowDrawable = mFolderShadowDrawableRL;
        }

        canvas.save();
        canvas.clipPath(mPath0);
        canvas.clipPath(mPath1, Region.Op.INTERSECT);
        mPaint.setColorFilter(mColorMatrixFilter);
        float dis = (float)Math.hypot(mCornerX - mBezierControl1.x,
                mBezierControl2.y - mCornerY);
        float f8 = (mCornerX- mBezierControl1.x)/dis;
        float f9 = (mBezierControl2.y - mCornerY)/dis;
        mMatrixArray[0] = 1 - 2*f9*f9;
        mMatrixArray[1] = 2*f8*f9;
        mMatrixArray[3] = mMatrixArray[1];/////////////
        mMatrixArray[4] = 1 - 2*f8*f8;
        mMatrix.reset();
        mMatrix.setValues(mMatrixArray);
        mMatrix.preTranslate(-mBezierControl1.x, -mBezierControl1.y);
        mMatrix.postTranslate(mBezierControl1.x, mBezierControl1.y);
        canvas.drawBitmap(bitmap, mMatrix, mPaint);
        mPaint.setColorFilter(null);
        canvas.rotate(mDegrees, mBezierControl1.x, mBezierStart1.y);
        mFolderShadowDrawable.setBounds(left, (int)mBezierStart1.y, right,
                (int)(mBezierStart1.y + mMaxLength));
        mFolderShadowDrawable.draw(canvas);
        canvas.restore();
    }

    public void computeScroll(){
        super.computeScroll();
        if (mScroller.computeScrollOffset()){
            float x = mScroller.getCurrX();
            float y = mScroller.getCurrY();
            mTouch.x = x;
            mTouch.y = y;
            postInvalidate();
        }
    }

    private void startAnimation(int delayMillis){
        int dx,dy;
        if (mCornerX > 0){
            dx = -(int)(mWidth + mTouch.x);
        }else {
            dx = (int)(mWidth - mTouch.x + mWidth);
        }
        if (mCornerY > 0){
            dy = (int)(mHeight - mTouch.y);
        }else {
            dy = (int)(1 - mTouch.y);
        }
        mScroller.startScroll((int)mTouch.x, (int)mTouch.y, dx, dy, delayMillis);
    }

    public void abortAnimation(){
        if ((!mScroller.isFinished())){
            mScroller.abortAnimation();
        }
    }

    public boolean canDragOver(){
        if (mTouchToCornerDis > 1)
            return true;
        return false;
    }

    public boolean DragToRight(){
        if (mCornerX > 0)
            return false;
        return true;
    }
}
