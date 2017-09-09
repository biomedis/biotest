package ru.biomedis.biotest.util;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.*;
import ru.biomedis.biotest.util.Log;
import android.widget.Toast;
import ru.biomedis.biotest.util.MidifyViews.BiotestToast;

import java.util.*;

//TODO проверить нужно при попытках использовать устройство есть ли разрешение, можно создать своЙ event. Также отрабатывать момент если юзер отказал!!. Можно рпосто в нужных местах мониторить разрешение и предлагать запрос . если отказано то мы блокируем действие.


/**
 * Класс инкапсулирует работу с usb устройствами.
 *
 * Created by Anama on 26.09.2014.
 *Как работать с классом.
 *
 * USBUtil  usbutil=new USBUtil(this);
 *
 * Просматриваем список подключенных устройств
 *
  HashMap<String, UsbDevice> usbMap = usbutil.enumerate();
 Iterator< UsbDevice > deviceIterator = usbMap.values().iterator();

  Находим подъодящее устройство
 UsbDevice targetDevice=null;
 while(deviceIterator.hasNext())
 {
 UsbDevice device = deviceIterator.next();

 if(device.getProductId()==1 & device.getVendorId()==64598) targetDevice=device;

 }
 регистрируем приемник usb событий(в нем происходят подключения к устройству)
 usbutil.registerUSBReciever();
 Запросим разрешение доступа к устройству. Этот вызов инициирует открытие устройстрва, если есть разрешение доступа или запросит таковое.
 if(targetDevice!=null)usbutil.requestPermission(targetDevice);//если разрешение есть он просто не покажет диалог, но приемник сработает


 Далее получаем буферы входные или выходные(их размер равен максимальному размеру пакета)
 byte[] buffer =  usbutil.getHelper(targetDevice).getInbuffer();

 // getHelper(targetDevice) получит объект через который мы будем работать с устройством

 Получаем или передаем данные
 final int status = usbutil.getHelper(targetDevice).bulkTransfer(USBUtil.EndpointDir.ENDPOINT_IN,2,300);

 Если реализовать в активности IUsbEventHandler и установить его void addEventHandler(IUsbEventHandler usbAction) в этом классе, то можно получать события detach и permission

 Работа ведется с нулевым интерфейсом USB и автоматически найдеными входной и выходной точками.
 Если нужно что-то более широкое - работа с разными интерфейсами, большее число конечных точек то нужно дописать класс.

 Класс позволяет работать одновременно с любым числом usb устройств. Для каждого будет свой Helper. Достаточно одного объекта USBUtil для всех устройств сразу.

 В будущем стоит доработать для работы в режиме аксесуара и  разными интерфейсами и конечными точками.



Безопасно использовать устройсво можно только после появления события IUsbEventHandler  isPermission, тогда устройство готово!! Если пытаться раньше будет ошибка


 *
 * Перед обменом с устройством нужно проверить есть ли доступ к нему, если нет то запросить.
 *  Мы делаем requestPermission(targetDevice) и если есть разрешение устройство просто откроется,
 *  если нет то будет диалог и откроется. Удобно размещать этот код в onCreate. Также регистрировать приемник. ТАкже приемник мы можем откл и вкл при приостоновке. Чтобы не висел. А можно и не отключать его.
 *  Устройство лучше не закрывать даже в destroy иначе придется перевтыкать. Оно закроется при вытыкивании.
 *
 */
public class USBUtil extends BroadcastReceiver
{

   public static final String ACTION_USB_PERMISSION = "ru.biomedis.biotest.USB_PERMISSION";

   private Context ctx;
   private UsbManager manager=null;

    private PendingIntent   mPermissionIntent=null;//интент для запроса разрешения к устройству

    private Map<UsbDevice,USBHelper> activeDevice;//хранит класс описатель устройств с которыми получено соединение

   private boolean isRegisterReciever=false;// был ли зарегистрирован приемник событий usb

