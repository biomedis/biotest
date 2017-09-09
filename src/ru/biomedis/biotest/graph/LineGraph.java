package ru.biomedis.biotest.graph;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import ru.biomedis.biotest.R;
import ru.biomedis.biotest.util.BaseCustomView;
import ru.biomedis.biotest.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by Anama on 30.09.2014.
 */
public class LineGraph extends BaseCustomView
{


    public interface ActionListener
    {
        public void onReady();
    }

    public void setActionListener(ActionListener a)
    {
        actionListener=a;
    }

private boolean isReady=false;

    public boolean isReady(){return isReady;}

private ActionListener actionListener;
    private String xmlns="http://schemas.android.com/apk/res/android";

/////Параметры из XML
    private String titleGraph;
    private float paddingY;
    private float   paddingX;
    private int sizeFontLabelsAxis;
    private int colorLine;
    private int colorAxis;
    private int colorLabelAxisX;
    private int  colorLabelAxisY;
    private int  colorBackground;
    private int  colorBackground2;
    private int  colorTitle;

    private int zeroLineOfGraph;//нулевая линия графика по Y. Величина. Координата будет рассчитана в инициализации
    private int minY;//макс значение по Y
    private int maxY;// макс значение по Х


    ///////





//координаты опорных точек осей на канве
    private Point2D zeroLineOfGraphPoint;//нулевая линия графика по Y. Величина. Координата будет рассчитана в инициализации
    private Point2D maxYPoint;// макс значение по Х
    private Point2D zeroPoint;//нулевая точка
    private Point2D maxXPoint;//крайняя точка
    private float diapX;// Размах по х в системе осей для расчетов преобразования в СО
    private float diapY;//Размах по y в системе осей


    private float[] renderedArray=null;//массив для рендеринга
    private int maxCountPointForDraw;//сколько точек мы отрисовываем на графике
    private int arrayPositon=0;//текущая позиция вставки в массив отрисовки - это последнее вставленное значение(его позиция)
    private int numDrawingElements=10;//сколько элементов накопить перед вызовом отрисовки
    private int drawCounter=0;//счетчик для numDrawingElements



    private float hiPorogFloat;
    private Point2D hiPorog;// линия уровня

    private boolean isInitLine=true;//если true то нужно переинициализировать линию порога

private int drawCount=0;






    //Ресурсы для отрисовки, инициализируются в initDraw
    Paint axisPaint;
    Paint labelPaintX;
    Paint labelPaintY;
    Paint linePaint;
    Paint backgroundPaint;
    boolean isInited=false;
    Paint porogLine;

    /**
     * Значение передается ввиде числа 0.0 - 1.0 это процентная часть от общего размаха графика(255)
     * @param hiPorogFloat
     */
    public void setHiPorogFloat(float hiPorogFloat)
    {
        if( this.hiPorogFloat == hiPorogFloat) return;
        this.hiPorogFloat = hiPorogFloat;
        isInitLine=true;
    }


     private void initLine(float  hiPorog)
    {
       if(this.hiPorog==null) this.hiPorog = new Point2D();


        this.hiPorog.setX(zeroPoint.getX());
        this.hiPorog.setY((float)(this.zeroPoint.getY()-this.diapY/2 - hiPorog*this.diapY*0.7));//мы рисуем от середины по верт this.diapY/2 и и учтем высоту линии порога над серединой 0.8 остальное типа ниже (тк у нас размах а нам линию надо рисовать)


        isInitLine=false;
    }

    public LineGraph(Context context) { this(context,null);}
    public LineGraph(Context context, AttributeSet attrs) {this(context, attrs, 0); }

