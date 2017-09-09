package ru.biomedis.biotest;

import ru.biomedis.biotest.util.FFT.Complex;
import ru.biomedis.biotest.util.Log;

import javax.xml.transform.Source;
import java.io.Serializable;
import java.util.*;

/**
 * Класс работы с сырыми данными с датчика
 * Позволяет получить RR интервалы для дальнейшей работы
 * Created by Anama on 06.10.2014.
 */
public class RawDataProcessor implements Serializable
{
    private List<Short> rawData;//сырые данные с датчика.  Они short тк нужны + значения, а у нас 0 - 255, и byte не подойдет
    private Integer nearCountElements;//примерное колличество сырых данных исходя из int testTime,int dataReadPeriodUSB_mc(параметры в digits_params.xml)
    private List<Point<Integer>> rrArray;//rr интервалы
    private List<Point<Double>> interpolatedRRArray;// интерполированные интервалы, здесь индекс он относится ко временному параметру.
    private int deltaRRPorog=50;//порог аритмии, те разница между соседними интервалами выше которой у нас типа считается что аритмия и мы этот интервал заменим на предыдущий
    private int maxRR=0;
    private Histogramm histogramm;
    private Spectrum spectrum;
    private final int interpolayedArraySise=32768;
    private boolean isAlternate=false;//указывает на то что класс инициализируется альтернативно, готовыми данными
    private byte[] rawDataStore;// хранилище для сырыхх данных, могут быть сжаты


    private int HR;

    /**
     * Частота сердечных сокращений в минуту
     * @return
     */
    public int getHR() {
        return HR;
    }

    public int getMaxRR() {
        return maxRR;
    }

    public List<Point<Integer>> getRR(){return rrArray;}
    public List<Point<Double>> getInterpolatedRR(){return interpolatedRRArray;}
    public Histogramm getHistogramm() {   return histogramm; }

    public Spectrum getSpectrum() {
        return spectrum;
    }

   public class Point<T> implements Serializable

    {
        private int x;
        private  T y;

        Point(int x, T y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public T getY() {
            return y;
        }

        public void setY(T y) {
            this.y = y;
        }



    }


    public RawDataProcessor(int testTime, int dataReadPeriodUSB_mc)
    {
        this.nearCountElements= (int)Math.floor(testTime / (dataReadPeriodUSB_mc*0.001));
        this.rawData=new ArrayList<Short>(this.nearCountElements);//выделим сразу памяти
        this.interpolatedRRArray=new ArrayList<Point<Double>>();
        this.rrArray=new ArrayList<Point<Integer>>();
        this.spectrum=new Spectrum();
        this.histogramm=new Histogramm();
        isAlternate=false;


    }

    public RawDataProcessor(List<Point<Integer>> rrArray,ArrayList<Double> spector_array,int HR)
    {
        isAlternate=true;
        this.rrArray=rrArray;
        this.spectrum=new Spectrum(spector_array);
        this.histogramm=new Histogramm();
        this.HR=HR;

    }

    /**
     * Для работы суже готовыми сырыми данными
     * @param testTime
     * @param dataReadPeriodUSB_mc
     * @param rawData
     */
    public RawDataProcessor(int testTime, int dataReadPeriodUSB_mc, byte[] rawData)
    {
       this(testTime,dataReadPeriodUSB_mc);
        //rawDataStore=rawData;
        short tmp;
        for(byte itm :rawData)
        {

            this.rawData.add(itm<0?(short)(itm+256):itm);
        }


    }



    /**
     *
     * @return Массив с данными
     */
    public  List<Short> getRawData() {
        return rawData;
    }

    /**
     * Метод получает суб-массив данных
     * @param from
     * @param to
     * @return
     */
    private List<Short> getRawData(int from,int to ) {
        return rawData.subList(from,to);
    }

    /**
     * Вставка данных в массив
     * @param val
     */
    public void addRawData(short val)
    {
        this.rawData.add(val);

    }

    private short getDataElement(int index)
    {
        return this.rawData.get(index);

    }

    /**
     * Колличество реально записанных данных в массив
     * @return
     */

