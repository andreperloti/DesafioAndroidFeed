package br.com.developers.perloti.desafioandroidtechfit.model;

import android.app.Application;

import br.com.developers.perloti.desafioandroidtechfit.R;
import br.com.developers.perloti.desafioandroidtechfit.util.ApplicationUtil;

/**
 * Created by perloti on 08/05/18.
 */

public enum Meal {

    breakfast,
    morningSnack,
    lunch,
    afternoonSnack,
    dinner,
    nightSnack,
    preTraining,
    postTraining,
    ;


    public static Meal getMeal(int ordinal) {
        return values()[ordinal];
    }

    public int getResName() {
        return ApplicationUtil.getContext().getResources().getIdentifier(
                "meal_" + ordinal(), "string",
                ApplicationUtil.getContext().getPackageName());
    }

    @Override
    public String toString() {
        return ApplicationUtil.getContext().getString(getResName());
    }

}