    public static  enum EndpointDir{ENDPOINT_IN,ENDPOINT_OUT}

    private List<IUsbEventHandler> listEventHandler=new ArrayList<IUsbEventHandler>();

    public USBUtil(Context ctx) {
        this.ctx = ctx;
        this.manager=this.getUsbManager();
        activeDevice=new HashMap<UsbDevice,USBHelper>();


    }


    /**
     * Класс хранит информацию об usb устройстве с которым открыто соединение
     */

    public class USBHelper
    {
      private UsbDevice usbDev;
      private UsbInterface intf;
      private UsbEndpoint endpointIn;
      private UsbEndpoint endpointOut;
      private UsbDeviceConnection connection;
      private byte[] inBuffer=null;
      private byte[] outBuffer=null;





      private int maxPacketSizeIn;
      private int maxPacketSizeOut;


        private void createBufferIn(int size){inBuffer=new byte[size];}
        private void createBufferOut(int size){outBuffer=new byte[size];}


        public int getMaxPacketSizeIn() {
            return maxPacketSizeIn;
        }

        public int getMaxPacketSizeOut() {
            return maxPacketSizeOut;
        }

        public UsbEndpoint getEndpointOut() {
            return endpointOut;
        }

        private void setEndpointOut(UsbEndpoint endpointOut) {
            this.endpointOut = endpointOut;
        }

        /**
         * Возвращает ссылку на буфер приема. Буфер создается в момент открытия подключения
         * Размер равен максимальному размеру пакета из дескриптора устройства
         * @return
         */
        public byte[] getInbuffer()
        {
            return inBuffer;
        }
        /**
         * Возвращает ссылку на буфер передачи. Буфер создается в момент открытия подключения
         * Размер равен максимальному размеру пакета из дескриптора устройства
         * @return
         */
        public byte[] getOutbuffer(){return outBuffer;}

        /**
         * Получает UsbDevice сохраненного в классе.
         * @return
         */
        public UsbDevice getUsbDev() {
            return usbDev;
        }

        private void setUsbDev(UsbDevice usbDev) {
            this.usbDev = usbDev;
        }

        public UsbInterface getIntf() {
            return intf;
        }

        private void setIntf(UsbInterface intf) {
            this.intf = intf;
        }

        public UsbEndpoint getEndpointIn() {
            return endpointIn;
        }

        private void setEndpointIn(UsbEndpoint endpoint) {
            this.endpointIn = endpoint;
        }

        public UsbDeviceConnection getConnection() {
            return connection;
        }

        private void setConnection(UsbDeviceConnection connection) {
            this.connection = connection;
        }

        /**
         * Совершает передачу/прием через буфер. Обертка над bulkTransfer
         * @param count сколько читаем или передаем
         * @timeout timeout
         * @return колличество прочитанных байт или 0 если все хорошо,  -1 если проблемы с соединением, -33 если разрешения нет на работу с устройством(хотя конечно логика либы не допускает обмен с запрещенным устройством, но на всякий, те если у нас есть объект USBHelper то у нас есть разрешение.)
         */
        public int bulkTransfer(EndpointDir endPointDir,int count, int timeout)
        {

            if(manager.hasPermission(this.getUsbDev())==false) return -33;
            int res=-1;
           switch(endPointDir)
           {
               case ENDPOINT_IN:
                  res= this.connection.bulkTransfer(getEndpointIn(),this.inBuffer,count,timeout);
               break;

               case ENDPOINT_OUT:
                   res=  this.connection.bulkTransfer(getEndpointOut(),this.outBuffer,count,timeout);
               break;

           }

            return res;

        }
    }




    //PRIVATE


    private UsbManager getUsbManager()
    {
        return (UsbManager)ctx.getSystemService(Context.USB_SERVICE);
    }


    /**
     * Интерфейс обработки событий usb
     */
    public interface IUsbEventHandler
    {
        /**
         * Срабатывает когда изменяется разрешение устройства и как следствие устанавливается соединение с устройством
         * @param device устройство для которого произошло событие
         */
        public void onPermission(UsbDevice device,boolean permisson);

