package ru.biomedis.biotest.graph;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.ViewGroup;
import ru.biomedis.biotest.R;
import ru.biomedis.biotest.util.BaseCustomView;

import java.util.ArrayList;
import java.util.List;

/**
  * Created by Anama on 30.09.2014.
 */
public  class ScaterGraph extends BaseCustomView
{


    /// Доп параметры графика ////
    int sizeFontAxisDigit = (int) pxFromDp(12);
    int axismargin = (int) pxFromDp(15)+this.sizeFontAxisDigit;// отступ осей от краев виджета
    int sizeFontLabel = (int) pxFromDp(15);

    int colorBisect = Color.GRAY;
    int axisColor = Color.WHITE;
    int axisLabelColor = Color.WHITE;
    int countLabelsX = 7;
    int countLabelsY = 7;
    private int color_poits = 0;
    String labelX = "";
    String labelY = "";
   private  boolean isInit=false;


    ////

    private Point2D zeroPoint = new Point2D();//нулевая точка графика в координатах View
    private Point2D maxAxisX = new Point2D();//макс точка графика по оси X в координатах View
    private Point2D maxAxisY = new Point2D();//макс точка графика по оси Y в координатах View
    private double maxData = 0;
    private double minData = 0;

    private float[] dataMass;

    protected Resources resources;


    private List<Double> dataArray = new ArrayList<Double>();//данные


    public ScaterGraph(Context context) { this(context, null);}

    public ScaterGraph(Context context, AttributeSet attrs) {this(context, attrs, 0); }

    public ScaterGraph(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        this.resources = context.getResources();
        colorBisect = this.resources.getColor(R.color.ui_grey);
        color_poits = this.resources.getColor(R.color.green);


    }

    public void addData(Double item) { this.dataArray.add(item); }

    public void addAllData(List<Double> d) { this.dataArray = d; }

    public void clearData() {this.dataArray.clear(); }

    public void setLabelX(String labelX)
    {
        this.labelX = labelX;
    }

    public void setLabelY(String labelY)
    {
        this.labelY = labelY;
    }


    private float calcWidth = 0;

    /**
     * Нужно чтобы оси были одинаковыми, смотрим по высоте
     *
     * @param canvas
     */
    private void drawAxis(Canvas canvas)
    {

        Paint paint = new Paint();
        paint.setColor(this.axisColor);
        paint.setStrokeWidth((int) pxFromDp(1));

        this.zeroPoint.setX(axismargin);
        this.zeroPoint.setY(getRealHeight() - axismargin);

        // calcWidth=getRealHeight();

        // getLayoutParams().width=(int)calcWidth;

        this.maxAxisX.setX(getRealWidth() - pxFromDp(3));
        this.maxAxisX.setY(this.zeroPoint.getY());

        this.maxAxisY.setX(axismargin);
        this.maxAxisY.setY(pxFromDp(3));


        canvas.drawLine(this.zeroPoint.getX(), this.zeroPoint.getY(), this.maxAxisY.getX(), this.maxAxisY.getY(), paint);
        canvas.drawLine(this.zeroPoint.getX(), this.zeroPoint.getY(), this.maxAxisX.getX(), this.maxAxisX.getY(), paint);

        paint.setColor(colorBisect);
        paint.setAntiAlias(true);
        canvas.drawLine(this.zeroPoint.getX(), this.zeroPoint.getY(), this.maxAxisX.getX(), this.maxAxisY.getY(), paint);

        this.maxData = 0;
        this.minData = 100000;

        for (Double data : this.dataArray) {

            if (data > this.maxData) this.maxData = data;
            if (data < this.minData) this.minData = data;
        }


    }


    private void initPoints()
    {
        /*
        У нас каждые 2 элемента массива это точка первый x второй y
         */
        int size=(int)Math.ceil(dataArray.size()/2)*2;
        dataMass=new float[size];

        double hCoeff = (this.maxAxisX.getX() - this.zeroPoint.getX()) / this.maxData;
        double vCoeff = (this.zeroPoint.getY() - this.maxAxisY.getY()) / this.maxData;
        for (int i=0;i<size;)
        {

            dataMass[i]=(float)(this.zeroPoint.getX()+this.dataArray.get(i)*hCoeff);
            i++;
            dataMass[i]=(float)(this.zeroPoint.getY()-this.dataArray.get(i)*vCoeff);
            i++;
        }
        isInit=true;
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
        float deltaX=   (float)((this.maxAxisX.getX()-this.zeroPoint.getX())/this.maxData);
        int delta=(int)Math.ceil(this.maxData/countLabelsX);
        int ss=(int)Math.floor(this.maxData);
        paint.setTextAlign(Paint.Align.CENTER);
        for(int i=0;i<=ss;i+=delta)
        {
            canvas.drawText(i+"",this.zeroPoint.getX()+i*deltaX,zeroPoint.getY()+this.sizeFontAxisDigit+(int)pxFromDp(3),paint);
            canvas.drawLine(this.zeroPoint.getX()+i*deltaX,zeroPoint.getY()+(int)pxFromDp(3),this.zeroPoint.getX()+i*deltaX,zeroPoint.getY(),paint);
        }

        ////////

        ////Ось Y ///

        float deltaY=   (float)((this.zeroPoint.getY()-this.maxAxisY.getY())/this.maxData);
        delta=(int)Math.ceil(this.maxData/countLabelsY);
         ss=(int)Math.floor(this.maxData);
        paint.setTextAlign(Paint.Align.LEFT);

        int digitYPadding=0;
        if(maxData<100)digitYPadding=2*this.sizeFontAxisDigit;
        else if(maxData<1000)digitYPadding=2*this.sizeFontAxisDigit;
        else if(maxData<10000)digitYPadding=3*this.sizeFontAxisDigit;

        for(int i=0;i<=ss;i+=delta)
        {
            canvas.drawText(i+"",this.zeroPoint.getX()-digitYPadding,zeroPoint.getY()-i*deltaY+this.sizeFontAxisDigit/2,paint);
            canvas.drawLine(this.zeroPoint.getX()-(int)pxFromDp(3),zeroPoint.getY()-i*deltaY,this.zeroPoint.getX(),zeroPoint.getY()-i*deltaY,paint);

        }


        paint.setTextSize(this.sizeFontLabel);
        canvas.drawText("R-Ri",this.maxAxisX.getX()-this.sizeFontLabel*"R-Ri".length(),maxAxisX.getY()-(int)pxFromDp(2),paint);
        canvas.drawText("R-Ri+1",this.zeroPoint.getX()+(int)pxFromDp(5),maxAxisY.getY()+this.sizeFontLabel,paint);



    }

    private void drawData(Canvas canvas)
    {

        Paint p=new Paint();
        p.setColor(color_poits);
        p.setStrokeWidth(pxFromDp(4));
        canvas.drawPoints(dataMass,p);

    }



    @Override
    public  void onDraw(Canvas canvas)
    {

        super.onDraw(canvas);

        this.drawAxis(canvas);
        initPoints();//после drawAxis!!!!
        this.drawLabels(canvas);
        this.drawData(canvas);



    }










}
