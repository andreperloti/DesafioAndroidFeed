package br.com.developers.perloti.desafioandroidtechfit.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import br.com.developers.perloti.desafioandroidtechfit.R;

/**
 * Created by perloti on 10/05/18.
 */

public class CallbackRequestUtil {

    View viewLoading;
    View viewEE;
    Button buttonEE;
    TextView textViewEE;

    private MyListener myListener;

    public CallbackRequestUtil(Context context, MyListener myListener) {
        this.myListener = myListener;
        Activity activity = (Activity) context;
        viewEE = activity.findViewById(R.id.view_empty_error);
        buttonEE = activity.findViewById(R.id.button_empty_error);
        textViewEE = activity.findViewById(R.id.textview_empty_error);
        viewLoading = activity.findViewById(R.id.view_loading);
        callbackRequest.bindEE();
    }

    public interface MyListener {
        void onClick();
    }

    public CallbackRequestTN getCallbackRequestTN(){
        return this.callbackRequest;
    }

    private CallbackRequestTN callbackRequest = new CallbackRequestTN() {
        @Override
        public void empty() {
            Log.e(TNUtil.TN, "list empty");
            viewEE.setVisibility(View.VISIBLE);
            buttonEE.setVisibility(View.VISIBLE);
            textViewEE.setVisibility(View.VISIBLE);
            viewLoading.setVisibility(View.GONE);
            textViewEE.setText(R.string.no_results_for_filter);
            TNUtil.toastLong(ApplicationUtil.getContext().getString(R.string.no_results_for_filter));
        }

        @Override
        public void error() {
            Log.e(TNUtil.TN, "erro loading list");
            viewEE.setVisibility(View.VISIBLE);
            buttonEE.setVisibility(View.VISIBLE);
            textViewEE.setVisibility(View.VISIBLE);
            viewLoading.setVisibility(View.GONE);
            textViewEE.setText(R.string.error_try_again_later);
            TNUtil.toastLong(ApplicationUtil.getContext().getString(R.string.error_try_again_later));
        }

        @Override
        public void success() {
            Log.d(TNUtil.TN, "success loading list");
            viewLoading.setVisibility(View.GONE);
            viewEE.setVisibility(View.GONE);
        }

        @Override
        public void bindEE() {
            viewLoading.setVisibility(View.GONE);
            textViewEE.setVisibility(View.GONE);
            viewEE.setVisibility(View.GONE);
            buttonEE.setVisibility(View.GONE);
            buttonEE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewLoading.setVisibility(View.VISIBLE);
                    getMyListener().onClick();
                }
            });
        }

    };

    private MyListener getMyListener() {
        return myListener;
    }
}
