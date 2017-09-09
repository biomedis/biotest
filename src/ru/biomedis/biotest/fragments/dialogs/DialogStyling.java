package ru.biomedis.biotest.fragments.dialogs;

import android.app.DialogFragment;
import android.view.View;
import ru.biomedis.biotest.R;

/**
 * Created by Anama on 09.02.2015.
 */
abstract public class DialogStyling extends DialogFragment
{
    /**
     * Использует хак стиля, вытаскивает нужные элементы диалогога и применяет стили/
     * Вызывать в onCreateView
     */
  protected void styling()
  {
      //хак разделительной полоски
      int titleDividerId = getResources().getIdentifier("titleDivider", "id", "android");
      View titleDivider = getDialog().findViewById(titleDividerId);
      if (titleDivider != null)  titleDivider.setBackgroundColor(getResources().getColor(R.color.actionbar_background_line));
  }
    /**
     * Преобразует значение из PX в DP согласно коэфициента масштабирования экрана
     * @param px
     * @return
     */
    public float dpFromPx(float px)
    {
        return px / getResources().getDisplayMetrics().density;
    }
    /**
     * Преобразует значение из DP в PX согласно коэфициента масштабирования экрана
     * @param dp
     * @return
     */
    public float pxFromDp(float dp)
    {
        return dp * getResources().getDisplayMetrics().density;
    }


}
