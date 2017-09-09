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
import ru.biomedis.biotest.util.BaseCustomView;
import ru.biomedis.biotest.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * В реализации класса нужно реализовать свой метод доп рисования drawExtra()
 * @param <T> Тип данных
 * @param <F> Тип доп информации
 * Created by Anama on 30.09.2014.
 */
public abstract class SingleBarGraph<T extends Number,F> extends BaseCustomView
{


    private List<Level<T,F>> levelList=new ArrayList<Level<T, F>>();
    private int backgroundColor;//цвет фона\


    protected Resources resources;
    private T data;// данные для отображения
    private T maxData;// максимальное значение для бара, расчитывается исходя из максимального уровня





    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }






    private class BarLevelComparator implements Comparator<Level>
    {

        @Override
        public int compare(Level lhs, Level rhs) {
            if(lhs.getLevel().intValue()==rhs.getLevel().intValue())return 0;
            if(lhs.getLevel().intValue()>rhs.getLevel().intValue()) return 1;
            else return -1;

        }


    }

    /**
     * Вычисляет Level в который попадает данные. У нас уровень от 0 до следующего итд, те данные меньше уровня. Те уровень это от предыдущего или нуля к указанному уровню, а не выше его.
     * @param data
     * @return
     */
    private Level<T, F> getLevelByData(T data)
    {
        //если значение меньше значения уровня то мы сразу выскакиваем, те это наш искомый уровень.
       int d= data.intValue();

        for (Level<T, F> itm : levelList) {

            if(d<itm.getLevel().intValue()) return itm;
        }
    return null;
    }



    /**
     * Уровни отображения
     * @param <T> Тип данных
     * @param <F> Тип доп информации
     */
    protected class Level<T extends Number,F>
    {
       private T level;
       private F extra;
       private int color;




        Level(T level) {
           this(level,null,0);
        }



        /**
         *
         * @param level уровень
         * @param extra дополнение
         * @param color ресурс цвета типа R.color
         */
        Level(T level, F extra, int color) {
            this.level = level;
            this.extra = extra;
            this.color=resources.getColor(color);
        }



        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public T getLevel() {
            return level;
        }



        public F getExtra() {
            return extra;
        }

        public void setExtra(F extra) {
            this.extra = extra;
        }


    }



    public SingleBarGraph(Context context) { this(context,null);}
    public SingleBarGraph(Context context, AttributeSet attrs) {this(context, attrs, 0); }
    public SingleBarGraph(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);

        TypedArray typedArray2 = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.background});



       this.backgroundColor= typedArray2.getColor(0, Color.BLACK);

        this.resources = context.getResources();



    }

    /**
     * Добавляет уровень данных , для цветового отображения диапазонов.Порядок добавления важен, он должен быть по возрастанию значений.
     * Максимальное значение данных задает последний добавленный уровень. Те мы смотрим что ниже уровня!
     * @param level уровень, верхняя границв
     * @param extra доп информация
     * @param color ресурс цвета
     */
    public void addLevel(T level,F extra,int color)
    {
        this.levelList.add(new Level<T, F>(level, extra, color));

        //находим максимальное значение данных как самый верхний уровень.
        if(maxData==null)maxData=level;
        if(level.intValue()>=maxData.intValue())maxData=level;

        redraw();
    }
    public void removeLevel(T level){this.levelList.remove(level);redraw(); }
    public List<Level<T,F>> getLevels(){return this.levelList;}

    /**
     * Позволяет нарисовать что-то дополнительное на графике.
     * @param canvas канва
     * @param rect прямоугольник отображения
     * @param data данные графика текущие
     */
    protected abstract void drawExtra(Canvas canvas,RectF rect,T data );



    @Override
    public  void onDraw(Canvas canvas)
    {

      //   if(levelList.size()==0)return;


        Paint paint=new Paint();
        paint.setStyle(Paint.Style.FILL);

        paint.setAntiAlias(true);

        double v = (getRealHeight() / maxData.doubleValue()) * data.doubleValue();



        RectF rect=new RectF(0,(float)(getRealHeight()-v),getRealWidth(),getRealHeight());// прямоугольник отображения

        Level<T, F> levelByData = this.getLevelByData(data);
        if(levelByData!=null)paint.setColor(levelByData.getColor());
        else {paint.setColor(Color.BLACK); Log.v("Данные не попали ни в один уровень, бар выведен черным!!");}


        canvas.drawRect(rect,paint);



        this.drawExtra(canvas,rect,data);


    }






}
