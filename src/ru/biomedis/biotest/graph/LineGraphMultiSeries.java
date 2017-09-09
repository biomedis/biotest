package ru.biomedis.biotest.graph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import ru.biomedis.biotest.util.BaseCustomView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Строит статический график нескольких серий
 * Created by Anama on 13.12.2014.
 */
public abstract class LineGraphMultiSeries<T extends Number> extends BaseCustomView
{
    public LineGraphMultiSeries(Context context)
    {
        super(context);
    }

    public LineGraphMultiSeries(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public LineGraphMultiSeries(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    private Map<String,MultiDatasItem<T>> datas=new HashMap<String, MultiDatasItem<T>>();


    /**
     * Устанавливает данные
     * @param name
     * @param color цвет (не ресурс)
     * @param data
     * @throws Exception
     */
    public void setDatas(String name,int color,List<T> data) throws Exception
    {

        if(datas.containsKey(name)) throw new Exception("Набор данных с таким именем уже существует");

        datas.put(name,new MultiDatasItem<T>(color,data,name));
    }



    /**
     * Класс серии данных для отображения
     * Он автоматически получает максимум в массиве данных
     * @param <T>
     */
    public class MultiDatasItem< T extends Number>
    {
        private String name;
        private List<T> data;
        private int color;
        private double maxData;

        public MultiDatasItem(int color, List<T> data, String name)
        {
            this.color = color;
            this.data = data;
            this.name = name;
            this.calcMaxData();
        }

        public int getColor()
        {
            return color;
        }

        public void setColor(int color)
        {
            this.color = color;
        }

        public List<T> getData()
        {
            return data;
        }

        public void setData(List<T> data)
        {
            this.data = data;
            this.calcMaxData();
        }

        public double getMaxData()
        {
            return maxData;
        }



        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }


        private void calcMaxData()
        {
            this.maxData=0;

            for (T t : getData()) {


                if(t.doubleValue() > this.maxData)
                {

                    this.maxData=t.doubleValue();
                }
            }
        }

    }

    /// Доп параметры графика ////
    private int axismargin=(int)pxFromDp(50);// отступ осей от краев виджета
    private  int sizeFontLabel=(int)pxFromDp(12);
    private  int sizeFontAxisDigit=(int)pxFromDp(11);
    private  int colorLine= Color.GREEN;
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


    private double maxData;// максимум среди всех сери данных
    private int maxSizeData;//макс размер серии данных

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




        this.maxData=0;// найдем максимум среди всех серий данных

        for (Map.Entry<String, MultiDatasItem<T>> entry : datas.entrySet())
        {
            if(entry.getValue().getMaxData() > this.maxData)this.maxData=entry.getValue().getMaxData();
        }

        this.maxSizeData=0;//максимальный размер массивов данных

        for (Map.Entry<String, MultiDatasItem<T>> entry : datas.entrySet())
        {
            if(entry.getValue().getData().size() > this.maxData)this.maxData=entry.getValue().getData().size();
        }









    }

    public void drawLables(Canvas canvas)
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
    public void drawData(Canvas canvas)
    {

        for (Map.Entry<String, MultiDatasItem<T>> entry : this.datas.entrySet()) drawDataItem(canvas,entry.getKey());

    }

    private void drawDataItem(Canvas canvas,String name)
    {

        List<T> ts = this.datas.get(name).getData();

        //нужно посчитать коэффициент для преобразования в СО по Х и по Y
        Paint drawPaint=new Paint();
        drawPaint.setAntiAlias(true);
        drawPaint.setColor(this.datas.get(name).getColor());

        float [] arr=new float[(ts.size()-1)*4];//колличество лилий промежутков(колличество точек -1) на 4 элемента для их отображения

        double hCoeff = (this.maxAxisX.getX() - this.zeroPoint.getX()-pxFromDp(1)) / (ts.size()-1);// коэфициенты пропорциональности для данных
        double vCoeff = (this.zeroPoint.getY() - this.maxAxisY.getY()) / this.maxData;

        int i=0;
//для последнего элемента у нас расчет на предыдущей итерации
        for (int k=0;k<ts.size()-1;k++) {

            arr[i++]=zeroPoint.getX()+(float)(hCoeff*k);
            arr[i++]=zeroPoint.getY()-(float)(ts.get(k).doubleValue()*vCoeff);

            arr[i++]=zeroPoint.getX()+(float)(hCoeff*(k+1));
            arr[i++]=zeroPoint.getY()-(float)(ts.get((k+1)).doubleValue()*vCoeff);


        }


        canvas.drawLines(arr,drawPaint);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);


        if(this.datas.size()==0 ) return;

       this.drawAxis(canvas);

        this.drawLables(canvas);
        this.drawData(canvas);


    }
}
