package com.example.hogwartsbattle.CustomDialog;

import android.content.Context;

import com.example.hogwartsbattle.Interface.IOwnAllyListener;

public abstract class CustomDialog {

    Context context;
    int position;

    public CustomDialog(Context context, int position){
        this.context = context;
        this.position = position;

    }
    public abstract void showDialog();

}
