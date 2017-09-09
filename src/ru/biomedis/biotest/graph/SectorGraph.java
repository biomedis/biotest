package ru.biomedis.biotest.graph;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import ru.biomedis.biotest.R;
import ru.biomedis.biotest.util.BaseCustomView;
import ru.biomedis.biotest.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * В реализации класса нужно реализовать свой метод доп рисования drawExtra()
 * @param <T> Тип данных
 * @param <F> Тип доп информации
 * Created by Anama on 30.09.2014.
 */
public abstract class SectorGraph<T extends Number,F> extends BaseCustomView
{


    private List<SectorDataItem<T,F>> dataList=new ArrayList<SectorDataItem<T, F>>();
    private double total=0;
    protected Resources resources;




    /**
     * Данные диаграммы.
     * @param <T> Тип данных
     * @param <F> Тип доп информации
     */
    protected class SectorDataItem<T extends Number,F>
    {
       private T data;
       private F extra;
       private int color;
       private double angleDelta;
       private  double startAngle;

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        SectorDataItem(T data) {
           this(data,null,0);
        }

        public double getAngleDelta() {
            return angleDelta;
        }

        public double getStartAngle() {
            return startAngle;
        }

        public void setAngleDelta(double angleDelta) {
            this.angleDelta = angleDelta;
        }

        public void setStartAngle(double startAngle) {
            this.startAngle = startAngle;
        }

        /**
         *
         * @param data Данные
         * @param extra дополнение
         * @param color ресурс цвета типа R.color
         */
        SectorDataItem(T data, F extra,int color) {
            this.data = data;
            this.extra = extra;
            this.color=resources.getColor(color);
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public F getExtra() {
            return extra;
        }

        public void setExtra(F extra) {
            this.extra = extra;
        }
    }



    public SectorGraph(Context context) { this(context,null);}
    public SectorGraph(Context context, AttributeSet attrs) {this(context, attrs, 0); }
    public SectorGraph(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);



        this.resources = context.getResources();

    }

    public void addDataItem(T item,F extra,int color){ this.dataList.add(new SectorDataItem<T, F>(item,extra,color));redraw();  }
    public void removeDataItem(T item){this.dataList.remove(item);redraw(); }
    public List<SectorDataItem<T,F>> getData(){return this.dataList;}

    /**
     * Позволяет нарисовать что-то дополнительное на графике.
     * @param canvas канва
     * @param circleRect квадрат в который вписан круг диаграммы
     * @param x0 точка центра круга
     * @param y0 точка центра круга
     * @param total сумма данных для расчетов
     */
    protected abstract void drawExtra(Canvas canvas,RectF circleRect,float x0,float y0,double total );

    private double getAnglePie(SectorDataItem<T, F> itm)
    {
        return ((itm.getData().doubleValue()/total)*360.0);
    }

    @Override
    public  void onDraw(Canvas canvas)
    {

        if(dataList.size()==0)return;

        float X0=getRealWidth()/2;
        float Y0=getRealHeight()/2;
        double angle=0;
        Paint paint=new Paint();
        paint.setStyle(Paint.Style.FILL);

        paint.setAntiAlias(true);


        RectF rect=new RectF(0,0,getRealWidth(),getRealHeight());// прямоугольник для круга

        total=0;
        for (SectorDataItem<T, F> itm : dataList) {
            total+=itm.getData().doubleValue();
        }

        double delta=0;
        for (SectorDataItem<T, F> itm : dataList)
        {
            delta= Math.round(getAnglePie(itm)) ;

            itm.setAngleDelta(delta);
            itm.setStartAngle(angle);

            paint.setColor(itm.getColor());
            canvas.drawArc(rect,(float)angle,360-(float)(angle),true,paint);// 360-(float)(delta) чтобы сгладить ошибки округления!! Иначе будут дырочки
            angle+=delta;
        }



        this.drawExtra(canvas,rect,X0,Y0,total);


    }






}
