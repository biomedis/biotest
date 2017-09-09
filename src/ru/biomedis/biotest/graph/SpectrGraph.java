package ru.biomedis.biotest.graph;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import ru.biomedis.biotest.R;
import ru.biomedis.biotest.RawDataProcessor;
import ru.biomedis.biotest.util.BaseCustomView;
import ru.biomedis.biotest.util.Log;

import java.util.ArrayList;

/**
  * Created by Anama on 30.09.2014.
 */
public  class SpectrGraph extends BaseCustomView
{



    /// Доп параметры графика ////
    int axismargin=(int)pxFromDp(30);// отступ осей от краев виджета
    int sizeFontLabel=(int)pxFromDp(15);
    int sizeFontAxisDigit=(int)pxFromDp(11);
    int colorBars= Color.GREEN;
    int axisColor=Color.WHITE;
    int axisLabelColor=Color.WHITE;
    int countLabelsX=10;
    int countLabelsY=5;

    String labelY="Интенсивность, 100/мс2";
    String labelX="Частота, Гц";

    private int colorVLF;
    private int colorLF;
    private int colorHF;
    ////

    private Point2D zeroPoint=new Point2D();//нулевая точка графика в координатах View
    private Point2D maxAxisX =new Point2D();//макс точка графика по оси X в координатах View
    private Point2D maxAxisY =new Point2D();//макс точка графика по оси Y в координатах View
    private float barWidth=0;//ширина бара(будет расчитываться ниже исходя из колличество данных)
    private RectF barRect=new RectF(0,0,getRealWidth(),getRealHeight()); // этот прямоугольник будет масшттабироваться под все бары
    private double maxData=0;
    private int barPadding=(int)pxFromDp(2);//отступы по бокам бара


    protected Resources resources;



    private RawDataProcessor.Spectrum spectrum;





    public SpectrGraph(Context context) { this(context,null);}
    public SpectrGraph(Context context, AttributeSet attrs) {this(context, attrs, 0); }
    public SpectrGraph(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);



