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

        initGame1();
        doGame1();

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
        Sensor currentSensor = SensorsChair.getInstance().getSensor(sensorCode);
        switch(sensorCode)
        {
            case SITTING_BONE_SENSOR_NAME:
            case ARM:
            case FEET_SENSOR_NAME:
            case  LOWER_BACK_SENSOR_NAME:
            case UPPER_BACK_SENSOR_NAME:
                if(currentSensor.isSitting() && avatarIsUp)
                {
                    MoveAvatarDown();
                }
                else if(!currentSensor.isSitting() && !avatarIsUp)
                {
                    MoveAvatarUp();
                }
                break;
        }
    }


    private void MoveAvatarUp() {
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
        if (avatarIsUp) {
            TranslateAnimation moveDown = new TranslateAnimation(0, 0, -200, 0);
            moveDown.setDuration(1000);
            moveDown.setFillAfter(true);
            imageView.startAnimation(moveDown);
        }

        if(game1state==4 && situpIsGood)
        {
            situpIsGood = false;
            imageView.setImageResource(R.drawable.androidr);
        }
        else
        {
            imageView.setImageResource(R.drawable.android);
        }
        avatarIsUp = false;
    }


    private  View.OnClickListener myListener =  new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if(!avatarIsUp)
            {
                //Toast.makeText(getApplicationContext(), "Up", Toast.LENGTH_SHORT).show();
                MoveAvatarUp();
                //Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();

            } else {
                //Toast.makeText(getApplicationContext(), "Down", Toast.LENGTH_SHORT).show();
                MoveAvatarDown();
            }
        }
    };

    private void initGame1() {
        //currentBallNum = numBalls;
        game1state=0;
        scoreText.setText("SitUpsLeft=" + currentBallNum);
    }

    private void doGame1() {

        final long duration1 = 50000/speedLevel;
        if (game1state==0 && currentBallNum>=0) {
            game1state = 1;
            TranslateAnimation moveLeft1 = new TranslateAnimation(0, -400, 0, 0);
            moveLeft1.setDuration(duration1);
            moveLeft1.setFillAfter(true);

            moveLeft1.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation a) {
                    //Log.v("anim","---- animation start listener called"  );
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                           Toast goUpToast =  Toast.makeText(getActivity(), "Be careful !", Toast.LENGTH_SHORT);
                            goUpToast.setGravity(Gravity.BOTTOM, 200, 200);
                            goUpToast.show();
                        }
                    }, (long)(duration1 * 0.8));
                }

                public void onAnimationRepeat(Animation a) {
                    //Log.v("anim","---- animation repeat listener called"  );
                }

                public void onAnimationEnd(Animation a) {
                    //Log.v("anim", "---- animation end listener called");
                    game1state=2;
                    doGame1();
                }
            });
            ballImage.startAnimation(moveLeft1);
            return;
        }
        if (game1state==2) {
            game1state = 3;
            //imageView.setImageResource(R.drawable.handsup);
            TranslateAnimation moveLeft1 = new TranslateAnimation(-400, -800, 0, 0);
            moveLeft1.setDuration(50000/speedLevel);
            moveLeft1.setFillAfter(true);

            moveLeft1.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation a) {
                    //Log.v("anim","---- animation start listener called"  );
                    game1state = 4;
                    if (!avatarIsUp) {
                        situpIsGood = false;
                        imageView.setImageResource(R.drawable.androidr);

                        ballImage.clearAnimation();
                        /*

                        */
                    }
                }

                public void onAnimationRepeat(Animation a) {
                }

                public void onAnimationEnd(Animation a) {
                    //Log.v("anim", "---- animation end listener called");
                    if (!situpIsGood) {
                        //imageView.setImageResource(R.drawable.android);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageResource(R.drawable.android);
                                game1state=0;
                                situpIsGood=true;
                                doGame1();
                            }
                        }, 300);
                        return;
                    }
                    game1state=5;
                    doGame1();
                }
            });
            ballImage.startAnimation(moveLeft1);
            return;
        }

        if (game1state==5) {
            if (situpIsGood)
                currentBallNum--;
            if (currentBallNum>=0) {
                //initGame1();
                game1state=0;
                situpIsGood=true;
                scoreText.setText("SitUpsLeft=" + currentBallNum);
                doGame1();
            } else {
                scoreText.setText("Good Job!");
            }
        }
    }
}
