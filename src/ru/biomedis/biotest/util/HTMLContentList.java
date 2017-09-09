package ru.biomedis.biotest.util;

import android.content.Context;
import android.text.TextUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Класс читает файлы XML содержащие данные о размещении HTML статей итп.
 *
 * Используется XML вида:
 *<pages base_dir="manual" image_dir="images">
 *<page menu_name="Введение" doc_name="start.html" />
 *<page menu_name="Работа с профилями" doc_name="profile.html" description="Описывает работу с профилями: создание итп" />
 *<page menu_name="Проведение измерений" doc_name="measure.html" />
 *<page menu_name="Результаты измерений" doc_name="measure_result.html" />
 *<page menu_name="Динамика показателей" doc_name="dynamics.html" />
 *</pages>
 * Created by Anama on 05.01.2015.
 */
public class HTMLContentList
{
    private Context ctx;
    private int resIdXML;//идентификатор ресурса XML списка
    private  XmlPullParser xpp;
    private Page startPage;//стартовая
    private List<Page> list=new ArrayList<Page>();//список
    private String [] menuNames=null;

    private String baseDir;
    private String imageDir;

    public String getBaseDir()
    {
        return baseDir;
    }

    public String getImageDir()
    {
        return imageDir;
    }


    public Page getStartPage()
    {
        return startPage;
    }

    public HTMLContentList(Context ctx, int resIdXML)
    {
        this.ctx = ctx;
        this.resIdXML = resIdXML;
        this.xpp = ctx.getResources().getXml(resIdXML);
        parseXMLList();
    }

    /**
     * Обрабатывает XML, получает список страниц с параметрами
     */
    private void parseXMLList()
    {

        try {
        while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
            switch (xpp.getEventType()) {
                // начало документа
                case XmlPullParser.START_DOCUMENT:
                    //Log.v("START_DOCUMENT");
                    break;
                // начало тэга
                case XmlPullParser.START_TAG:

                    switch (xpp.getName())
                    {

                        case "page":


                            list.add(new Page());
                            for (int i = 0; i < xpp.getAttributeCount(); i++) {

                                 switch(xpp.getAttributeName(i))
                                 {
                                     case "menu_name":
                                       list.get(list.size()-1).setMenuName(xpp.getAttributeValue(i));
                                         break;
                                     case "doc_name":
                                         list.get(list.size()-1).setDocName(xpp.getAttributeValue(i));
                                         break;
                                     case "description":
                                         list.get(list.size()-1).setDescription(xpp.getAttributeValue(i));
                                         break;
                                     case "start_page":
                                         this.startPage=list.get(list.size()-1);
                                         break;
                                 }


                            }

                            break;



                        case "pages":
                            for (int i = 0; i < xpp.getAttributeCount(); i++) {

                                switch(xpp.getAttributeName(i))
                                {
                                    case "base_dir":
                                        this.baseDir=xpp.getAttributeValue(i);
                                        break;
                                    case "images":
                                        this.imageDir=xpp.getAttributeValue(i);
                                        break;
                                }


                            }
                            break;


                    }





                    break;
                // конец тэга
                case XmlPullParser.END_TAG:
                    break;

                // содержимое тэга
                case XmlPullParser.TEXT:
                  // xpp.getText();
                    break;

                default:
                    break;
            }
            // следующий элемент
            xpp.next();
        }


    } catch (XmlPullParserException e) {
    e.printStackTrace();
} catch (IOException e) {
    e.printStackTrace();
}
    }

    /**
     * Вернет список имен страниц. В прорядке их следования в XML
     * @return
     */
    public String[] getNameList()
    {
        if(menuNames!=null) return menuNames;

        menuNames=new String[list.size()];
        int i=0;
        for (Page  p : list)  menuNames[i++]=p.getMenuName();
        return menuNames;
    }

    /**
     * Вернет описание страницы по индексу
     * @param index
     * @return
     */
    public Page getPage(int index)
    {
        return list.get(index);
    }


    /**
     * Класс инкапсулирующий параметры страницы контента. Название для меню, и имя файла html.
     */
public class Page
{
    private String menuName;
    private String docName;
    private String description;

    public Page()
    {

    }
    public Page(String docName, String menuName,String description)
    {
        this.docName = docName;
        this.menuName = menuName;
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDocName()
    {
        return docName;
    }

    public void setDocName(String docName)
    {
        this.docName = docName;
    }

    public String getMenuName()
    {
        return menuName;
    }

    public void setMenuName(String menuName)
    {
        this.menuName = menuName;
    }
}

}
