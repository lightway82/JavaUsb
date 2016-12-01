package org.anantacreative.javausb.USB;

import java.util.ArrayList;
import java.util.List;

/**
 * Методы для байтовых операций
 * Created by Ananta on 30.11.2016.
 */
public class ByteHelper {
    public  enum ByteOrder{SMALL_TO_BIG,BIG_TO_SMALL}


    /**
     * int в байтовый массив с указаным порядком байт
     * @param value
     * @param order
     * @return
     */
    public static final byte[] intToByteArray(int value, ByteOrder order ) {
        if(order==ByteOrder.BIG_TO_SMALL)
        {
            return new byte[]{
                    (byte) value,
                    (byte) (value >>> 8),
                    (byte) (value >>> 16),
                    (byte) (value >>> 24)
            };
        } else {
            return new byte[] {
                    (byte)(value >>> 24),
                    (byte)(value >>> 16),
                    (byte)(value >>> 8),
                    (byte)value
            };
        }

    }

    /**
     * int в список байт с заданным порядком
     * @param value
     * @param order
     * @return
     */
    public static  List<Byte> intToByteList(int value, ByteOrder order ) {

        List<Byte> res=new ArrayList<>();
        if(order==ByteOrder.BIG_TO_SMALL){
            res.add((byte)(value >>> 24));
            res.add((byte)(value >>> 16));
            res.add((byte)(value >>> 8));
            res.add((byte)value);
        }else {
            res.add((byte)value);
            res.add((byte)(value >>> 8));
            res.add((byte)(value >>> 16));
            res.add((byte)(value >>> 24));

        }
        return res;

    }


    /**
     * Преоюразует 4 элемента начиная с startPos в int
     * @param val
     * @param startPos
     * @param order
     * @return
     * @throws Exception
     */
    public static int byteArray4ToInt(byte[] val, int startPos, ByteOrder order) throws Exception{
        return byteArrayToInt(val,startPos,startPos+3,order);
    }

    /**
     * Преоюразует 3 элемента начиная с startPos в int
     * @param val
     * @param startPos
     * @param order
     * @return
     * @throws Exception
     */
    public static int byteArray3ToInt(byte[] val, int startPos, ByteOrder order) throws Exception{
        return byteArrayToInt(val,startPos,startPos+2,order);
    }

    /**
     * Преоюразует 2 элемента начиная с startPos в int
     * @param val
     * @param startPos
     * @param order
     * @return
     * @throws Exception
     */
    public static int byteArray2ToInt(byte[] val, int startPos, ByteOrder order) throws Exception{
        return byteArrayToInt(val,startPos,startPos+1,order);
    }

    /**
     * Преоюразует 1 элемент  в позиции startPos в int
     * @param val
     * @param startPos
     *
     * @return
     * @throws Exception
     */
    public static int byteArray1ToInt(byte[] val, int startPos) throws Exception{
        return (val[startPos] & 0xFF);
    }