        /**
         * Срабатывает при отсоединении устройства
         * @param device устройство для которого произошло событие
         */
        public void onDetach(UsbDevice device);

    }

    public void addEventHandler(IUsbEventHandler usbAction)
    {
        this.listEventHandler.add(usbAction);

    }
    public void removeEventHandler(IUsbEventHandler usbAction)
    {
        this.listEventHandler.remove(usbAction);
    }


    //////PUBLIC

    /**
     * Откроет соединение с выбранным устройством через выбранный интерфейс. Конечноые точки определяются автоматически. Все параметры пишутся в UsbHelper
     * Читается он getHelper(UsbDevice dev). Не рекомендуется вызывать напрямую. Лучше открывать в onCreate через вызов reqeuestPermission. После переоткрытие программы произойдет автоматическое пересоздание. Внутри жизненоого цикла все данные будут наместах.
     * @param dev выбранное устройство
     * @param interfaceIndex индекс интерфейса
     * @return false в случае неудачного соединения или отсутствия конечных точек
     */
    private boolean openConnection(UsbDevice dev,int interfaceIndex)
{



    USBHelper uh=  new USBHelper();
    uh.setUsbDev(dev);
    uh.setIntf(dev.getInterface(interfaceIndex));

    if(uh.getIntf().getEndpointCount()==0) return false;

    for (int i = 0; i < uh.getIntf().getEndpointCount(); i++)
    {
        Log.v("ENDPOINT: index="+i+" тип="+uh.getIntf().getEndpoint(i).getType()+" направление="+uh.getIntf().getEndpoint(i).getDirection());
        if (uh.getIntf().getEndpoint(i).getDirection() == UsbConstants.USB_DIR_IN) uh.setEndpointIn(uh.getIntf().getEndpoint(i));
         else  uh.setEndpointOut(uh.getIntf().getEndpoint(i));

    }


    uh.maxPacketSizeIn=uh.getEndpointIn().getMaxPacketSize();
    uh.maxPacketSizeOut=uh.getEndpointOut().getMaxPacketSize();
    uh.createBufferIn(uh.maxPacketSizeIn);
    uh.createBufferOut(uh.maxPacketSizeOut);




    UsbDeviceConnection connection = this.manager.openDevice(dev);
    if(connection==null) return false;
    connection.claimInterface(dev.getInterface(interfaceIndex), true);

    uh.setConnection(connection);

    this.activeDevice.put(dev,uh);

return true;


}

    /**
     * Отключает устройстов на всегда. Требуется физическое переподключение после этого чтобы возобновить работу. Не рекомендуется вызывать напрямую.
     * @param dev
     */
  private void closeConnection(UsbDevice dev)
  {
      //закроем интерфейс и удалим из списка активное устройство
      USBHelper usbHelper = this.activeDevice.get(dev);
      usbHelper.getConnection().releaseInterface(usbHelper.getIntf());
      usbHelper.getConnection().close();
      this.activeDevice.remove(dev);


  }


    /**
     * Получает USBHelper из активных устройст, те тех к которым было открыто соединение
     * @param dev
     * @return
     */

public USBHelper  getHelper(UsbDevice dev)
{
    return this.activeDevice.get(dev);
}

    /**
     * Проверит есть ли присоединенное устройство в списке и вернет его.
     * @param PID
     * @param VID
     * @param name
     * @return Возвратит либо устройство или NULL если ненайдено
     */
    public UsbDevice findDevice(int PID,int VID,String name)
    {
        //Посмотрим что подключенно и поищем.
        HashMap<String, UsbDevice> usbMap = this.enumerate();
        Iterator< UsbDevice > deviceIterator = usbMap.values().iterator();
        UsbDevice dataDev=null;

        while(deviceIterator.hasNext())
        {
            UsbDevice device = deviceIterator.next();

            if(device.getProductId()==PID & device.getVendorId()==VID)
            {
                dataDev=device;
                if(name!=null)if(!name.isEmpty())
                {
                    if(device.getDeviceName().equals(name)) dataDev=device;
                    else dataDev=null;
                }


            }

           if( dataDev!=null) break;
        }
        return dataDev;
    }