    public int getRawDataCount(){return this.rawData.size();}
    public void clearData()
    {
     this.rawData.clear();

    }


    public List<Point<Integer>> getRrArray() {
        return rrArray;
    }

    public List<Point<Double>> getInterpolatedRRArray() {
        return interpolatedRRArray;
    }








    /**
     * Выдает пороговую линию для расчета пиков
     * @param index индекс в масиве сырых данныъ
     * @param size сколько данных брать для расчета
     * @return
     */
    private int getPorogLine(int index,int size)
    {
        int max=0;
        int min = Integer.MAX_VALUE;

        int endIndex=index+size;
        for(int i=index;i<endIndex;i++)
        {
            if(this.rawData.get(i)>max) max=this.rawData.get(i);
            if(this.rawData.get(i)<min) min=this.rawData.get(i);

        }

        return (max-(max-min)/4);

    }


    /**
     * В результате у нас RR в десятках мс. Те 80 это на самом деле 800 мс
     * Рассичитывает R-R интервалы и интерполированную версию, самостоятельно определит альтернативную версию инициализации и вызовет нужный метод
     */
    public void calcR_R()
    {
        //если у нас альтернативный конструктрор сработал, то мы производим альтернативный расчет
        if(isAlternate==true) {calcRRAlternate(); return;}

        int step=256;
        int max=0;
        List<Point> tempArray=new ArrayList<Point>();

        this.rrArray.clear();
        this.interpolatedRRArray.clear();
        /*
        Мы рассматриваем данные по 256 отсчетов, в этот период попадает 3-4 пика.
        Мы считаем параметр max-(max-min)/4
        Используем его как планку выше которой считаем есть только нужные нам  R пики.
        Находим их после чего  мы вычисляем расстояния между пиками и пишем в новый массив
        Далее он корректируется и мы отбрасываем неподходящие интервалы и пересчитывем.
        Далее мы его интерполируем и приводим ось х к нулю, чтобы первый элемент был от нуля.
        В результате получаем нужный нам массив.
        Данный массив интерполируем между пиками
         */

        int porog=0;//пороговое значение выше которого будем брать значения.

        int roundSize=(rawData.size()/step)*step;
        int ostatok=rawData.size()-roundSize;
        int lastMaxIndex=0;// индекс последнего найденного R пика.

        for (int i=0;i<roundSize;i+=step)//проходим по кускам данных
        {

            porog =  getPorogLine(i,step);//получим порогувую линию
            for(int j=0;j<step;j++)//внутри куска данных считаем
            {


                if(rawData.get(i+j)>=porog)
                {
                    if(rawData.get(i+j)>max)
                    {
                        max=rawData.get(i+j);
                        lastMaxIndex=j+i;
                    }
                }
                else
                {
                    if(max==0) continue;// мы еще не считали максимум, или это вход ниже porog
                    else
                    // пересекли линию и сразу срабатывет, если не пересекли и кусок закончен то мы продолжем считадь далее на следующем витке
                    // также сработает если мы перешли на следующий кусок и не переходили через порог, а при пересчете порог поднялся и новая точка оказалась ниже. То мы запишем старый максимум
                    { tempArray.add(new Point<Short>(lastMaxIndex,rawData.get(lastMaxIndex))); max=0;}
                }
            }


            if(i==roundSize-1)// посчитаем остаток
            {
                porog =  getPorogLine(roundSize,ostatok);//получим порогувую линию
                for(int j=0;j<ostatok;j++)//внутри куска данных считаем
                {
                    if(rawData.get(roundSize+j)>=porog)
                    {
                        if(rawData.get(roundSize+j)>max)
                        {
                            max=rawData.get(roundSize+j);
                            lastMaxIndex=j+roundSize;
                         }
                    }
                    else
                    {
                        if(max==0) continue;// мы еще не считали максимум, или это вход ниже porog
                        else{ tempArray.add(new Point<Integer>(lastMaxIndex,rawData.get(lastMaxIndex).intValue())); max=0;}
                    }
                }
            }
        }




int rr=0;
        // вычисление R-R
        for(int i=0;i<tempArray.size()-1;i++)
        {
            rr=tempArray.get(i+1).getX()-tempArray.get(i).getX();
            tempArray.get(i).setY(rr);
            this.rrArray.add( tempArray.get(i));

        }
        tempArray.clear();

        //Заменяет интервал на предыдущий если разница стала с ним > 50 те более пол секунлы, типа аритмию правит, те если они соответствуют менее 30 ударов, те менее 50 отсчетов, тк 1 удар в сек(60 в мин) это 100 отсчетов те 10 мс*100=1сек
       // тут  момент если разовая аритмия, то прокатит, а если много подряд идут, то мы заменим на нормальные. Те идет 1 1 1 5 5 5 5 1 1 1 , то у нас все будут 1111111
        // и мы не учтем эти значения. Хотя возможно они не так важны тк их мало, но мы повышаем вес других. Может быть лучше удалять их???
        for(int i=0;i<this.rrArray.size()-1;i++)
        {
           if(Math.abs(this.rrArray.get(i+1).getY()-this.rrArray.get(i).getY()) > deltaRRPorog) this.rrArray.get(i+1).setY(this.rrArray.get(i).getY());


        }

int rawDataSize=this.rawData.size();
       // this.rawData.clear();// очистим сырые данные


        int x1= 0;
        double y1 = 0;
        int x2 = 0;
        double y2= 0;
        double yn=0;
        int xn=0;
        int b=0;
        double k=0;
        int difer = this.rrArray.get(0).getX();

        double last1=this.rrArray.get(this.rrArray.size()-2).getY();//крайние элементы
        double last2=this.rrArray.get(this.rrArray.size()-1).getY();

        // интерполяция/ В х координата в y интервал
        for(int i=0;i<this.rrArray.size()-1;i++)
        {
             x1 = this.rrArray.get(i).getX();
             y1 = this.rrArray.get(i).getY();
             x2 = this.rrArray.get(i+1).getX();
             y2 = this.rrArray.get(i+1).getY();

             k = (y2-y1)/(x2-x1);//производная интервалов по времени появления
             b = (x2-x1);
            for(int j = 0; j<b;j++)
            {
                 yn = k*j+y1;//интерполиуем точки по направлению производной
                 xn = j+x1-difer;
                 interpolatedRRArray.add(new Point<Double>(xn,yn));
            }
        }

        int iSize=interpolatedRRArray.size();

     boolean   while_count =true;// заполним массиив до 300000



        if(iSize<interpolayedArraySise) while (interpolatedRRArray.size() < interpolayedArraySise)
               {
                   if(while_count)
                   {
                       interpolatedRRArray.add(new Point<Double>(interpolatedRRArray.size(),last1));
                       while_count = false;
                   }
                   else
                   {
                       interpolatedRRArray.add(new Point<Double>(interpolatedRRArray.size(),last2));
                       while_count = true;
                   }
               }






       this.maxRR= maxRR();
        Log.v("RR- ВЫЧИСЛЕН");
       this.histogramm.calcHistogram();
        Log.v("HIST- ВЫЧИСЛЕН");
       this.spectrum.calcSpectrum();
        Log.v("SPEC- ВЫЧИСЛЕН");
       // (this.rawData.size() * 0.01) это время измерения в секундах. те число данныых на расстояние во времени в секундах между ними (10 мс)
        double countPerSec = this.rrArray.size() / (rawDataSize * 0.01);// как бы число сокращений в секунду сердца
        this.HR=(int)Math.floor(countPerSec*60);

        Log.v(countPerSec+"HR");

        Log.v("ALL- ВЫЧИСЛЕН");
    }

