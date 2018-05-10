package br.com.developers.perloti.desafioandroidtechfit.util;

import android.content.Context;

/**
 * Created by perloti on 08/05/18.
 */

public class ApplicationUtil {

    private static Context context;

    private ApplicationUtil(){
        super();
    }

    public static void  setContext(Context context){
        ApplicationUtil.context = context;
    }

    public static Context getContext(){
        return ApplicationUtil.context;
    }


}
