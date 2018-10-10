package osdnk.mobilization_panroratezoom;

import android.annotation.SuppressLint;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class MainActivity extends AppCompatActivity {

  ImageView mImage;
  float scaleDiff;
  private static final int NONE = 0;
  private static final int DRAG = 1;
  private static final int ZOOM = 2;
  private int mode = NONE;
  private float oldDist = 1f;
  private float d = 0f;
  private float newRot = 0f;

  @SuppressLint("ClickableViewAccessibility")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    init();


    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(250, 250);
    layoutParams.leftMargin = 50;
    layoutParams.topMargin = 50;
    layoutParams.bottomMargin = -250;
    layoutParams.rightMargin = -250;
    mImage.setLayoutParams(layoutParams);

    mImage.setOnTouchListener(new View.OnTouchListener() {

      RelativeLayout.LayoutParams params;
      int startWidth;
      int startHeight;
      float dx = 0, dy = 0, x = 0, y = 0;
      float angle = 0;

      @Override
      public boolean onTouch(View v, MotionEvent event) {
        final ImageView view = (ImageView) v;
        ((BitmapDrawable) view.getDrawable()).setAntiAlias(true);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
          case MotionEvent.ACTION_DOWN:
            params = (RelativeLayout.LayoutParams) view.getLayoutParams();
            startWidth = params.width;
            startHeight = params.height;
            dx = event.getRawX() - params.leftMargin;
            dy = event.getRawY() - params.topMargin;
            mode = DRAG;
            break;

          case MotionEvent.ACTION_POINTER_DOWN:
            oldDist = spacing(event);
            if (oldDist > 10f) { mode = ZOOM; }
            d = rotation(event);
            break;

          case MotionEvent.ACTION_UP:
            break;

          case MotionEvent.ACTION_POINTER_UP:
            mode = NONE;
            break;

          case MotionEvent.ACTION_MOVE:
            if (mode == DRAG) {
              x = event.getRawX();
              y = event.getRawY();
              params.leftMargin = (int) (x - dx);
              params.topMargin = (int) (y - dy);
              params.rightMargin = params.leftMargin + (5 * params.width);
              params.bottomMargin = params.topMargin + (10 * params.height);
              view.setLayoutParams(params);
            } else if (mode == ZOOM) {
              if (event.getPointerCount() == 2) {
                newRot = rotation(event);
                angle = newRot - d;
                x = event.getRawX();
                y = event.getRawY();
                float newDist = spacing(event);
                if (newDist > 10f) {
                  float scale = newDist / oldDist * view.getScaleX();
                  if (scale > 0.6) {
                    scaleDiff = scale;
                    view.setScaleX(scale);
                    view.setScaleY(scale);
                  }
                }
                view.animate().rotationBy(angle).setDuration(0).setInterpolator(new LinearInterpolator()).start();
                x = event.getRawX();
                y = event.getRawY();
                params.leftMargin = (int) ((x - dx) + scaleDiff);
                params.topMargin = (int) ((y - dy) + scaleDiff);
                params.rightMargin = params.leftMargin + (5 * params.width);
                params.bottomMargin = params.topMargin + (10 * params.height);
                view.setLayoutParams(params);
              }
            }
        }
        return true;
      }
    });
  }

  private void init() {
    mImage = findViewById(R.id.im_move_zoom_rotate);
  }

  private float spacing(MotionEvent event) {
    float x = event.getX(0) - event.getX(1);
    float y = event.getY(0) - event.getY(1);
    return (float) Math.sqrt(x * x + y * y);
  }

  private float rotation(MotionEvent event) {
    double delta_x = (event.getX(0) - event.getX(1));
    double delta_y = (event.getY(0) - event.getY(1));
    double radians = Math.atan2(delta_y, delta_x);
    return (float) Math.toDegrees(radians);
  }
}

