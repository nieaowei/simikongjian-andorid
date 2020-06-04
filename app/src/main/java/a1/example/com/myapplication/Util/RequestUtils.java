package a1.example.com.myapplication.Util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

public class RequestUtils {

        /**
         * 获取的读取流转为String返回
         *
         * @param:inputStream
         */
        public static String stremToString(InputStream in) {
            String result="";
            try {
                //创建一个字节数组写入流
                ByteArrayOutputStream out=new ByteArrayOutputStream();

                byte[] buffer=new byte[1024];
                int length=0;
                while((length=in.read(buffer))!=-1){  //如果返回-1 则表示数据读取完成了。
                    //int s=(int)Character.toUpperCase((char)length);
                    int s = (length >= 97 && length <= 122) ? length - 32 : length;
                    out.write(buffer,0,(char)s);//写入数据
                    out.flush();
                }
                result=out.toString();//写入流转为字符串
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

    public static Map<String, Object> getObjectToMap(Object obj) throws IllegalAccessException {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        Class<?> clazz = obj.getClass();
        System.out.println(clazz);
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = field.get(obj);
            if (value == null){
                value = "";
            }
            map.put(fieldName, value);
        }
        return map;
    }
    /**
     * InputStream转jsonarray
     * 此处在处理InputStream时所用的方法和上面一个函数不同，只是多列出一个可用方法，实际使用时，请考虑一下效率问题
     * 至于转成jsonarray的笨办法只是由于个人需求，接口也是自己写的，有十足把握不会导致出错，各位看官莫怪，轻易不要这样干
     * @param is
     * @return
     */
    public static String stream2jarray(InputStream is) {
        if (is != null) {
            StringBuffer sb = new StringBuffer();
            byte[] b = new byte[4096];
            try {
                for(int n;(n=is.read(b))!=-1;){
                    sb.append(new String(b,0,n));
                }
                is.close();
                if(sb.charAt(0) == '{'){
                    return "["+sb.toString()+"]";
                }
                else{
                    return sb.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}
