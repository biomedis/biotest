package ru.biomedis.biotest.graph;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import ru.biomedis.biotest.util.BaseCustomView;

import java.util.ArrayList;
import java.util.List;

/**
 * Строит статический график заданной серии.
 * Created by Anama on 13.12.2014.
 */
public abstract class LineGraphSingleSeries<T extends Number> extends BaseCustomView
{
    public LineGraphSingleSeries(Context context)
    {
        super(context);
        this.resources = context.getResources();
    }

    public LineGraphSingleSeries(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.resources = context.getResources();
    }

    public LineGraphSingleSeries(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        this.resources = context.getResources();
    }





    private List<T> data=null;

    private List<Level<T,String>> levelList=new ArrayList<Level<T, String>>();
    protected Resources resources;

    /// Доп параметры графика ////
    private int axismargin=(int)pxFromDp(50);// отступ осей от краев виджета
    private  int sizeFontLabel=(int)pxFromDp(12);
    private  int sizeFontAxisDigit=(int)pxFromDp(11);
    private  int colorLine= Color.WHITE;
    private int axisColor=Color.WHITE;
    private  int axisLabelColor=Color.WHITE;
    private  int countLabelsX=10;
    private  int countLabelsY=8;
    private String labelX="";
    private String labelY="";

    ////

    private Point2D zeroPoint=new Point2D();//нулевая точка графика в координатах View
    private Point2D maxAxisX =new Point2D();//макс точка графика по оси X в координатах View
    private Point2D maxAxisY =new Point2D();//макс точка графика по оси Y в координатах View
    private double maxData;


    public List<T> getData()
    {
        return data;
    }

    public void setData(List<T> data)
    {
        this.data = data;
    }

    /**
     * Подпись оси Х
     * @return
     */
    public String getLabelX()
    {
        return labelX;
    }

    public void setLabelX(String labelX)
    {
        this.labelX = labelX;
    }
    /**
     * Подпись оси Y
     * @return
     */
    public String getLabelY()
    {
        return labelY;
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

        for (T t : getData()) {


            if(t.doubleValue() > this.maxData)
            {

                this.maxData=t.doubleValue();
            }
        }





    }


    private void drawLables(Canvas canvas)
    {

        Paint paint=new Paint();
        paint.setColor(this.axisLabelColor);
        paint.setTextSize(this.sizeFontAxisDigit);
/*

        ////// ось х ////
        float deltaX=   (float)(this.maxAxisX.getX()-this.zeroPoint.getX())/data.size();
        int delta=(int)Math.ceil(data.size()/countLabelsX);

        paint.setTextAlign(Paint.Align.CENTER);
        for(int i=0;i<=data.size();i+=delta)
        {
            canvas.drawText(i+"",this.zeroPoint.getX()+i*deltaX,zeroPoint.getY()+this.sizeFontAxisDigit+3,paint);
            canvas.drawLine(this.zeroPoint.getX()+i*deltaX,zeroPoint.getY()+3,this.zeroPoint.getX()+i*deltaX,zeroPoint.getY(),paint);
        }

        ////////
*/
        ////Ось Y ///
        int digitYPadding=0;
        if(maxData<100)digitYPadding=2*this.sizeFontAxisDigit;
        else if(maxData<1000)digitYPadding=2*this.sizeFontAxisDigit;
        else if(maxData<10000)digitYPadding=3*this.sizeFontAxisDigit;

        float deltaY=   (float)((this.zeroPoint.getY()-this.maxAxisY.getY())/this.maxData);
        int delta=(int)Math.ceil(this.maxData/countLabelsY);
        int ss=(int)Math.floor(this.maxData);
        paint.setTextAlign(Paint.Align.LEFT);
        for(int i=0;i<=ss;i+=delta)
        {
            canvas.drawText(i+"",this.zeroPoint.getX()-digitYPadding,zeroPoint.getY()-i*deltaY+this.sizeFontAxisDigit/2,paint);
            canvas.drawLine(this.zeroPoint.getX()-pxFromDp(3),zeroPoint.getY()-i*deltaY,this.zeroPoint.getX(),zeroPoint.getY()-i*deltaY,paint);

        }

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(this.sizeFontAxisDigit+pxFromDp(2));
        canvas.drawText(this.labelX,(this.maxAxisX.getX()-this.zeroPoint.getX())/2,maxAxisX.getY()+this.sizeFontAxisDigit+pxFromDp(13),paint);


        //canvas.save();
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(sizeFontLabel);
        // canvas.rotate(270);
        canvas.drawText(this.labelY,this.zeroPoint.getX(),this.maxAxisY.getY()-pxFromDp(5),paint);
        // canvas.restore();






    }