    /**
     * Альтернетивный расчет для готовых данных RR, вызывается при восстановлении из БД, с альтернативным конструктором.
     */
    private void calcRRAlternate()
    {

        this.maxRR= maxRR();
        this.histogramm.calcHistogram();
        this.spectrum.calcSpectrumAlternate();// альтернативный расчет спектра




    }


    public int maxRR()
    {
        int max=0;
        for(Point<Integer> p:rrArray)
        {
            if(p.getY()>max) max=p.getY();
        }
        return max;
    }

//TODO гистограмма не учитывет полимодальность. Максимум можно найти даже из двух мод, но вот ширину(range ) уже никак. Нужно как -то находить факт полимодальности и что-то с этим делать!

    /**
     * У нас данные пришли через каждые 10 мс, значит если у на интервал 50 отсчетов это 50*10 мс.те 500 мс или 0.5 сек. Те нужно 50 / 100. В формулах расчета это учтено и получается все уже в секундах
     * Гистограммы
     */

   public class Histogramm implements Serializable
    {
       private  ArrayList<Point<Integer>> histogramm ;
       private double MO;
       private double Amo;
       private  double range;
       private double IN;// индекс напряженности
       private final double koeffPorog=0.1; //коэффициент порога, от 0 до 1 он умножается на высоту гистограммы, ниже него мы отбрасываем диапазоны при расчете ширины гистограммы
        private int stepHistogramm=5;
        private int startDiapHistogramm=20;
        private int endDiapHistogramm=130;

