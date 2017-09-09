package ru.biomedis.biotest.graph.MultiLineImpl;

import android.content.Context;
import android.util.AttributeSet;
import ru.biomedis.biotest.graph.LineGraphMultiSeries;

/**
 * Created by Anama on 23.12.2014.
 */
public class MultiLineDouble extends LineGraphMultiSeries<Double>
{
    public MultiLineDouble(Context context)
    {
        super(context);
    }

    public MultiLineDouble(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MultiLineDouble(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
}
