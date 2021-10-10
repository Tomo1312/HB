package com.example.hogwartsbattle.CustomDialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hogwartsbattle.Model.Card;
import com.example.hogwartsbattle.R;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class ShowCardDialog {
    public static ShowCardDialog mDialog;

    public static ShowCardDialog getInstance() {
        if (mDialog == null)
            mDialog = new ShowCardDialog();
        return mDialog;
    }

    public void showCardDialog(Context context, Card cardToShow, String title) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout parent = new LinearLayout(context);
        parent.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        parent.setOrientation(LinearLayout.VERTICAL);

        if (title != null) {
            TextView heading = new TextView(context);
            ViewGroup.LayoutParams tvParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            heading.setLayoutParams(tvParams);
            heading.setGravity(View.TEXT_ALIGNMENT_CENTER);

            heading.setText(title);
            heading.setTextSize(26);
            heading.setTextColor(Color.parseColor("#000000"));
            parent.addView(heading);
        }

        // Set an image for ImageView
        ImageView iv = new ImageView(context);
        String cardName = "id" + cardToShow.getId();
        int id = context.getResources().getIdentifier("drawable/" + cardName, null, context.getPackageName());
        iv.setImageResource(id);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.width = 800;
        lp.height = 1354;
        iv.setLayoutParams(lp);
        parent.addView(iv);

        dialog.setContentView(parent);

        dialog.setCancelable(false);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

//        TimerTask task = new TimerTask() {
//            public void run() {
//
//            }
//        };
//        Timer timer = new Timer("Timer");
//
//        long delay = 1000L;
//        timer.schedule(task, delay * 2);
        try {
            TimeUnit.MILLISECONDS.sleep(2500 );
            dialog.dismiss();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