        this.resources = context.getResources();
        colorHF =  this.resources.getColor(R.color.hf);
        colorVLF =  this.resources.getColor(R.color.vlf);
        colorLF =  this.resources.getColor(R.color.lf);
    }

    public void setData(RawDataProcessor.Spectrum d)
    {
        this.spectrum =d;

    }

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
        paint.setStrokeWidth(1);


        //Расчет параметров координатных систем //
        this.zeroPoint.setX(axismargin);
        this.zeroPoint.setY(getRealHeight() - axismargin);

        this.maxAxisX.setX(getRealWidth() - axismargin);
        this.maxAxisX.setY(this.zeroPoint.getY());

        this.maxAxisY.setX(axismargin);
        this.maxAxisY.setY(axismargin);


        ///////////////
        canvas.drawLine(this.zeroPoint.getX(),this.zeroPoint.getY(), this.maxAxisY.getX(),this.maxAxisY.getY(),paint);
        canvas.drawLine(this.zeroPoint.getX(),this.zeroPoint.getY(), this.maxAxisX.getX(),this.maxAxisX.getY(),paint);

        this.maxData=0;

        int count=0;
        for (double data:  this.spectrum.getSpector()) {
            count++;
          if(count==1)continue;
           if(data > this.maxData)
           {

               this.maxData=data;
           }

        }


    }

    //TODO нужно разобраться с подписями. Можно с учетом шрифта вычислять сколько влезет надписей и штрихов. Иначе при малом размере графика у нас нифига не влезет.
    /**
     * Рисует доп линии и пометки осей. Тут реально бы алаптировать колличество пометок по размеру области отрисовки. Можно потом. Пока вручную мы проставляем
     * @param canvas
     */
    private void drawLabels(Canvas canvas)
    {
        Paint paint=new Paint();
        paint.setColor(this.axisLabelColor);
        paint.setTextSize(this.sizeFontAxisDigit);







/*
        ////// ось х ////


        int max_diap=0;
        for (RawDataProcessor.Point<Integer> data:  this.histogramm.getHistogramm()) {

            if(data.getX()>max_diap)max_diap=data.getX();
        }

        float deltaX=   (float)(this.maxAxisX.getX()-this.zeroPoint.getX())/(histogramm.getEndDiapHistogramm()-histogramm.getStartDiapHistogramm());//гистрограмма идет по х не от 0. Это нужно учесть.

        paint.setTextAlign(Paint.Align.CENTER);
int j=0;
        for(int i= histogramm.getStartDiapHistogramm();i<=histogramm.getEndDiapHistogramm();i+=histogramm.getStepHistogramm())
        {

            canvas.drawText(i+"",this.zeroPoint.getX()+j*deltaX,zeroPoint.getY()+this.sizeFontAxisDigit+3,paint);
            canvas.drawLine(this.zeroPoint.getX()+j*deltaX,zeroPoint.getY()+3,this.zeroPoint.getX()+j*deltaX,zeroPoint.getY(),paint);
            j+=histogramm.getStepHistogramm();
        }

        ////////
 */
        ////Ось Y ///

        float deltaY=   (float)((this.zeroPoint.getY()-this.maxAxisY.getY())/this.maxData);
        int delta=(int)Math.ceil(this.maxData/countLabelsY);
        int ss=(int)Math.floor(this.maxData);
        paint.setTextAlign(Paint.Align.LEFT);
       double maxDataT=maxData;
        maxData/=100;

        int digitYPadding=0;
        if(maxData<100)digitYPadding=2*this.sizeFontAxisDigit+1;
        else if(maxData<1000)digitYPadding=2*this.sizeFontAxisDigit+1;
        else if(maxData<10000)digitYPadding=3*this.sizeFontAxisDigit+1;
        maxData=maxDataT;

        for(int i=0;i<=ss;i+=delta)
        {
            canvas.drawText((int)Math.floor((float)(i/100.0))+"",this.zeroPoint.getX()-digitYPadding,zeroPoint.getY()-i*deltaY+this.sizeFontAxisDigit/2,paint);
            canvas.drawLine(this.zeroPoint.getX()-pxFromDp(3),zeroPoint.getY()-i*deltaY,this.zeroPoint.getX(),zeroPoint.getY()-i*deltaY,paint);

        }
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(this.sizeFontLabel);
        canvas.drawText(this.labelX,(this.maxAxisX.getX()-this.zeroPoint.getX())/2,maxAxisX.getY()+this.sizeFontAxisDigit+sizeFontLabel+(int)pxFromDp(2),paint);


       //canvas.save();
        paint.setTextAlign(Paint.Align.LEFT);
       // canvas.rotate(270);
        canvas.drawText(this.labelY,this.zeroPoint.getX(),this.maxAxisY.getY()-pxFromDp(5),paint);
       // canvas.restore();


        ////////////

    }
    private void drawData(Canvas canvas)
    {

        if(this.spectrum.getSpector().size()!=0)
        {
            Paint paint=new Paint();

            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);


           int vSize= this.spectrum.getSpector().size();


            float h = (this.maxAxisX.getX() - this.zeroPoint.getX()) / vSize;
            float v=(float)((zeroPoint.getY()-maxAxisY.getY())/this.maxData);



            Path p1=new Path();

            p1.moveTo(zeroPoint.getX()+1,this.zeroPoint.getY());
            //нижняя часть спектра
            float lastVal=0;
            float lastY=0;
            float lastX=0;
            int ii=0;
            for (RawDataProcessor.Point<Double> point : this.spectrum.getSpectorVLF())
            {

                lastVal=(float)(point.getY()*v);
                lastY=this.zeroPoint.getY()-lastVal  ;
                lastX=this.zeroPoint.getX()+(float)(point.getX()*h);


               if(ii==0) p1.lineTo(lastX+pxFromDp(1),lastY);
                else p1.lineTo(lastX,lastY);
                ii++;
            }
            p1.rLineTo(0,lastVal);
            p1.close();


            paint.setColor(colorVLF);
            canvas.drawPath(p1,paint);

            paint.setTextSize(this.sizeFontAxisDigit);
            paint.setColor(Color.GRAY);
            canvas.drawLine(lastX,this.zeroPoint.getY(),lastX,this.zeroPoint.getY()+(int)pxFromDp(3),paint);
            paint.setColor(Color.WHITE);
            canvas.drawText(this.spectrum.getStartLF()+"", lastX, zeroPoint.getY()+this.sizeFontAxisDigit+(int)pxFromDp(3),paint);

            Path p2=new Path();

            p2.moveTo(lastX,this.zeroPoint.getY());
            //нижняя часть спектра
            for (RawDataProcessor.Point<Double> point : this.spectrum.getSpectorLF())
            {
                lastVal=(float)(point.getY()*v);
                lastY=this.zeroPoint.getY()-lastVal  ;
                lastX=this.zeroPoint.getX()+(float)(point.getX()*h);
                p2.lineTo(lastX,lastY);

            }
            p2.rLineTo(0, lastVal);
            p2.close();
            paint.setColor(colorLF);
            canvas.drawPath(p2,paint);

            paint.setColor(Color.GRAY);
            canvas.drawLine(lastX,this.zeroPoint.getY(),lastX,this.zeroPoint.getY()+(int)pxFromDp(3),paint);
            paint.setColor(Color.WHITE);
            canvas.drawText(this.spectrum.getStartHF()+"", lastX, zeroPoint.getY()+this.sizeFontAxisDigit+(int)pxFromDp(3),paint);

            Path p3=new Path();

            p3.moveTo(lastX,this.zeroPoint.getY());
            //нижняя часть спектра
            for (RawDataProcessor.Point<Double> point : this.spectrum.getSpectorHF())
            {
                lastVal=(float)(point.getY()*v);
                lastY=this.zeroPoint.getY()-lastVal  ;
                lastX=this.zeroPoint.getX()+(float)(point.getX()*h);
                p3.lineTo(lastX,lastY);

            }
            p3.rLineTo(0,lastVal);
            p3.close();
            paint.setColor(colorHF);
            canvas.drawPath(p3,paint);

            paint.setColor(Color.GRAY);
            canvas.drawLine(lastX,this.zeroPoint.getY(),lastX,this.zeroPoint.getY()+(int)pxFromDp(3),paint);
            paint.setColor(Color.WHITE);
            canvas.drawText(this.spectrum.getEndHF()+"", lastX, zeroPoint.getY()+this.sizeFontAxisDigit+(int)pxFromDp(3),paint);

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
