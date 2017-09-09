package ru.biomedis.biotest.graph.SectorGraphImpl;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import ru.biomedis.biotest.graph.*;
/**
 * реализация рисования секторов с о строковыми подписямим
 * Created by Anama on 14.11.2014.
 */
public class SimpleSectorGraph extends SectorGraph<Double,String>
{
 

    public SimpleSectorGraph(Context context) {
        super(context);
    }

    public SimpleSectorGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleSectorGraph(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }






    @Override
    protected void drawExtra(Canvas canvas, RectF circleRect, float x0, float y0, double total) {

        //здесь можно сделать подписи
        Paint p=new Paint();
        p.setColor(Color.WHITE);
        p.setTextSize((float)(0.27*circleRect.width()/2));//размер шрифта от процент от радиуса круга
        p.setTextAlign(Paint.Align.CENTER);


        double v=0;

        float s0x =circleRect.right- x0;// приводив центр СО в начало круга
        float s0y =circleRect.bottom/2 -y0;
        float x;
        float y;

        //мы поворачиваем точку начала отсчета угра сектора она на 3 часа, на угол старт+дельта/2 и находим координаты. Зная их и координаты центра мы легко все посчитем


        for (SectorDataItem<Double, String> itm : getData()) {
             v = Math.toRadians(itm.getStartAngle()+itm.getAngleDelta() / 2);
             x= (float)((s0x* Math.cos(v) - s0y * Math.sin(v))*0.65)+x0;// повернем точку и потом преобразуем координату в СО канвы, еще и сдвигаем к центру круга
             y= (float)((s0x* Math.sin(v)+ s0y * Math.cos(v))*0.65)+y0;
            canvas.drawText(itm.getExtra(),x,y,p);
        }



    }


}
