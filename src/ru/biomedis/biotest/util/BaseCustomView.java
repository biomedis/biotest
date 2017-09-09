package ru.biomedis.biotest.util;


import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import ru.biomedis.biotest.fragments.dialogs.TextViewDialog;

/**
 * Базовый класс для всех кастомных view, Нужно не забывать что размеры все внутри в PX те в сырых пикселах
 */
public abstract class BaseCustomView extends View
{

    private float desuredHeight;
    private float desuredWidth;//желаемые значения из XML(исп стандартные значения width и height) в псырых пикселях
    private float realWidth;//реальноые значения. Те что у нас получились в результате компоновке. В сырых пикселах
    private float realHeight;
    private String description;
    private String titleDescription;
    private String extraDescription;
    private Context ctx;
    private View thisViev;

    public String getTitleDescription()
    {
        return titleDescription;
    }

    public void setTitleDescription(String titleDescription)
    {
        this.titleDescription = titleDescription;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getExtraDescription()
    {
        return extraDescription;
    }

    public void setExtraDescription(String extraDescription)
    {
        this.extraDescription = extraDescription;
    }

    public class Point2D
    {

        private float x;
        private float y;


        public Point2D(){x=0;y=0;}
        public Point2D(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }


    }





private View.OnLongClickListener onclick=new OnLongClickListener()
{
    @Override
    public boolean onLongClick(View v)
    {
        Log.v("ID_CLICKED = "+v.getId());
        if(!titleDescription.isEmpty())
        {
            TextViewDialog dlg = TextViewDialog.newInstance(titleDescription, (extraDescription==null?"":extraDescription)+ description);

            if(ctx instanceof Activity) dlg.show(((Activity)ctx).getFragmentManager(), "TEXT_VIEW");

        }
        return false;
    }
};


    public BaseCustomView(Context context) { this(context,null);}
    public BaseCustomView(Context context, AttributeSet attrs) {this(context, attrs, 0); }

    /**
     * Если нужно получить особые параметры из XML то в наследние их можно получить в конструкторе наследованного от этого
     * @param context
     * @param attrs
     * @param defStyle
     */
    public BaseCustomView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
        this.description="";
        this.titleDescription="";
        TypedArray typedArray2 = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.height,android.R.attr.width});


        this.desuredHeight=typedArray2.getDimensionPixelSize(0, 0);
        this.desuredWidth=typedArray2.getDimensionPixelSize(1, 0);



        typedArray2.recycle();
        ctx=context;
        thisViev=this;

        //this.setOnClickListener(onclick);

            this.setOnLongClickListener(onclick);
    }




    /**
     * Желаемый размер из параметра ХML android:height в сырых пикселах
     * @return
     */
    public float getDesuredHeight()
    {
        return desuredHeight;
    }
    /**
     * Желаемый размер из параметра ХML android:width в сырых пикселах
     * @return
     */
    public float getDesuredWidth()
    {
        return desuredWidth;
    }

    /**
     * Реальные значения размера в сырых пикселах
     * @return
     */
    public float getRealHeight()
    {
        return realHeight;
    }
    /**
     * Реальные значения размера в сырых пикселах
     * @return
     */
    public float getRealWidth()
    {
        return realWidth;
    }

    /**
     * Размер экрана в пикселах в текущей ориентации
     * @return
     */
    public int getScreenWidth()
    {
       return getResources().getDisplayMetrics().widthPixels;
    }
    /**
     * Размер экрана в пикселах в текущей ориентации
     * @return
     */
    public int getScreenHeight()
    {
        return getResources().getDisplayMetrics().heightPixels;
    }


    @Override
    public void onMeasure(int wSpec, int hSpec)
    {

        // super.onMeasure(wSpec,hSpec);


        int modeW = MeasureSpec.getMode(wSpec);
        int modeH = MeasureSpec.getMode(hSpec);

        int sizeW = MeasureSpec.getSize(wSpec);
        int sizeH = MeasureSpec.getSize(hSpec);//это все в пикселях сырых



     //   Log.v("density="+getResources().getDisplayMetrics().density);
     //   Log.v("widthScreen="+getResources().getDisplayMetrics().widthPixels);
      //  Log.v("heightScreen="+getResources().getDisplayMetrics().heightPixels);

      //  Log.v("desireW="+desuredWidth);
      //  Log.v("desireH="+desuredHeight);

      //  Log.v("sizeW=" + sizeW);
      //  Log.v("sizeH="+sizeH);

        int h=0;
        int w=0;
        switch (modeW)
        {
            case MeasureSpec.AT_MOST://заданы границы размера

                if(this.desuredWidth!=0) w=(int) Math.min(this.desuredWidth, sizeW);
                else w=sizeW;
             //   Log.v("MESURE MODE MeasureSpec.AT_MOST\n");
                break;

            case MeasureSpec.EXACTLY://точно уже определен размер

                w=sizeW;
            //   Log.v("MESURE MODE MeasureSpec.EXACTLY:\n");
                break;

            case MeasureSpec.UNSPECIFIED://беру сколько хочу
                if(this.desuredWidth!=0) w=(int)this.desuredWidth;
                else  w=sizeW;

              //  Log.v("MESURE MODE MeasureSpec.UNSPECIFIED\n");
                break;

        }

        switch (modeH)
        {
            case MeasureSpec.AT_MOST://заданы границы размера, мы берем сколько желаем но не более заданного.
                if(this.desuredHeight!=0 )  w=(int) Math.min(this.desuredHeight, sizeH);
                else    h=sizeH;

               // Log.v("MESURE MODE MeasureSpec.AT_MOST\n");
                break;

            case MeasureSpec.EXACTLY://точно уже определен размер
                h=sizeH;

               //Log.v("MESURE MODE MeasureSpec.EXACTLY:\n");
                break;

            case MeasureSpec.UNSPECIFIED://беру сколько хочу

                if(this.desuredHeight!=0 ) h=(int)this.desuredHeight;
                else     h=sizeH;
              //  Log.v("MESURE MODE MeasureSpec.UNSPECIFIED\n");
                break;

        }
        setMeasuredDimension( w, h);



    }
    @Override
    public void  onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout( changed,  left,  top,  right,  bottom);


        realWidth =this.getMeasuredWidth();
        realHeight =this.getMeasuredHeight();

        //Log.v("realWidth=" + realWidth + "\n");
       // Log.v("realHeight="+ realHeight +"\n");

    }
    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH)
    {
        super.onSizeChanged(w, h, oldW, oldH);

    }

    /**
     * Преобразует значение из PX в DP согласно коэфициента масштабирования экрана
     * @param px
     * @return
     */
    public float dpFromPx(float px)
    {
        return px / this.getContext().getResources().getDisplayMetrics().density;
    }
    /**
     * Преобразует значение из DP в PX согласно коэфициента масштабирования экрана
     * @param dp
     * @return
     */
    public float pxFromDp(float dp)
    {
        return dp * this.getContext().getResources().getDisplayMetrics().density;
    }


public void redraw()
{
    ((Activity)getContext()).runOnUiThread(  new Runnable(){
                @Override
                public void run() {invalidate();}
            });
}

    /**
     * This method returns a bitmap related to resource id. It is ready to use method, you can
     * use it by simply copying in your project.
     *
     * @param context Context of calling activity
     * @param drawableId Resource ID of bitmap drawable
     * @return Bitmap whose resource id was passed to method.
     */
    public static Bitmap getBitmapFromDrawableId(Context context,int drawableId){
        Bitmap bitmap = null;
        try {
            BitmapDrawable drawable = (BitmapDrawable)context.getResources().getDrawable(drawableId);
            bitmap = drawable.getBitmap();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
    /**
     * This method returns a bitmap related to drawable. It is ready to use method, you can
     * use it by simply copying in your project.
     *
     * @param drawable Drawable resource of image
     * @return Bitmap whose resource id was passed to method.
     */
    public static Bitmap getBitmapFromDrawable(Drawable drawable){
        Bitmap bitmap = null;
        try {
            BitmapDrawable bitmapDrawable = (BitmapDrawable)drawable;
            bitmap = bitmapDrawable.getBitmap();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }




}

