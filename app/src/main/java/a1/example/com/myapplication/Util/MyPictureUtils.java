package a1.example.com.myapplication.Util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class MyPictureUtils {
      /*
      * 将base64转换成bitmap图片
      * */
    public static Bitmap stringtoBitmap(String string) {
        Bitmap bitmap = null;

        try{
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,bitmapArray.length);
        }catch (Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }
}
