package team4.weekathon.com.sitapp;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import rx.Subscriber;
import rx.android.observables.AndroidObservable;

/**
 * Created by Ori on 3/24/2015.
 */
public class HandsExercise extends Fragment {

    private ImageView imageView;
    private TextView scoreText;
    private ImageView ballImage;

    private boolean avatarIsUp = false;
    private int game1state = 0;
    private int numBalls = 8;
    private int currentBallNum=numBalls;
    private int speedLevel = 8;
    private int game1score=0;
    private boolean situpIsGood = true;
    private Handler crabEnterZoneHandler;
    private Handler crabExitZoneHandler;
    private Handler warningHandler;
    private boolean IsCrabInZone;
    private Toast goUpToast;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(R.layout.hands_exercise, container, false);
        imageView = (ImageView) rootView.findViewById(R.id.android);
        scoreText = (TextView)rootView.findViewById(R.id.hello);
        RelativeLayout mainLayout = (RelativeLayout) rootView.findViewById(R.id.game1);

        scoreText.setOnClickListener(myListener);

        ballImage = new ImageView(getActivity());
        //setting image resource
        ballImage.setImageResource(R.drawable.crab);
        //ballImage.setId(R.id.ball);
        ViewGroup.LayoutParams imageViewLayoutParams
                = new RelativeLayout.LayoutParams(90,90);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams
                (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp.height=100;
        lp.width=100;

        imageViewLayoutParams.height=100;
        imageViewLayoutParams.width=100;
        ballImage.setLayoutParams(lp);
        //ballImage.setLayoutParams(new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        mainLayout.addView(ballImage);

        scoreText.setText("SitUpsLeft=" + currentBallNum);
        startIteration();


        rx.Observable<SensorsChair.SENSORS_CODE_LIST> updateSensorUI = SensorsChair.getInstance().getUpdateNotifier();
        updateSensorUI =  AndroidObservable.bindFragment(this, updateSensorUI);
        updateSensorUI.subscribe(new Subscriber<SensorsChair.SENSORS_CODE_LIST>() {
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
        return rootView;
    }

    private void updateSensorUI(SensorsChair.SENSORS_CODE_LIST sensorCode)
    {
        if(sensorCode == SensorsChair.SENSORS_CODE_LIST.ARM || sensorCode == SensorsChair.SENSORS_CODE_LIST.SITTING_BONE_SENSOR_NAME)
        {
            Sensor armSensor = SensorsChair.getInstance().getSensor(SensorsChair.SENSORS_CODE_LIST.ARM);
            Sensor sittingSensor = SensorsChair.getInstance().getSensor(SensorsChair.SENSORS_CODE_LIST.SITTING_BONE_SENSOR_NAME);
            if(armSensor.isSitting() && sittingSensor.isSitting())
            {
                MoveAvatarDown();
            }
            else if(armSensor.isSitting() && !sittingSensor.isSitting())
            {
                MoveAvatarUp();
            }
        }
    }


    private void MoveAvatarUp()
    {
        if (!avatarIsUp) {
            TranslateAnimation moveUp = new TranslateAnimation(0, 0, 0, -200);
            moveUp.setDuration(1000);
            moveUp.setFillAfter(true);
            imageView.startAnimation(moveUp);
            avatarIsUp = true;
        }
        imageView.setImageResource(R.drawable.handsup);
    }

    private void MoveAvatarDown() {
        if (avatarIsUp)
        {
            TranslateAnimation moveDown = new TranslateAnimation(0, 0, -200, 0);
            moveDown.setDuration(1000);
            moveDown.setFillAfter(true);
            imageView.startAnimation(moveDown);
        }

        if(IsCrabInZone && situpIsGood)
        {
            Fail();
        }

        avatarIsUp = false;
    }


    private void Fail()
    {
        //cancel handlers
        crabEnterZoneHandler.removeCallbacks(null);
        warningHandler.removeCallbacks(null);
        crabExitZoneHandler.removeCallbacks(null);

        //cancel animation and red android
        imageView.setImageResource(R.drawable.androidr);
        ballImage.clearAnimation();
        situpIsGood = false;

    }


    private  View.OnClickListener myListener =  new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if(!avatarIsUp)
            {
                MoveAvatarUp();
            }
            else
            {
                MoveAvatarDown();
            }
        }
    };

    private void crabEnterZone()
    {
        IsCrabInZone = true;

        if(!avatarIsUp)
        {
            Fail();
        }

    }

    private void crabExitZone()
    {
        IsCrabInZone = false;
    }

    private void startIteration() {
        //Position crab
        //Start animation of crab
        //set timer to call crab enter red zone
        //
        final long duration1 = 50000 / speedLevel;
        if(avatarIsUp)
        {
            imageView.setImageResource(R.drawable.handsup);
        }
        else
        {
            imageView.setImageResource(R.drawable.android);
        }
        situpIsGood = true;
        IsCrabInZone = false;

        if (currentBallNum <= 0)
        {
            scoreText.setText("Good Job!");
            return;
        }

        TranslateAnimation moveLeft1 = new TranslateAnimation(0, -1000, 0, 0);
        moveLeft1.setDuration(duration1);
        moveLeft1.setFillAfter(true);

        moveLeft1.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation a) {
                //Log.v("anim","---- animation start listener called"  );
                warningHandler = new Handler();
                warningHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!avatarIsUp)
                        {
                            goUpToast = Toast.makeText(getActivity(), "Be careful !", Toast.LENGTH_SHORT);
                            goUpToast.setGravity(Gravity.BOTTOM, 200, 200);
                            goUpToast.show();
                        }
                    }
                }, (long)(duration1 * 0.3));

                crabEnterZoneHandler = new Handler();
                crabEnterZoneHandler.postDelayed(new Runnable() {
                    @Override
                    public void run()
                    {
                        crabEnterZone();
                    }
                }, (long)(duration1 * 0.47));

                crabExitZoneHandler = new Handler();
                crabExitZoneHandler.postDelayed(new Runnable() {
                    @Override
                    public void run()
                    {
                        crabExitZone();
                    }
                }, (long) (duration1 * 0.65));

            }

            public void onAnimationRepeat(Animation a) {
                //Log.v("anim","---- animation repeat listener called"  );
            }

            public void onAnimationEnd(Animation a)
            {
                if (situpIsGood)
                {
                    currentBallNum--;
                    scoreText.setText("SitUpsLeft =" + currentBallNum);
                }


                Handler animationEnd = new Handler();
                animationEnd.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startIteration();
                    }
                }, 300);
            }

        });
        ballImage.startAnimation(moveLeft1);
    }
}
