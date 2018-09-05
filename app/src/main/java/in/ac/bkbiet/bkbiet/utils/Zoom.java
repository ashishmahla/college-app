package in.ac.bkbiet.bkbiet.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;

public class Zoom extends View {

    private Drawable image;
    private int zoomController = 20;

    public Zoom(Context context, Drawable image) {
        super(context);
        this.image = image;
        //image=context.getResources().getDrawable(R.drawable.icon);

        setFocusable(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //here u can control the width and height of the images........ this line is very important
        image.setBounds((getWidth() / 2) - zoomController, (getHeight() / 2) - zoomController, (getWidth() / 2) + zoomController, (getHeight() / 2) + zoomController);
        image.draw(canvas);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            // zoom in
            zoomController += 10;
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            // zoom out
            zoomController -= 10;
        }
        if (zoomController < 10) {
            zoomController = 10;
        }

        invalidate();
        return true;
    }
}