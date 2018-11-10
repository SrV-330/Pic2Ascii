package com.wsf.pic2ascii.utils;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;

public class CommUtil {


    private static final String base = "#8XOHLTI)i=+;:,.";
    public static Bitmap createAsciiPic(Bitmap image, Context context){


        WindowManager wm=(WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics dm=new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        int width0=image.getWidth();
        int height0=image.getHeight();

        int width1=0,height1=0;
        int scale=7;

        if(width0<=width/scale){
            width1=width0;
            height1=height0;
        }else {
            width1=width/scale;
            height1=width1/width0*height0;
        }

        image=scale(image,width1,height1);

        StringBuffer sb=new StringBuffer();
        for (int j = 0; j < image.getHeight(); j=j+2) {


            for (int i = 0; i <image.getWidth() ; i++) {

                int pixel=image.getPixel(i,j);
                int r=(pixel&0xff0000)>>16;
                int b=(pixel&0x00ff00)>>8;
                int g=(pixel&0x0000ff);
                float gray = 0.299f * r + 0.578f * g + 0.114f * b;
                int index=Math.round(gray*(base.length()+1)/255);
                String s=index>=base.length()? " ":String.valueOf(base.charAt(index));
                sb.append(s);
            }
            sb.append("\n");
        }

        return textToBitmap(sb,context);
    }
    public static Bitmap createAsciiPic(final String path, Context context){

        Bitmap image=BitmapFactory.decodeFile(path);
        WindowManager wm=(WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics dm=new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        int width0=image.getWidth();
        int height0=image.getHeight();

        int width1=0,height1=0;
        int scale=7;

        if(width0<=width/scale){
            width1=width0;
            height1=height0;
        }else {
            width1=width/scale;
            height1=width1*height0/width0;
        }

        image=scale(image,width1,height1);

        StringBuffer sb=new StringBuffer();
        for (int j = 0; j < image.getHeight(); j=j+2) {


            for (int i = 0; i <image.getWidth() ; i++) {

                int pixel=image.getPixel(i,j);
                int r=(pixel&0xff0000)>>16;
                int b=(pixel&0x00ff00)>>8;
                int g=(pixel&0x0000ff);
                float gray = 0.299f * r + 0.578f * g + 0.114f * b;
                int index=Math.round(gray*(base.length()+1)/255);
                String s=index>=base.length()? " ":String.valueOf(base.charAt(index));
                sb.append(s);
            }
            sb.append("\n");
        }

        return textToBitmap(sb,context);
    }
    private static Bitmap scale(String src,int newWidth,int newHeight){
        Bitmap ret=Bitmap.createScaledBitmap(BitmapFactory.decodeFile(src),newWidth,newHeight,true);
        return ret;

    }
    private static Bitmap scale(Bitmap src,int newWidth,int newHeight){
        Bitmap ret=Bitmap.createScaledBitmap(src,newWidth,newHeight,true);
        return ret;

    }
    private static Bitmap textToBitmap(StringBuffer text,Context context){

        TextPaint textPaint=new TextPaint();
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(12f);
        textPaint.setTypeface(Typeface.MONOSPACE);

        WindowManager wm=(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm=new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        StaticLayout sl=new StaticLayout(
                    text,
                    textPaint,
                    width,
                    StaticLayout.Alignment.ALIGN_CENTER,
                    1f,
                    0.0f,
                    true
                    );
        Bitmap bitmap=Bitmap.createBitmap(sl.getWidth()+20,sl.getHeight()+20,Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);
        canvas.translate(10,10);
        canvas.drawColor(Color.WHITE);
        sl.draw(canvas);



        return bitmap;
    }

    public static void choosePhoto(Activity context,int requestCode){
        PictureSelector.create(context)
                .openGallery(PictureMimeType.ofAll())
                .maxSelectNum(1)
                .imageSpanCount(4)
                .selectionMode(PictureConfig.SINGLE)
                .isCamera(true)
                .imageFormat(PictureMimeType.PNG)
                .isZoomAnim(true)
                .sizeMultiplier(0.5f)
                .openClickSound(true)
                .minimumCompressSize(500)
                .forResult(requestCode);

    }

}
