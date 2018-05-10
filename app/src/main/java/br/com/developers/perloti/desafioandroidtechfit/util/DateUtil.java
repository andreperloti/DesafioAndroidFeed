package br.com.developers.perloti.desafioandroidtechfit.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import br.com.developers.perloti.desafioandroidtechfit.R;

/**
 * Created by perloti on 08/05/18.
 */

public class DateUtil {


    public static Date stringToDate(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static Date stringToDateFeed(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static String dateToStringMask(Date date) {
        String convertedDate = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(ApplicationUtil.getContext().getString(R.string.date_mask));
        try {
            convertedDate = dateFormat.format(date);
        } catch (Exception e) {
            Log.i("ERRO", "ERRO AO CONVERTER DATA PARA STRING");
        }
        return convertedDate;
    }



}

