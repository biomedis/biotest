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
import ru.biomedis.biotest.R;
import ru.biomedis.biotest.RawDataProcessor;
import ru.biomedis.biotest.util.BaseCustomView;
import ru.biomedis.biotest.util.Log;

/**
  * Created by Anama on 30.09.2014.
 */
public  class HistogrammGraph extends BaseCustomView
{





    /// Доп параметры графика ////
    int axismargin=(int)pxFromDp(30);// отступ осей от краев виджета
    int sizeFontLabel=(int)pxFromDp(15);
    int sizeFontAxisDigit=(int)pxFromDp(11);
    int colorBars= Color.GREEN;
    int axisColor=Color.WHITE;
    int axisLabelColor=Color.WHITE;
    int countLabelsX=10;
    int countLabelsY=8;

    String labelX="R-R интервалы, x10мс";
    String labelY="Частота появления";

    ////

    private Point2D zeroPoint=new Point2D();//нулевая точка графика в координатах View
    private Point2D maxAxisX =new Point2D();//макс точка графика по оси X в координатах View
    private Point2D maxAxisY =new Point2D();//макс точка графика по оси Y в координатах View
    private float barWidth=0;//ширина бара(будет расчитываться ниже исходя из колличество данных)
    private RectF barRect=new RectF(0,0,getRealWidth(),getRealHeight()); // этот прямоугольник будет масшттабироваться под все бары
    private double maxData=0;
    private int barPadding=(int)pxFromDp(2);//отступы по бокам бара


    protected Resources resources;




    private RawDataProcessor.Histogramm histogramm;



    public HistogrammGraph(Context context) { this(context,null);}
    public HistogrammGraph(Context context, AttributeSet attrs) {this(context, attrs, 0); }
    public HistogrammGraph(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);



        this.resources = context.getResources();
        colorBars =  this.resources.getColor(R.color.hf);
    }

    public void setData(RawDataProcessor.Histogramm d)
    {
        this.histogramm =d;

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

        this.barWidth=(this.maxAxisX.getX()-this.zeroPoint.getX())/( (histogramm.getEndDiapHistogramm()-histogramm.getStartDiapHistogramm())/histogramm.getStepHistogramm() );
        this.barRect.set(this.zeroPoint.getX(),this.maxAxisY.getY(),this.zeroPoint.getX()+ this.barWidth,this.maxAxisY.getY());
        this.barWidth-=this.barPadding*2;// сделаем отсупы
        ///////////////
        canvas.drawLine(this.zeroPoint.getX(),this.zeroPoint.getY(), this.maxAxisY.getX(),this.maxAxisY.getY(),paint);
        canvas.drawLine(this.zeroPoint.getX(),this.zeroPoint.getY(), this.maxAxisX.getX(),this.maxAxisX.getY(),paint);

        this.maxData=0;
       // this.maxData= this.histogramm.getAmo();

        for (RawDataProcessor.Point<Integer> data:  this.histogramm.getHistogramm()) {

           if(data.getY()>this.maxData)this.maxData=data.getY();
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

            canvas.drawText(i+"",this.zeroPoint.getX()+j*deltaX,zeroPoint.getY()+this.sizeFontAxisDigit+(int)pxFromDp(3),paint);
            canvas.drawLine(this.zeroPoint.getX()+j*deltaX,zeroPoint.getY()+(int)pxFromDp(3),this.zeroPoint.getX()+j*deltaX,zeroPoint.getY(),paint);
            j+=histogramm.getStepHistogramm();
        }

        ////////

        ////Ось Y ///

        float deltaY=   (float)((this.zeroPoint.getY()-this.maxAxisY.getY())/this.maxData);
        int delta=(int)Math.ceil(this.maxData/countLabelsY);
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
        canvas.drawText(this.labelY,this.zeroPoint.getX()+pxFromDp(4),this.maxAxisY.getY()-(int)pxFromDp(9),paint);
       // canvas.restore();


        ////////////

    }
    private void drawData(Canvas canvas)
    {

        if(this.histogramm.getHistogramm().size()!=0)
        {
            Paint paint=new Paint();
            paint.setColor(this.colorBars);
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);

            RectF tRect;
            int count=0;
            boolean isFirstNotNul=false;
            boolean trigger=false;

            for (RawDataProcessor.Point<Integer> data : this.histogramm.getHistogramm())
            {
                if(data.getY()==0){ count++;continue;}

                tRect=  getBarToDraw(data.getY());
                tRect.offset(count*(this.barWidth+this.barPadding*2)+this.barPadding,0);
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
