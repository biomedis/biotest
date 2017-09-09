package ru.biomedis.biotest.graph;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import ru.biomedis.biotest.R;
import ru.biomedis.biotest.util.BaseCustomView;
import ru.biomedis.biotest.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
  * Created by Anama on 30.09.2014.
 */
public  class ArrayGraph extends BaseCustomView
{




    /// Доп параметры графика ////
    int axismargin=(int)pxFromDp(30);// отступ осей от краев виджета
    int sizeFontLabel=(int)pxFromDp(15);
    int sizeFontAxisDigit=(int)pxFromDp(12);
    int colorBars= Color.GREEN;
    int axisColor=Color.WHITE;
    int axisLabelColor=Color.WHITE;
    int countLabelsX=10;
    int countLabelsY=8;

    String labelX="";
    String labelY="";

    ////

    private Point2D zeroPoint=new Point2D();//нулевая точка графика в координатах View
    private Point2D maxAxisX =new Point2D();//макс точка графика по оси X в координатах View
    private Point2D maxAxisY =new Point2D();//макс точка графика по оси Y в координатах View
    private float barWidth=0;//ширина бара(будет расчитываться ниже исходя из колличество данных)
    private RectF barRect=new RectF(0,0,getRealWidth(),getRealHeight()); // этот прямоугольник будет масшттабироваться под все бары
    private double maxData=0;


    protected Resources resources;






    private List<Double> dataArray=new ArrayList<Double>();//данные




    public ArrayGraph(Context context) { this(context,null);}
    public ArrayGraph(Context context, AttributeSet attrs) {this(context, attrs, 0); }
    public ArrayGraph(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        this.resources = context.getResources();
        colorBars =  this.resources.getColor(R.color.hf);

    }

    public void addData(Double item){ this.dataArray.add(item); }
    public void addAllData(List<Double> d){ this.dataArray=d; }
    public void clearData(){this.dataArray.clear(); }

    public void setLabelX(String labelX)
    {
        this.labelX = labelX;
    }

    public void setLabelY(String labelY)
    {
        this.labelY = labelY;
    }

    private void drawAxis(Canvas canvas)
    {

        Paint paint=new Paint();
        paint.setColor(this.axisColor);
        paint.setStrokeWidth((int)pxFromDp(1));

        this.zeroPoint.setX(axismargin);
        this.zeroPoint.setY(getRealHeight() - axismargin);

        this.maxAxisX.setX(getRealWidth() - axismargin);
        this.maxAxisX.setY(this.zeroPoint.getY());

        this.maxAxisY.setX(axismargin);
        this.maxAxisY.setY(axismargin);

        this.barWidth=(this.maxAxisX.getX()-this.zeroPoint.getX())/this.dataArray.size();
        this.barRect.set(this.zeroPoint.getX(),this.maxAxisY.getY(),this.zeroPoint.getX()+ this.barWidth,this.maxAxisY.getY());


        canvas.drawLine(this.zeroPoint.getX(),this.zeroPoint.getY(), this.maxAxisY.getX(),this.maxAxisY.getY(),paint);
        canvas.drawLine(this.zeroPoint.getX(),this.zeroPoint.getY(), this.maxAxisX.getX(),this.maxAxisX.getY(),paint);

        this.maxData=0;
        for (Double data : this.dataArray) {

           if(data>this.maxData)this.maxData=data;
        }


    }

    /**
     * Рисует доп линии и пометки осей. Тут реально бы алаптировать колличество пометок по размеру области отрисовки. Можно потом. Пока вручную мы проставляем
     * @param canvas
     */
    private void drawLabels(Canvas canvas)
    {
        Paint paint=new Paint();
        paint.setColor(this.axisLabelColor);
        paint.setTextSize(this.sizeFontAxisDigit);


        ////// ось х ////
        float deltaX=   (float)(this.maxAxisX.getX()-this.zeroPoint.getX())/dataArray.size();
        int delta=(int)Math.ceil(dataArray.size()/countLabelsX);

        paint.setTextAlign(Paint.Align.CENTER);
        for(int i=0;i<=dataArray.size();i+=delta)
        {
            canvas.drawText(i+"",this.zeroPoint.getX()+i*deltaX,zeroPoint.getY()+this.sizeFontAxisDigit+(int)pxFromDp(3),paint);
            canvas.drawLine(this.zeroPoint.getX()+i*deltaX,zeroPoint.getY()+(int)pxFromDp(3),this.zeroPoint.getX()+i*deltaX,zeroPoint.getY(),paint);
        }

        ////////

        ////Ось Y ///

        float deltaY=   (float)((this.zeroPoint.getY()-this.maxAxisY.getY())/this.maxData);
        delta=(int)Math.ceil(this.maxData/countLabelsY);
        int ss=(int)Math.floor(this.maxData);
        paint.setTextAlign(Paint.Align.LEFT);

        int digitYPadding=0;
        if(maxData<100)digitYPadding=2*this.sizeFontAxisDigit+1;
        else if(maxData<1000)digitYPadding=2*this.sizeFontAxisDigit+1;
        else if(maxData<10000)digitYPadding=3*this.sizeFontAxisDigit+1;

        for(int i=0;i<=ss;i+=delta)
        {
            canvas.drawText(i+"",this.zeroPoint.getX()-digitYPadding,zeroPoint.getY()-i*deltaY+this.sizeFontAxisDigit/2,paint);
            canvas.drawLine(this.zeroPoint.getX()-(int)pxFromDp(3),zeroPoint.getY()-i*deltaY,this.zeroPoint.getX(),zeroPoint.getY()-i*deltaY,paint);

        }




        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(this.sizeFontLabel);
        canvas.drawText(this.labelX,(this.maxAxisX.getX()-this.zeroPoint.getX())/2,maxAxisX.getY()+this.sizeFontAxisDigit+sizeFontLabel+(int)pxFromDp(2),paint);


        //canvas.save();
        paint.setTextAlign(Paint.Align.LEFT);

       // canvas.rotate(270);
        canvas.drawText(this.labelY,this.zeroPoint.getX(),this.maxAxisY.getY()-(int)pxFromDp(5),paint);
       // canvas.restore();


        ////////////

    }
    private void drawData(Canvas canvas)
    {

        if(this.dataArray.size()!=0)
        {
            Paint paint=new Paint();
            paint.setColor(this.colorBars);
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);

            RectF tRect;
            int count=0;

            for (Double data : this.dataArray) {

                tRect=  getBarToDraw(data);
                if(count==0)tRect.offset(count*this.barWidth+pxFromDp(1),0);
                else tRect.offset(count*this.barWidth,0);
                canvas.drawRect(tRect,paint);
                count++;
            }






        }
    }

    /**
     * Выдает размер бара для рисования. Его нужно еще сдвинуть по оси Х
     * @param data
     * @return
     */
    private RectF getBarToDraw(double data)
    {

        float sizeBar=(float)((data/this.maxData)*(this.zeroPoint.getY()-this.maxAxisY.getY()));


        this.barRect.set(this.zeroPoint.getX(),this.zeroPoint.getY()-sizeBar,this.zeroPoint.getX()+ this.barWidth,this.maxAxisX.getY());
    return  this.barRect;
    }


    @Override
    public  void onDraw(Canvas canvas)
    {

        super.onDraw(canvas);

        this.drawAxis(canvas);
        this.drawLabels(canvas);
        this.drawData(canvas);



    }










}
