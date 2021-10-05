package com.kehzabr.boutique;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Environment.getExternalStorageDirectory;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class FirstFragment extends Fragment {

    private TextView startTV, stopTV, playTV, stopplayTV;
    private MediaRecorder recorder = null;
    private static String fileName = null;
    private static String mFileName = null;
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private StorageReference mStorage;
    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    TextView recordingStatus;
    Button record;
    private ProgressDialog mProgress;
    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    Context mContext;
    private MediaPlayer player = null;
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

            }
        };
        mAuth.signInAnonymously()
                .addOnCompleteListener(task -> {
                    Log.d(TAG, "Auth : " + task.isSuccessful());
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Failed : ", task.getException());
                        Toast.makeText(mContext, "Login failed.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                    }

                });
        // Inflate the layout for this fragment
        //  fileName = getActivity().getExternalCacheDir().getAbsolutePath();
        fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        fileName += "/audiorecordtest.3gp";


        mStorage = FirebaseStorage.getInstance().getReference();
        ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        mProgress = new ProgressDialog(getActivity());
        return inflater.inflate(R.layout.fragment_first, container, false);


    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startTV = view.findViewById(R.id.btnRecord);
        stopTV = view.findViewById(R.id.btnStop);
        playTV = view.findViewById(R.id.btnPlay);
        stopplayTV = view.findViewById(R.id.btnStopPlay);
        record = view.findViewById(R.id.record);
        recordingStatus = view.findViewById(R.id.record_status);
        startTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start recording method will start the recording of audio.
                startRecording();
            }
        });
        stopTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pause Recording method will pause the recording of audio.
                pauseRecording();
                uploadAudio();


            }
        });
        playTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // play audio method will play the audio which we have recorded
                playAudio();
            }
        });
        stopplayTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pause play method will pause the play of audio
                pausePlaying();
            }
        });


        record.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startRecording();
                    recordingStatus.setText("Recording Started....");

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    stopRecording();
                    recordingStatus.setText("Recording Stopped....");

                }
                return false;
            }
        });
        view.findViewById(R.id.record).setOnClickListener(new View.OnClickListener() {

            boolean mStartRecording = true;

            @Override
            public void onClick(View view) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    recordingStatus.setText("Stop Recording");
                    //stop Recording
                } else {
                    //Start Recording
                }
                mStartRecording = !mStartRecording;
            }
        });

        view.findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {

            boolean mStartPlaying = true;

            @Override
            public void onClick(View view) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    recordingStatus.setText("Stop Recording");
                    //stop Recording
                } else {
                    //Start Recording
                }
                mStartPlaying = !mStartPlaying;
            }
        });
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        player.release();
        player = null;
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
        uploadAudio();
    }

    private void startRecordingold() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();
    }

    private void startRecording() {
        stopTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
        mFileName = Environment.getExternalStorageDirectory() + File.separator
                + Environment.DIRECTORY_DCIM + File.separator + "audio.3gp";
        mRecorder = new MediaRecorder();
        mRecorder.reset();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //below method is used to set the output format of the audio.
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        //below method is used to set the audio encoder for our recorded audio.
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //below method is used to set the output file location for our recorded audio
        mRecorder.setOutputFile(mFileName);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e("TAG", "prepare() failed");
        }
        // start method will start the audio recording.
        mRecorder.start();

    }


    public void playAudio() {
        startTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
        stopplayTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
        //for playing our recorded audio we are using media player class.
        mPlayer = new MediaPlayer();
        try {
            //below method is used to set the data source which will be our file name
            mPlayer.setDataSource(mFileName);
            //below method will prepare our media player
            mPlayer.prepare();
            //below method will start our media player.
            mPlayer.start();
        } catch (IOException e) {
            Log.e("TAG", "prepare() failed");
        }


    }

    public void pauseRecording() {
        startTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
        playTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
        stopplayTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
        //below method will stop the audio recording.
        mRecorder.stop();
        //below method will release the media recorder class.
        mRecorder.release();
        mRecorder = null;

    }

    public void pausePlaying() {
        //this method will release the media player class and pause the playing of our recorded audio.
        mPlayer.release();
        mPlayer = null;
        startTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
        playTV.setBackgroundColor(getResources().getColor(R.color.purple_200));

    }

    private void uploadAudio() {
        mProgress.setMessage("Uploading Audio.....");
        mProgress.show();
        StorageReference filepath = mStorage.child("Audio").child("1234.3gp");
        Uri uri = Uri.fromFile(new File(mFileName));
        filepath.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            mProgress.dismiss();
            recordingStatus.setText("Uploading Finished");

        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted)

            getActivity().finish();

    }

}