        /**
         * Макс диапазон. Он виртуальный. Для графика. Дает максимумдля графика
         * @return
         */
        public int getEndDiapHistogramm() {
            return endDiapHistogramm;
        }

        /**
         * Шаг гистограммы по X
         * @return
         */
        public int getStepHistogramm() {
            return stepHistogramm;
        }

        /**
         * Начальный диапазон по Х гистограммы
         * @return
         */
        public int getStartDiapHistogramm() {
            return startDiapHistogramm;
        }

        /**
         * Выдает данные гистограммы , частоты!!!, не проценты
         * По X интервал R-R, по Y частота
         * @return
         */
        public ArrayList<Point<Integer>> getHistogramm() {
            return histogramm;
        }

        /**
         * Индекс напряженности
         * @return
         */
        public double getIN() {
            return IN;
        }

        /**
         * Ширина гистограмы в секундах!
         * @return
         */
        public double getRange() {
            return range;
        }

        /**
         * Мода в секундах, те наиболее часто встречающийся диаппазон  RR
         * @return
         */
        public double getMO() {
            return MO;
        }

        /**
         * Высота гистограммы в процентах от общего числа выборок RR
         * @return
         */
        public double getAmo() {
            return Amo;
        }

        Histogramm() {
            this.histogramm=new ArrayList<Point<Integer>>();
        }


        /**
         * Гистограмма интервалов. от 20 и больше с шагом 5, сколько попадает в интервлы
         * @return
         */

        private ArrayList<Point<Integer>> calcHistogram()
        {
            this.histogramm.clear();

            int delta=this.stepHistogramm;//шаг гистограммы
            int startDiap=this.startDiapHistogramm;//стартовый диапазон
            int maxRR=maxRR();
            int [] rawHist = new int[maxRR+1];//maxRR должен поместиться
            Arrays .fill(rawHist,0);

            for(Point<Integer> p : rrArray)   rawHist[ p.getY()]++;// сырая гистограмма

            int count=0;

            for(int i=startDiap;i<=maxRR;i+=delta)
            {
                if(i>maxRR) break;
                count=0;
                for(int j=i;j<(i+delta);j++)
                {
                    if(j>maxRR) break;
                    count+=rawHist[j];
                }
               // if(count!=0)
                    this.histogramm.add(new Point<Integer>(i,count));




            }

            this.Amo= this.calcHistogramAmo();
            this.MO=this.calcHistogramMo();

            this.range=this.calcHistogramRange();
            this.IN=this.calcIN();

            return this.histogramm;
        }