    /**
     * Преобразует в int до 4  байтов
     * @param val
     * @param order порядок байт в массиве
     *  @param startPos начальный элемент массива
     *  @param stopPos конечный элемент массива, может быть равен startPos, тогда будет обрабатываться один байт
     * @return
     */
    public static int byteArrayToInt(byte[] val, int startPos,int stopPos, ByteOrder order) throws Exception {

        int len = stopPos-startPos+1;
        if(len > 4) throw new Exception("Массив должен иметь не более 4 элементов");
        else if(len <= 0) throw new Exception("startPos должен быть меньше stopPos");
        else if(startPos >=val.length || startPos < 0)throw new Exception("startPos должен в пределах индексов массива");
        else if(stopPos >=val.length || startPos < 0)throw new Exception("stopPos должен в пределах индексов массива");

        if(order==ByteOrder.BIG_TO_SMALL) {
            if(len==4) return  ((val[startPos] & 0xFF) << 24) + ((val[startPos+1] & 0xFF) << 16) + ((val[startPos+2] & 0xFF) << 8) + (val[startPos+3] & 0xFF);
            else if(len==3) return    ((val[startPos+1] & 0xFF) << 16) + ((val[startPos+2] & 0xFF) << 8) + (val[startPos+3] & 0xFF);
            else if(len==2) return     ((val[startPos+2] & 0xFF) << 8) + (val[startPos+3] & 0xFF);
            else if(len==1) return    (val[startPos+3] & 0xFF);
            else throw new Exception("Отрезок массива должен быть не более 4 элементов и не менее 1");
        }else {

            if(len==4) return   ((val[startPos+3] & 0xFF) << 24) + ((val[startPos+2] & 0xFF) << 16) + ((val[startPos+1] & 0xFF) << 8) + (val[startPos] & 0xFF);
            else if(len==3)return    ((val[startPos+2] & 0xFF) << 16) + ((val[startPos+1] & 0xFF) << 8) + (val[startPos] & 0xFF);
            else if(len==2) return    ((val[startPos+1] & 0xFF) << 8) + (val[startPos] & 0xFF);
            else if(len==1) return     (val[startPos] & 0xFF);
            else throw new Exception("Отрезок массив должен быть не более 4 элементов и не менее 1");
        }
    }


    /**
     * Преобразует в int до 4  байтов массива, начиная с начала массива
     * @param val
     * @param order порядок байт в массиве
     * @return
     */
    public static int byteArrayToInt(byte[] val, ByteOrder order) throws Exception {

        if(val.length>4) throw new Exception("Массив должен иметь не более 4 элементов");
        if(order==ByteOrder.BIG_TO_SMALL) {
            if(val.length==4) return  ((val[0] & 0xFF) << 24) + ((val[1] & 0xFF) << 16) + ((val[2] & 0xFF) << 8) + (val[3] & 0xFF);
            else if(val.length==3) return    ((val[1] & 0xFF) << 16) + ((val[2] & 0xFF) << 8) + (val[3] & 0xFF);
            else if(val.length==2) return     ((val[2] & 0xFF) << 8) + (val[3] & 0xFF);
            else if(val.length==1) return    (val[3] & 0xFF);
            else throw new Exception("Массив должен иметь не более 4 элементов и не менее 1");
        }else {

            if(val.length==4) return   ((val[3] & 0xFF) << 24) + ((val[2] & 0xFF) << 16) + ((val[1] & 0xFF) << 8) + (val[0] & 0xFF);
            else if(val.length==3)return    ((val[2] & 0xFF) << 16) + ((val[1] & 0xFF) << 8) + (val[0] & 0xFF);
            else if(val.length==2) return    ((val[1] & 0xFF) << 8) + (val[0] & 0xFF);
            else if(val.length==1) return     (val[0] & 0xFF);
            else throw new Exception("Массив должен иметь не более 4 элементов и не менее 1");
        }
    }

    /**
     * Преобразует байтовый массив в строку
     * @param array байтовый массив
     * @param byteInLine сколько байт на линию в строке, до переноса
     * @param delmitter разделитель между байтами
     * @return
     */
    public static String bytesToHex(byte[] array,int byteInLine, char delmitter)
    {

        int lineDelmitterNum = array.length/byteInLine;
        char[] val = new char[3*array.length+lineDelmitterNum];
        String hex = "0123456789ABCDEF";
        int lineDelmitterCounter=0;
        for (int i = 0; i < array.length; i++)
        {
            int b = array[i] & 0xff;
            val[3*i+lineDelmitterCounter] = hex.charAt(b >>> 4);
            val[3*i + 1+lineDelmitterCounter] = hex.charAt(b & 15);
            val[3*i + 2+lineDelmitterCounter] =delmitter;
            if( byteInLine*(lineDelmitterCounter+1) == (i+1) ) {
                val[3*i + 2+lineDelmitterCounter+1]='\n';
                lineDelmitterCounter++;
            }
        }
        return String.valueOf(val);
    }
}