    public LineGraph(Context context, AttributeSet attrs, int defStyle)
    {

        super(context, attrs, defStyle);


        this.setMinimumHeight(100);
        this.setMinimumWidth(100);


        //Получаем удобно стандартные атрибуты
        TypedArray typedArray2 = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.height,android.R.attr.width,android.R.attr.background});
       // Log.v("i="+dpFromPx(typedArray2.getDimensionPixelSize(0,0)));
        //Log.v(typedArray2.getIndexCount()+"IC1");
        //
        //нестандартные атрибуты
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LineGraph);
       // int indexCount = typedArray.getIndexCount();
       // Log.v(indexCount+"IC2");
       // Log.v(typedArray.getString(R.styleable.LineGraph_titleGraph)+"=текст");
        //

       //читает все атрибуты в сыром виде
      //  int attributeCount = attrs.getAttributeCount();
       // for(int i=0;i<attributeCount;i++) Log.v(attrs.getAttributeName(i)+"="+ attrs.getAttributeValue(i)+"\n");
        //
        /////////////////////////////////////////////////////////////////////////

        this.colorBackground2=typedArray2.getColor(2,Color.BLACK);
        Log.v("color="+typedArray2.length());

        this.titleGraph=typedArray.getString(R.styleable.LineGraph_titleGraph);
        this.paddingY=typedArray.getDimension(R.styleable.LineGraph_paddingX, 0);
        this.paddingX=typedArray.getDimension(R.styleable.LineGraph_paddingY, 0);
        this.sizeFontLabelsAxis= (int) typedArray.getDimension(R.styleable.LineGraph_sizeFontLabelsAxis, 10);
        this.colorLine=typedArray.getColor(R.styleable.LineGraph_colorLine, Color.GREEN);
        this.colorAxis=typedArray.getColor(R.styleable.LineGraph_colorAxis, Color.WHITE);
        this.colorLabelAxisX=typedArray.getColor(R.styleable.LineGraph_colorLine, Color.WHITE);
        this.colorLabelAxisY=typedArray.getColor(R.styleable.LineGraph_colorLine, Color.WHITE);
        this.colorBackground=typedArray.getColor(R.styleable.LineGraph_ColorBackground, Color.BLACK);
        this.colorTitle=typedArray.getColor(R.styleable.LineGraph_ColorTitle, Color.BLACK);

        this.zeroLineOfGraph=typedArray.getInteger(R.styleable.LineGraph_zeroLineY,0);
        this.minY=typedArray.getInteger(R.styleable.LineGraph_minY,0);
        this.maxY=typedArray.getInteger(R.styleable.LineGraph_maxY,255);

        //this.setMaxCountPointForDraw(R.integer.drawWindow);//немного говнокода )). Компонент завязан на приложение! Что упрощает )
        //this.numDrawingElements=R.integer.numDrawingElements;//число накапливаемых элементов до отрисовки


    }


    /**
     * Устанавливливает сколько отсчетов мы будем отрисовывать. Это нужно для циклической обработки.
     * Данные в массив отрисовки записываются циклически, дойдя до конца массив очищается и начинается снова. Это как буфер экрана.
     * @param val
     */
    synchronized public void setMaxCountPointForDraw(int val)
    {
      this.maxCountPointForDraw=val;
        if(this.renderedArray==null)
        {
            this.renderedArray=new float[(val)*4];//у нас пары значений, каждая следующая точка связывается с предыдущей. на одну точку у нас4 элемента нужно. -1 тк первая точка не отрисовывается как линия
           Arrays.fill(this.renderedArray,0);
            this.arrayPositon=0;
        }else {Arrays.fill(this.renderedArray,0); this.arrayPositon=0;}


        maxPos = (this.maxCountPointForDraw)*4-1;
    }

    public void setNumDrawingElements(int val)
    {
        this.numDrawingElements=val;
    }