    private void drawLevels(Canvas canvas)
    {

        if(levelList.size()!=0)
        {
            Paint p=new Paint();

            RectF rect=new RectF();
            Level<T, String> prevLevel=null;
            double hCoeff = (this.maxAxisX.getX() - this.zeroPoint.getX()) / data.size();// коэфициенты пропорциональности для данных
            double vCoeff = (this.zeroPoint.getY() - this.maxAxisY.getY()) / this.maxData;

            double top=0;// уровень который мы рисуем, если у нас level > MaxData то мы top=maxData(чтоб за график не уйти)

            boolean stopItterate=false;
            for (Level<T, String> level : levelList)
            {
                p.setColor(level.getColor());
                p.setAlpha(70);
                // уровень указывает на вершину уровня.
                prevLevel = getPrevLevel(level);



                if(prevLevel==null)//те у нас первый уровень, нарисуем его снизу
                {
                    top=level.getLevel().doubleValue();
                    rect.set(zeroPoint.getX()+pxFromDp(1),zeroPoint.getY()-(float)(top*vCoeff),maxAxisX.getX(),zeroPoint.getY());
                }
                else
                {
                    if(level.getLevel().doubleValue()>maxData) {top=maxData; stopItterate=true;}
                    else  top=level.getLevel().doubleValue();

                    rect.set(zeroPoint.getX() + pxFromDp(1), zeroPoint.getY() - (float) (top * vCoeff), maxAxisX.getX(), zeroPoint.getY() - (float) (prevLevel.getLevel().doubleValue()*vCoeff));//рисуем от предыдущего
                }

                canvas.drawRect(rect,p);
                if(stopItterate) break;//закончим итерации если нужно
            }


        }
    }
    private void drawData(Canvas canvas)
    {
        //нужно посчитать коэффициент для преобразования в СО по Х и по Y
        Paint drawPaint=new Paint();
        drawPaint.setAntiAlias(true);
        drawPaint.setColor(colorLine);
        drawPaint.setStrokeWidth(pxFromDp(2));
        float [] arr=new float[(data.size()-1)*4];//колличество лилий промежутков(колличество точек -1) на 4 элемента для их отображения

        double hCoeff = (this.maxAxisX.getX() - this.zeroPoint.getX()-pxFromDp(1)) / (data.size()-1);// коэфициенты пропорциональности для данных
        double vCoeff = (this.zeroPoint.getY() - this.maxAxisY.getY()) / this.maxData;

        int i=0;
//для последнего элемента у нас расчет на предыдущей итерации
        for (int k=0;k<data.size()-1;k++) {

            arr[i++]=zeroPoint.getX()+(float)(hCoeff*k);
            arr[i++]=zeroPoint.getY()-(float)(data.get(k).doubleValue()*vCoeff);

            arr[i++]=zeroPoint.getX()+(float)(hCoeff*(k+1));
            arr[i++]=zeroPoint.getY()-(float)(data.get((k+1)).doubleValue()*vCoeff);


        }


        canvas.drawLines(arr,drawPaint);

    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);


        if(this.data==null ) return;

        this.drawAxis(canvas);

        this.drawLables(canvas);
        this.drawLevels(canvas);
        this.drawData(canvas);


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
    /**
     * Вычисляет Level в который попадает данные. У нас уровень от 0 до следующего итд, те данные меньше уровня. Те уровень это от предыдущего или нуля к указанному уровню, а не выше его.
     * @param data
     * @return
     */
    private Level<T, String> getLevelByData(T data)
    {
        //если значение меньше значения уровня то мы сразу выскакиваем, те это наш искомый уровень.
        int d= data.intValue();

        for (Level<T, String> itm : levelList) {

            if(d<itm.getLevel().intValue()) return itm;
        }
        return null;
    }

    /**
     * Добавляет уровень данных , для цветового отображения диапазонов.Порядок добавления важен, он должен быть по возрастанию значений.
     * Максимальное значение данных задает последний добавленный уровень. Те мы смотрим что ниже уровня!
     * @param level уровень, верхняя границв
     * @param extra доп информация
     * @param color ресурс цвета
     */
    public void addLevel(T level,String extra,int color)
    {
        this.levelList.add(new Level<T, String>(level, extra, color));

        //находим максимальное значение данных как самый верхний уровень.

        if(level.intValue()>=maxData) maxData=level.doubleValue();

        redraw();
    }
    public void removeLevel(T level){this.levelList.remove(level);redraw(); }
    public List<Level<T,String>> getLevels(){return this.levelList;}


    public Level<T,String> getPrevLevel(Level<T,String>  level)
    {
        int i = this.levelList.indexOf(level);
        if(i==-1 )return null;
        if(i==0) return null;
        return this.levelList.get(i-1);

    }

    public Level<T,String> getNextLevel(Level<T,String>  level)
    {
        int i = this.levelList.indexOf(level);

        if(i==-1 )return null;
        if(i==this.levelList.size()-1) return null;
        return this.levelList.get(i+1);

    }
}