        /**
         * в секундах
         * Диаппазон, ширина гистограммы. Использует getAmo() и должен вызываеться после его расчета в секундах
         * @return
         */
        private double calcHistogramRange()
        {


            Double maxY = koeffPorog*getAmo()*rrArray.size()/100;// Отбрасывакм сосем ниское число частот *rrArray.size()/100 - тк ранее мы привели высоту гистограммы к такому видлу в процентах от выборки, для расчета IN, а нам нужно абсолютное значение
            int  max = 0;
            int min = 100000;

            for(int i = 0; i<this.histogramm.size(); i++)
            {
                if(this.histogramm.get(i).getY() <  maxY.intValue()) continue;// Отбрасывакм сосем ниское число частот, в 10 раз меньше чем максимум

                if(  this.histogramm.get(i).getX() > max ) max = this.histogramm.get(i).getX();
                if( this.histogramm.get(i).getX() < min  ) min = this.histogramm.get(i).getX();
            }

            if(max==min) max+=this.stepHistogramm;

            return (double)(max-min)/100;//делим на 100 чтобы результат был в сек, тк у нас значения между отсчетами 10 мс.
        }

        /**
         * Максимум в гистограмме, высота деленная на величинну выборки и на 100 чтобы в процентах
         * @return Величина в процентах
         */
        private double calcHistogramAmo()
        {
            int max = 0;
            for(int i = 0; i<this.histogramm.size(); i++){
                if(max<this.histogramm.get(i).getY())max = this.histogramm.get(i).getY();

            }
            return ((double)max/(double)rrArray.size())*100;
        }

        /**
         * Мат ожидание те точка Х в которой максимальна гистограмма, в секундах
         *
         * @return
         */
        private double calcHistogramMo()
        {
            int max = 0;
            int mo = 0;
            for(int i=0;i<this.histogramm.size();i++)
            {
                if(max<this.histogramm.get(i).getY())
                {
                    max = this.histogramm.get(i).getY();
                    mo = this.histogramm.get(i).getX();
                }
            }
            return (double)mo/100;// делим на 100 чтоб было в сек
        }


        /**
         *Индекс напряжения
         * Вернемся к индексу напряжения.
         Итак. Индекс напряжения, иногда его называют стресс-индекс, был введен Баевским и вычисляется по формуле:

         Ин = АМо /(2 * Mо* MxDMn)

         Мо (мода) – это наиболее часто встречающееся в данном динамическом ряде значение кардиоинтервала.
         Амо (амплитуда моды) – это число кардиоинтервалов, соответствующих значению моды, в % к объему выборки.
         MxDMn (вариационный размах) отражает степень вариативности значений кардиоинтервалов в исследуемом динамическом ряду. Он вычисляется по разности максимального (Mx) и минимального (Mn) значений интервалов и поэтому при аритмиях или артефактах может быть искажен. При построении гистограмм (или вариационных пульсограмм; это зависит от того, что было изначально зарегистрировано — сердечные сокращения или пульс) первостепенное значение имеет выбор способа группировки данных. В многолетней практике сложился традиционный подход к группировке кардиоинтервалов в диапазоне от 400 до 1300 мс с интервалом в 50 мс. Таким образом, выделяются 20 фиксированных диапазонов длительностей кардиоинтервалов, что позволяет сравнивать вариационные пульсограммы, полученные разными исследователями на разных группах исследований. При этом объем выборки, в которой производится группировка и построение вариационной пульсограммы, также стандартный – 5 минут.
         * @return
         */

        private double calcIN()
        {


            return getAmo()/(double)(2* getMO()*getRange());
        }

    }