private  float x=0;
    private   float y=0;
    private int maxPos = 0;

    /**
     * Добавляет значение к массиву отрисовки. Если массив заполнен, он очистится и начнется сначала.
     * @param val
     */
    synchronized public void addDrawPoint(short val)
    {


       //...some code






        val=(short)(this.maxY-val);//тк ось y канвы вниз идет.



        if(this.arrayPositon == maxPos ){/*Arrays.fill(this.renderedArray,0); */this.arrayPositon=0;}//если мы уже записывали последний элемент, то очистим массив, начнем с нуля

        //полученное значение нужно преобразовать в нашу систему координат осей и записать в массив.
        //Вставка пар значений x,y для canvas.drawLines(float[],Paint)
        //сдесь можно набирать немного значений(счетчик), потом делать команду this.invalidate()
        // при использовании счетчика незабыть о том что нужно проверять что до конца массива больше чем макс значение счетчика. Иначе произойдет обнуление.
        // нужно сразу выдавать
        if(this.arrayPositon==0)
        {
           // this.renderedArray[0] =(((float)this.arrayPositon/2)/this.maxCountPointForDraw)*this.diapX;
            this.renderedArray[0] =((float)this.arrayPositon/maxPos)*this.diapX+this.zeroPoint.getX();//просто сохраним пропорции
            this.renderedArray[++this.arrayPositon] =((float)val/this.maxY)*this.diapY+this.maxYPoint.getY();
            return;//мы пишем одну точку и выпрыгиваем, ждем следющей
        }
        //далее код для последующих точек (тех что не первые те не с 0 индексом)
        this.arrayPositon++;

        //x = this.renderedArray[this.arrayPositon] =(((float)this.arrayPositon/2)/this.maxCountPointForDraw)*this.diapX;

        x = this.renderedArray[this.arrayPositon] = ((float)this.arrayPositon/maxPos)*this.diapX+this.zeroPoint.getX();
        this.arrayPositon++;
        y=this.renderedArray[this.arrayPositon] =((float)val/this.maxY)*this.diapY+this.maxYPoint.getY();

        drawCount=this.arrayPositon;

        if(this.arrayPositon == maxPos)

        {
            this.arrayPositon=0;
            this.invalidate();

            drawCounter=0;

            return;
        }


// по нормальному у нас на макс позиции будет последняя точка, и добавлять склейку ненадо
        if(this.arrayPositon<maxPos)//не надо вставлять точки сверх массива
        {
            //запишем точку для склейки. Те мы точку пишем дважды.
            this.arrayPositon++;
            this.renderedArray[this.arrayPositon] = x;
            this.arrayPositon++;
            this.renderedArray[this.arrayPositon] = y;
        }
        //Log.v("ARR="+this.renderedArray[this.arrayPositon]+" Val="+val);

        //если макс позиция, то нарисуем, и уйдем в новое заполнение


        if(drawCounter>=numDrawingElements)  {this.invalidate();drawCounter=0;}//чтобы не перерисовывать слишком часто.
         else drawCounter++;




    }






    /**
     * Инициализация переменных для отрисовки.
     */
    private void initDraw()
    {
         axisPaint=new Paint();

        axisPaint.setColor(this.colorAxis);
        axisPaint.setStrokeWidth(pxFromDp(1));
        axisPaint.setStyle(Paint.Style.STROKE);

         labelPaintX=new Paint();
         labelPaintX.setColor(this.colorLabelAxisX);


        labelPaintY=new Paint();
        labelPaintY.setColor(this.colorLabelAxisY);


         linePaint=new Paint();
         linePaint.setColor(this.colorLine);
         linePaint.setStrokeWidth(pxFromDp(2));
         linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
         linePaint.setAntiAlias(true);

         backgroundPaint=new Paint();
        // backgroundPaint.setColor(this.colorBackground);
         backgroundPaint.setStrokeWidth(pxFromDp(2));
         backgroundPaint.setStyle(Paint.Style.FILL);

        porogLine=new Paint();
        porogLine.setColor(Color.GREEN);
        porogLine.setAlpha(128);
        porogLine.setStrokeWidth(pxFromDp(2));
        porogLine.setStyle(Paint.Style.FILL_AND_STROKE);


        //расчет координат на канве точек привязки.
    this.zeroPoint=new Point2D(this.paddingX+this.sizeFontLabelsAxis+pxFromDp(5),this.getRealHeight()-this.paddingY-this.sizeFontLabelsAxis-pxFromDp(5));
    this.maxXPoint=new Point2D(getRealWidth()-this.paddingX,this.getRealHeight()-this.paddingY-this.sizeFontLabelsAxis-pxFromDp(5));// макс значение по Х
    this.maxYPoint=new Point2D(this.paddingX+this.sizeFontLabelsAxis+pxFromDp(5),this.paddingY);

    this.zeroLineOfGraphPoint=new Point2D(this.zeroPoint.getX(),this.zeroPoint.getY()-(this.zeroPoint.getY()-this.maxYPoint.getY())/2);//нулевая линия графика по Y. Величина. Координата будет рассчитана в инициализации

    this.diapX= this.maxXPoint.getX()-this.zeroPoint.getX();
    this.diapY= this.zeroPoint.getY()-this.maxYPoint.getY();

       // Log.v("diapX="+diapX+" width="+this.getRealWidth());
       // Log.v("diapY="+diapY+" height="+this.getRealHeight());

        //


        isReady=true;
if(actionListener!=null)        actionListener.onReady();


    }
    private void drawAxis(Canvas canvas)
    {
        //учитывается отступ, размер шрифта.
        canvas.drawLine(this.zeroPoint.getX(),this.zeroPoint.getY(),this.maxXPoint.getX(),this.maxXPoint.getY(),this.axisPaint);
        canvas.drawLine(this.zeroPoint.getX(),this.zeroPoint.getY(),this.maxYPoint.getX(),this.maxYPoint.getY(),this.axisPaint);

        //canvas.drawLine(hiPorog.getX(),hiPorog.getY(),hiPorog2.getX(),hiPorog2.getY(),this.axisPaint);
        canvas.drawLine(hiPorog.getX(),hiPorog.getY(),this.maxXPoint.getX(),hiPorog.getY(),this.porogLine);

        //фон внутри .

        //canvas.



    }

private void drawDataLine(Canvas canvas)
{

   // Log.v("drawDataLine this.arrayPositon="+this.arrayPositon);
    if(this.arrayPositon < 4)return;//не отрисовывать если число элементов меньше 4, те у нас 1 точка еще
    this.renderedArray[0]+=pxFromDp(1);//чтобы график не заходил на ось
   // canvas.drawLine(this.zeroLineOfGraphPoint.x,this.zeroLineOfGraphPoint.y,this.maxXPoint.x,this.zeroLineOfGraphPoint.y,this.linePaint);
    canvas.drawLines(this.renderedArray,0,drawCount+1,linePaint);//отрисовываем от нуля до текущей позиции, просто нереальная херня, почему +1 ??????????
    this.renderedArray[0]-=pxFromDp(1);
}




    private void drawLineporog(Canvas canvas)
    {

      // canvas.drawLine(hiPorog.getX(),hiPorog.getY(),hiPorog2.getX(),hiPorog2.getY(),this.axisPaint);
      //  canvas.drawLine(this.zeroPoint.getX(),this.zeroPoint.getY()-20,this.maxXPoint.getX(),this.maxXPoint.getY()-20,this.axisPaint);
    }


    @Override
    public void onDraw(Canvas canvas)
    {

        if(!isInited) {initDraw();isInited=true;}
        if(isInitLine)initLine(hiPorogFloat);

        canvas.drawColor(this.colorBackground);
        drawAxis(canvas);
      // drawLineporog( canvas);
        drawDataLine(canvas);


    }

}
