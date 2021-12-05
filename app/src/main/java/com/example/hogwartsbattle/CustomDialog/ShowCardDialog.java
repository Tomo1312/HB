package com.example.hogwartsbattle.CustomDialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;

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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void showCardDialog(Context context, Card cardToShow, String title) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout parent = new LinearLayout(context);
        parent.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setBackgroundResource(R.color.background);

        if (title != null) {
            TextView heading = new TextView(context);
            ViewGroup.LayoutParams tvParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            heading.setLayoutParams(tvParams);
            heading.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            heading.setText(title);
            Typeface face = ResourcesCompat.getFont(context, R.font.lumos);
            heading.setTypeface(face);
            heading.setTextSize(18);
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
        if (title != null && !cardToShow.getCardType().equals("hex")){
            dialog.setCancelable(false);}
        else
            dialog.setCancelable(true);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
        Timer timer = new Timer("Timer");

        TimerTask task = new TimerTask() {
            public void run() {
                dialog.dismiss();
                mDialog = null;
                timer.cancel();
            }
        };
        long delay = 2000L;

        if (title != null) {
            if (!cardToShow.getCardType().equals("hex"))
                timer.schedule(task, delay * 2);
        }
    }
}