    /**
     * так как частота прихода символов с датчика 100 гц значит по фурье мы можем получить чатоту максимум в 50 гц(это котельников). Весь выходной массив состоит из 16378 элементов....50 гц делим на 16378 элементов получаем где то 0,0030528. Нам нужен диапазон до 0.4 Гц, вычисляем сколько элементов на с интересует в выходном массива фурье, для этого 0.4 делим на шаг т.е.0,0030528 и получается 131,024 округляем до 132 вот и все. Остальные диапазоны по такомуже принципу
     * Высокие частоты (HF - High Frequency) - 0.15 - 0.40 Гц.
     * Низкие частоты (Low Frequency - LF) - 0.04 - 0.15 Гц.
     * Очень низкие частоты (Very Low Frequency - VLF) -0,003 - 0, 04 Гц
     * В этой волновой структуре выделяют три типа волн в зависимости от их длительности.
     HF (High Frequency) — высокая частота, быстрые волны. Их длительность составляет 2,5-6,6 сек., а частота колебаний — 0,15-0,4 Гц. Обычно на графике выделяются зеленым цветом.
     LF (Low Frequency) — низкая частота, средние волны. Их длительность составляет 10-30 сек., а частота колебаний — 0,04-0,15 Гц. На графике обычно обозначаются красным цветом.
     VLF (Very Low Frequency) — очень низкая частота, медленные волны. Их длительность превышает 30 сек., а частота колебаний менее 0,04 Гц. На графике обычно выделяют голубым цветом.
     До сих пор еще идут споры по поводу определения биологического значения и точных границ этих диапазонов, но в большинстве случаев исследователи сходятся на следующем понимании.
     HF диапазон отражает процессы парасимпатической активности.
     LF диапазон связан с симпатической активностью.
     VLF диапазон отражает гуморально-метаболические влияния.
     * Спектр R-R
     */
   public class Spectrum implements Serializable
    {
        private final double baseFreq=100;// частота дескритизации
        private final double maxSpectrFreq=baseFreq/2;//макс частота в спектре по котельникову
        private double deltaFreq=0;// шаг частоты, вычисляется как maxSpectrFreq/ число отсчетов массива
        private  ArrayList<Double> spector_array = new ArrayList<Double>();
        private  ArrayList<Point<Double>> spector_vlf_array = new ArrayList<Point<Double>>();
        private   ArrayList<Point<Double>> spector_lf_array = new ArrayList<Point<Double>>();
        private   ArrayList<Point<Double>> spector_hf_array = new ArrayList<Point<Double>>();
        private  double startVLF =0.003;
        private  double startLF =0.04;
        private  double startHF =0.15;
        private  double endHF =0.45;

        private double TP;
        private double LF;
        private double VLF;
        private double HF;

        private ArrayList<Integer> meridians;

        public ArrayList<Integer> getMeridians() {
            return meridians;
        }

        public double getTP() {
            return TP;
        }

        public double getLF() {
            return LF;
        }
        public double getVLF() {
            return VLF;
        }
        public double getHF() {
            return HF;
        }


        public double getStartVLF() {
            return startVLF;
        }

        public double getStartLF() {
            return startLF;
        }

        public double getStartHF() {
            return startHF;
        }

        public double getEndHF() {
            return endHF;
        }

        /**
         * макс частота в спектре по котельникову
         * @return
         */
        public double getMaxSpectrFreq() {
            return maxSpectrFreq;
        }

        /**
         * частота дескритизации
         * @return
         */
        public double getBaseFreq() {
            return baseFreq;
        }

        /**
         * шаг частоты, вычисляется как maxSpectrFreq/ число отсчетов массива
         * @return
         */
        public double getDeltaFreq() {
            return deltaFreq;
        }



        public ArrayList<Double> getSpector() {
            return spector_array;
        }



        public ArrayList<Point<Double>> getSpectorVLF() {
            return spector_vlf_array;
        }



        public ArrayList<Point<Double>> getSpectorLF() {
            return spector_lf_array;
        }



        public ArrayList<Point<Double>> getSpectorHF() {
            return spector_hf_array;
        }

        public Spectrum()
        {

        }
        /**
         * Альтернативный конструктор для готового спектра
         * @param spector_array
         */
        public Spectrum(ArrayList<Double> spector_array)
        {
            this.spector_array = spector_array;
        }