    /**
     * Регает приемник интента запроса на разрешение usb
     */
    public  void registerUSBReciever()
    {
        if(!isRegisterReciever) {
            //регистрируем ожидающее намерение с упаковоным намерением(для запуска диалога разрешения)
            this.mPermissionIntent = PendingIntent.getBroadcast(ctx, 0, new Intent(ACTION_USB_PERMISSION), 0);
            
            // создадим фильтры и зарегаем приемник.
            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                         filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
            ctx.registerReceiver(this, filter);




            isRegisterReciever = true;
        }
    }

    /**
     * Убирает приемник интента запроса на разрешение usb
     */
    public  void unRegisterUSBReciever()
    {
        if(isRegisterReciever){
            ctx.unregisterReceiver(this);
        }
        isRegisterReciever=false;



    }


    /**
     * Проверит было ли выдано ранее разрешение на работу с устройством
     * @param dev устройстов usb
     * @return
     */
public boolean isPermission(UsbDevice dev)
{
    return manager.hasPermission(dev);
}

    /**
     * Список usb устройст подключенных
     * @return
     */
    public HashMap< String, UsbDevice> enumerate()
    {
        //здесь стоит проверять появились ли новые устройства или может какие были выключены. И обновлять список разрешений
        //Каждый вызов должен приводить к обновлению списка разрешений. Тк мы детач бдим, то скорее всего у нас могут появиться новые устройства
        //нужно сравнивать списки и пересортировывать.
       return manager.getDeviceList();
    }

    /**
     * Запрос диалога доступа к USB устройству и его открытия.
     * Можно использовать в коде если обноружено отсутствие доступ
     * Вызовет системный диалог, а значит окно получит onPause а потом onResume!!!! Это нужно учитывать!!
     *
     * @param device
     */
    public void requestPermission(UsbDevice device)
    {

        this.manager.requestPermission(device, mPermissionIntent);
    }




    @Override
    public void onReceive(Context context, Intent intent)
    {

        String action = intent.getAction();
        if (ACTION_USB_PERMISSION.equals(action))
        {
            synchronized (this)
            {
                UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false))
                {
                    if(device != null)
                    {
                        Log.v("Открытие устройства из onReceive");
                        this.openConnection(device,0);
                        Log.v( "permission ACCESS for device " + device);
                        //BiotestToast.makeMessageShow(ctx, "Разрешено USB", Toast.LENGTH_SHORT, BiotestToast.NORMAL_MESSAGE);


                        for(IUsbEventHandler evh:this.listEventHandler)evh.onPermission(device,true);

                    }
                }
                else
                {

                    BiotestToast.makeMessageShow(ctx, "Не разрешено USB", Toast.LENGTH_SHORT, BiotestToast.WARNING_MESSAGE);
                    Log.v("permission denied for device " + device);
                    for(IUsbEventHandler evh:this.listEventHandler)evh.onPermission(device,false);
                }
            }
        }

        if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
            synchronized (this)
            {
                UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null) {
                    Log.v("DEVICE DETACH " + device);
                    this.closeConnection(device);
                    for(IUsbEventHandler evh:this.listEventHandler)evh.onDetach(device);
                    BiotestToast.makeMessageShow(ctx, "Устройство USB отсоеденино.", Toast.LENGTH_SHORT,BiotestToast.NORMAL_MESSAGE);
                }
             }
        }

      /*
      if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            synchronized (this)
            {
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                if (device != null) {
                    Log.v(  "DEVICE ATTACH " + device);
                }
            }
        }
        */



    }

}
