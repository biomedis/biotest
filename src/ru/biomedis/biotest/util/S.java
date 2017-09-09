package ru.biomedis.biotest.util;

import android.app.Application;
import android.util.DisplayMetrics;
import ru.biomedis.biotest.BiotestApp;

/**
 * Created by Anama on 28.11.2014.
 */
public class S {

    /*
    private static final int ORIGINAL_VIEW_WIDTH = 768;
    private static final int ORIGINAL_VIEW_HEIGHT = 1184;
    private static final int ORIGINAL_VIEW_DIAGONAL = calcDiagonal(ORIGINAL_VIEW_WIDTH, ORIGINAL_VIEW_HEIGHT);

    private static int mWidth;
    private static int mHeight;
    private static int mDiagonal;
    private static float mDensity;

    static {
        DisplayMetrics metrics = BiotestApp.getContext().getResources().getDisplayMetrics();
        mWidth = metrics.widthPixels;
        mHeight = metrics.heightPixels;
        mDiagonal = calcDiagonal(mWidth, mHeight);
        mDensity = metrics.density;
    }

    public static int hScale(int value){
        return (int)Math.round(value * mWidth / (float) ORIGINAL_VIEW_WIDTH);
    }

    public static int vScale(int value){
        return (int)Math.round(value * mHeight / (float) ORIGINAL_VIEW_HEIGHT);
    }

    public static  int dScale(int value){
        return (int)Math.round(value * mDiagonal / (float) ORIGINAL_VIEW_DIAGONAL);
    }

    public static  int pxFromDp(int dp){
        return (int)Math.round(dp * mDensity);
    }

    private static int calcDiagonal(int width, int height){
        return (int)Math.round(Math.sqrt(width * width + height * height));
    }

    */
}
