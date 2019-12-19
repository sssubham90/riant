package com.riantservices.riant.models;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.riantservices.riant.R;

public class SOSView extends View {

    private int circleCol, labelCol;
    private String circleText;
    private Paint circlePaint;
    boolean isLongPress;

    public SOSView(Context context, AttributeSet attrs) {
        super(context, attrs);
        circlePaint = new Paint();
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SOSView, 0, 0);
        try {
            circleText = a.getString(R.styleable.SOSView_circleLabel);
            circleCol = a.getInteger(R.styleable.SOSView_circleColor, 0);//0 is default
            labelCol = a.getInteger(R.styleable.SOSView_labelColor, 0);
        } finally {
            a.recycle();
        }
        init();
    }

    private void init() {
        isLongPress = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int viewWidthHalf = this.getMeasuredWidth()/2;
        int viewHeightHalf = this.getMeasuredHeight()/2;
        int radius;
        if(viewWidthHalf>viewHeightHalf)
            radius=viewHeightHalf-10;
        else
            radius=viewWidthHalf-10;
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(circleCol);
        canvas.drawCircle(viewWidthHalf, viewHeightHalf, radius, circlePaint);
        circlePaint.setColor(labelCol);
        circlePaint.setTextAlign(Paint.Align.CENTER);
        circlePaint.setTextSize(50);
        canvas.drawText(circleText, viewWidthHalf, viewHeightHalf+15, circlePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        isLongPress=false;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                isLongPress = true;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(isLongPress)
                            performClick();
                    }
                }, 5000);
                break;
            case MotionEvent.ACTION_UP:
                isLongPress = false;
                break;
        }
        return isLongPress;
    }

    public void alertDialog(String Message) {

        new AlertDialog.Builder(getContext()).setTitle("Riant Alert").setMessage(Message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setIcon(android.R.drawable.ic_dialog_alert).show();
    }

    @Override
    public boolean performClick() {
        super.performClick();
        Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        if(vibrator!=null)
            vibrator.vibrate(200);
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:100"));
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            alertDialog("SOS activated. Calling 100.");
            getContext().getApplicationContext().startActivity(intent);
        } else alertDialog("SOS failed");
        return true;
    }

}