        /**
         * Альтернативный расчет параметров на основе готового спектра, используется при чтении сохраненных данных из базы
         *
         */
        private void calcSpectrumAlternate()
        {

          //  deltaFreq=maxSpectrFreq/interpolayedArraySise;

            deltaFreq=baseFreq/interpolayedArraySise;

            spector_vlf_array.clear();
            spector_lf_array.clear();
            spector_hf_array.clear();

            int maxSpektrKoeff=(int)Math.floor(getEndHF()/getDeltaFreq());
            int VLFSpektrKoeff=(int)Math.floor(getStartVLF()/getDeltaFreq());
            int LFSpektrKoeff=(int)Math.floor(getStartLF()/getDeltaFreq());
            int HFSpektrKoeff=(int)Math.floor(getStartHF()/getDeltaFreq());



            for(int i=0;i<LFSpektrKoeff;i++) spector_vlf_array.add(new Point<Double>(i,spector_array.get(i)));

            for(int i=LFSpektrKoeff;i<HFSpektrKoeff;i++) spector_lf_array.add(new Point<Double>(i,spector_array.get(i)));

            for(int i=HFSpektrKoeff;i<maxSpektrKoeff-1;i++) spector_hf_array.add(new Point<Double>(i,spector_array.get(i)));

            this.TP= this.calcTP();
            this.LF=   this.calcLF();
            this.HF= this.calcHF();
            this.VLF=this.calcVLF();
            this.meridians= this.calcMeredian();



        }


        private void  calcSpectrum()
        {

            //deltaFreq=maxSpectrFreq/(double)interpolatedRRArray.size();
            deltaFreq=baseFreq/(double)interpolatedRRArray.size();
            spector_array.clear();
            spector_vlf_array.clear();
            spector_lf_array.clear();
            spector_hf_array.clear();

            int maxSpektrKoeff=(int)Math.floor(getEndHF()/getDeltaFreq());
            int VLFSpektrKoeff=(int)Math.floor(getStartVLF()/getDeltaFreq());
            int LFSpektrKoeff=(int)Math.floor(getStartLF()/getDeltaFreq());
            int HFSpektrKoeff=(int)Math.floor(getStartHF()/getDeltaFreq());



            //ДПФ
            double Xk = 0;
            double Yk =0;
            double top = (2*Math.PI) / interpolatedRRArray.size();
            ArrayList<Double> xk_array_cos = new ArrayList<Double>();
            ArrayList<Double> xk_array_sin = new ArrayList<Double>();

            //  k/N - это частота текцщего вычисления( в спектре), n -дискретное время.
            for(int k=0; k<maxSpektrKoeff*2.2;k++)
            {
                for(int n=0;n<interpolatedRRArray.size();n++)
                {
                    Xk += interpolatedRRArray.get(n).getY() * Math.cos(top*n*k);
                    Yk += interpolatedRRArray.get(n).getY() * Math.sin(top * n * k);
                }
                Xk /= interpolatedRRArray.size();
                Yk /= interpolatedRRArray.size();

                xk_array_cos.add(Xk);
                xk_array_sin.add(Yk);

                Xk = 0;
                Yk=0;
            }

          //  StringBuilder strb=new StringBuilder();
        double maxf=0;
        int maxInd=0;

            //отсеем пост составляющую она на 0
            for(int i=1;i<maxSpektrKoeff;i++)
            {
                double cos = Math.pow(xk_array_cos.get(i), 2);
                double sin = Math.pow(xk_array_sin.get(i), 2);
                double z1 = Math.sqrt(sin + cos);
                spector_array.add(z1*100);// умножим на 100 тк у нас исходные интервалы в десятках мс.
            //    if(i>0)if(z1>maxf){maxf=z1;maxInd=i;}
            }
          //  Double dbl=new Double(getDeltaFreq()*(double)maxInd);
          //  strb.append("MAX freq ="+dbl+"Гц");

            //БПФ
            //Complex[] x =new Complex[32768];


            //далее выделим из спектра нужные участки соответствующие диапазонам
          //  strb.append("-------\n");

            for(int i=0;i<LFSpektrKoeff;i++)
            {
                spector_vlf_array.add(new Point<Double>(i,spector_array.get(i)));
             //   strb.append(spector_array.get(i)+",\n");
            }
           // strb.append("-------\n");
            for(int i=LFSpektrKoeff;i<HFSpektrKoeff;i++)
            {
                spector_lf_array.add(new Point<Double>(i,spector_array.get(i)));
               // strb.append(spector_array.get(i)+",\n");
            }
           // strb.append("-------\n");
            for(int i=HFSpektrKoeff;i<maxSpektrKoeff-1;i++)
            {
                spector_hf_array.add(new Point<Double>(i,spector_array.get(i)));
              //  strb.append(spector_array.get(i)+",\n");
            }






           // Log.v(strb.toString());
// далее при расчетах учтем что отсчеты у нас каждые 10 мс, а мы идем по индексам 1,2,3... значит мы умножаем результаты на 10 а не на 100
            this.TP= this.calcTP();
            this.LF=   this.calcLF();
            this.HF= this.calcHF();
            this.VLF=this.calcVLF();
           this.meridians= this.calcMeredian();
        }

