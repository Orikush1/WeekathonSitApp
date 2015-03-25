package team4.weekathon.com.sitapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import rx.Subscriber;
import rx.Subscription;
import rx.android.observables.AndroidObservable;

/**
 * Created by Ori on 3/24/2015.
 */
public class StaticExerciseActivity extends Activity{

    private ImageView balloonView = null;
    private ImageView arrowView = null;

    private int arrowViewPosition = 10;
    private Subscription updateSubscription;
    private boolean firstTimeOfUse;


    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_hand_exercise);

        firstTimeOfUse = true;
        balloonView = (ImageView) findViewById(R.id.balloon1);
        arrowView = (ImageView) findViewById(R.id.arrow);


        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.game2);

        balloonView.setOnClickListener(myListener);

        rx.Observable<SensorsChair.SENSORS_CODE_LIST> updateSensorUI = SensorsChair.getInstance().getUpdateNotifier();
        updateSensorUI =  AndroidObservable.bindActivity(this, updateSensorUI);
        updateSubscription = updateSensorUI.subscribe(new Subscriber<SensorsChair.SENSORS_CODE_LIST>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(SensorsChair.SENSORS_CODE_LIST sensorCode)
            {
                updateSensorUI(sensorCode);
            }
        });

    }

    private void updateSensorUI(SensorsChair.SENSORS_CODE_LIST sensorCode)
    {
        if(sensorCode == SensorsChair.SENSORS_CODE_LIST.HAND_POWER)
        {
            Sensor handPowerSensor = SensorsChair.getInstance().getSensor(SensorsChair.SENSORS_CODE_LIST.HAND_POWER);

            MoveArrow2(((HandPowerSensor) handPowerSensor).getPreviousValue());

        }
    }

    private void MoveArrow2(int value)
    {
       /* if (value < 340)
            value=320;
        if (value > 355)
            value=355;
        float a = -1000/(355-320);
        float b = -320*a;
        MoveArrow((int)(a*value+b));*/

        if(value > 355)
        {
            MoveArrow(arrowViewPosition-100);
        }
    }

    private void MoveArrow(int NewPosition)
    {
        // convert into bounds [320,355] --> [0:-1000]
        Log.i("anim", arrowViewPosition + "-->" + NewPosition);
        TranslateAnimation anim = new TranslateAnimation(10, 0, arrowViewPosition, arrowViewPosition-100);
        arrowViewPosition=NewPosition;
        anim.setDuration(10);
        anim.setFillAfter(true);
        arrowView.startAnimation(anim);
        if(NewPosition < -900)
        {
            popBalloon();
        }
    }


    private void popBalloon()
    {
       balloonView.setImageResource(R.drawable.pop);
       MoveArrow(0);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                balloonView.setImageResource(R.drawable.balloon);
            }
        }, 200);
    }

    private  View.OnClickListener myListener =  new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            MoveArrow(arrowViewPosition-100);
            /*
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams
                    (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lp.setMargins(0, 0, 0, 0);
            arrowView.setLayoutParams(lp);
            */

            //TranslateAnimation anim = new TranslateAnimation(10, 0, arrowViewPosition, arrowViewPosition-100);


        }
    };
}
