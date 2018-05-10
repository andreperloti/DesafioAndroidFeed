package br.com.developers.perloti.desafioandroidtechfit.util;

import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import br.com.developers.perloti.desafioandroidtechfit.R;

/**
 * Created by perloti on 08/05/18.
 */

public class TNUtil {

    public static final String TNREQUEST = "TN-Request";
    public static final String TN = "TN-Current";
    public static final String KEY_FEEDHASH = "FEED_HASH";
    public static final String KEY_IDPROFILE = "ID_PROFILE";


    public static void toastLong(String s) {
        Toast.makeText(ApplicationUtil.getContext(), s, Toast.LENGTH_LONG).show();
    }


}