        /**
         * TP в процентах.
         * @return
         */
        private double calcTP()
        {
            double all =0;
            for(double p:this.spector_array) all+=p;

            return all;
        }

        public double calcProc( double value)
        {
            return (value/TP) * 100;
        }

        public double calcVLF()
        {
            double all=0;

            for(Point<Double> p:this.spector_vlf_array)   all +=p.getY();

            return all;
        }

        public double calcLF()
        {
            double all=0;
            for(Point<Double> p:this.spector_lf_array)   all +=p.getY();
            return all;
        }

        public double calcHF()
        {
            double all=0;
            for(Point<Double> p:this.spector_hf_array)   all +=p.getY();
            return all;
        }




        private  ArrayList<Integer> calcMeredian()
        {
            int size = 0;
            int start_point = 0;
            int end_point = 0;
            Double value = 0.0;
            ArrayList<Integer> arr_value = new ArrayList<Integer>();
Log.v("CALC calcMeredian ");

            //VLF

            end_point = 0;
            start_point = 0;

            //TODO хз зачем убирать тут 0. Мы ведь уже удали пост сост
            //this.spector_vlf_array.remove(0);  ????????

            size = this.spector_vlf_array.size()/4;
            Double vlf_value = 0.0;

            for(int i=0;i<4;i++)
            {
                end_point += size;
                for(int m = start_point;m<end_point;m++)
                {
                    vlf_value+=this.spector_vlf_array.get(m).getY();
                    start_point=m;
                }
            }//тут расчитали фактически VLF тк раньше отбрасывался элемент


            end_point = 0;
            start_point = 0;

            for(int i=0;i<4;i++)
            {
                end_point += size;
                for(int m = start_point;m<end_point;m++)
                {
                    value+=this.spector_vlf_array.get(m).getY();
                    start_point=m;
                }
                Double _result = (value / vlf_value) * 100;
                arr_value.add((int)Math.round(_result));
                value = 0.0;
            }



            //LF
            end_point = 0;
            start_point = 0;
            size = this.spector_lf_array.size()/4;
            for(int i=0;i<4;i++)
            {
                end_point += size;
                for(int m = start_point;m<end_point;m++)
                {
                    value+=this.spector_lf_array.get(m).getY();
                    start_point=m;
                }
                Double _result = (value / this.LF)*100; //* 10000;
                arr_value.add(_result.intValue());
                value = 0.0;
            }

            //HF
            end_point = 0;
            start_point = 0;
            size = this.spector_hf_array.size()/4;
            for(int i = 0; i<4;i++)
            {
                end_point += size;
                for(int m = start_point;m<end_point;m++)
                {
                    value += this.spector_hf_array.get(m).getY();
                    start_point = m;
                }
                Double _result = (value / this.HF)*100;// * 10000;
                arr_value.add(_result.intValue());
                value = 0.0;
            }



            return arr_value;
        }


    }

    public int getIN()
    {

       return  (int)Math.round(getHistogramm().getIN());
    }

    public int getIS()
    {
        Double is = (getSpectrum().getHF() + getSpectrum().getLF()) / getSpectrum().getVLF();
        return  (int)Math.round(is);
    }

    public int getBAK()
    {
        Double bak = (getSpectrum().getHF()/getSpectrum().getTP())*100;
        return  (int)Math.round(bak);
    }

    public int getBE()
    {
        Double tp = getSpectrum().getTP();
        return  (int)Math.round(tp);
    }




    class Correlate
    {

    }



    class Chaotic
    {

    }


}
