package ru.biomedis.biotest.graph.SingleBarGraphImpl;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import ru.biomedis.biotest.graph.SingleBarGraph;

/**
 * Реализация бара для целочисленных значенийи
 * Created by Anama on 28.11.2014.
 */
public class IntegerSingleBarGraph extends SingleBarGraph<Integer,String> {


    public IntegerSingleBarGraph(Context context) {
        super(context);
    }

    public IntegerSingleBarGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IntegerSingleBarGraph(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void drawExtra(Canvas canvas, RectF rect, Integer data) {

    }
}
