package com.senior_design.pal_device;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.UUID;

import static android.content.ContentValues.TAG;

/**
 * Created by Sarah on 4/1/2018.
 */

public class BluetoothService {
    private static final String TAG = "BluetoothChatService";

    // Name for the SDP record when creating server socket
    private static final String NAME_SECURE = "BluetoothChatSecure";
    private static final String NAME_INSECURE = "BluetoothChatInsecure";

    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    // Member fields
    final BluetoothAdapter mAdapter;
    final Handler mHandler;
    AcceptThread mSecureAcceptThread;
    ConnectedThread mConnectedThread;
    int mState;
    int mNewState;
    BluetoothDevice mmDevice;
    BluetoothSocket mmSocket;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    public BluetoothService(Handler handler, BluetoothDevice device) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mNewState = mState;
        mHandler = handler;
        mmDevice = device;
    }

    public synchronized void start() {

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to listen on a BluetoothServerSocket
        if (mSecureAcceptThread == null) {
            mSecureAcceptThread = new AcceptThread(mmDevice);
            mSecureAcceptThread.run();
        }

    }

    public synchronized HashMap<String,String> getPALData(final File musicFile, StorageReference mStorage){
        //Variable for the read
        int timesBelowThreshold = 0;
        int timesAboveThreshold = 0;
        boolean playingMusic = false;
        final int timeBelow = 5000;
        int minThreshold = 2;
        int timeBeforePlay = 1000;
        final MediaPlayer mPlayer = new MediaPlayer();
        HashMap<String, String> data_DBref = new HashMap<String,String>();


        //Verify that we have  a connected socket
        if(mSecureAcceptThread == null){
            return null;
        }

        //Gets the input and output streams from the PAL Device
        mConnectedThread = new ConnectedThread(mmSocket);

        //If the input/output stream aren't initialized properly
        if(mConnectedThread == null){
            stop();
        }

        //15 minutes is 900000 milliseconds
        //30 seconds is 30000
        //5 seconds is 5000 milliseconds
        //1 second is 1000 milliseconds
        long startTime = System.currentTimeMillis();
        while((System.currentTimeMillis()-startTime)< 30000){
            int input = mConnectedThread.read();

            //input from sensor is above our minThreshold
            //In the event that music is not playing
            if(input > minThreshold && playingMusic == false && timesAboveThreshold > timeBeforePlay){
                mStorage.getFile(musicFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        try {
                          mPlayer.setDataSource(musicFile.getAbsolutePath());
                          mPlayer.prepare();
                          mPlayer.start();
                        } catch (IOException e) {
                          System.out.println("prepare() failed");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        System.out.println("Song was not loaded");
                    }
                });
                playingMusic = true;
            }

            //Increments the timesAboveThreshold
            //Ensures that we don't play the music for outlining data
            if(input > minThreshold ){
                timesAboveThreshold++;
            }


            //input from sensor is below our minThreshold
            //Ensures that we don't stop the music for outlining data
            if(input < minThreshold && timesBelowThreshold < timeBelow){
                timesBelowThreshold++;
            }

            //Please don't stop the music
            if(playingMusic == true && timesBelowThreshold > timeBelow && input < minThreshold){
                mPlayer.stop();
            }

            //Push data to hashmap to be pushed to the server
            data_DBref.put(Long.toString(System.currentTimeMillis()), Integer.toString(input) );
            System.out.println(Long.toString(System.currentTimeMillis()) + "     " + Integer.toString(input));

        }

        return data_DBref;

    }


    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device) {
        Log.d(TAG, "connected");

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Cancel the accept thread because we only want to connect to one device
        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }


        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.read();

        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);

    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        Log.d(TAG, "stop");

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }

        mState = STATE_NONE;

    }

    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    private void connectionFailed() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        mState = STATE_NONE;

        // Start the service over to restart listening mode
        BluetoothService.this.start();
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        mState = STATE_NONE;
        // Update UI title

        // Start the service over to restart listening mode
        BluetoothService.this.start();
    }


    public class ConnectedThread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            mState = STATE_CONNECTED;
        }

        public int read() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes = 0;

            // Keep listening to the InputStream while connected
            //while (mState == STATE_CONNECTED) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    // Send the obtained bytes to the UI Activity
                    mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                    return bytes;
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    return bytes;
                    //break;
                }
            //}
        }

        /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }


    public class AcceptThread extends Thread{
        private static final String TAG = "BluetoothChatService";

        // Name for the SDP record when creating server socket
        private static final String NAME_SECURE = "BluetoothChatSecure";
        private static final String NAME_INSECURE = "BluetoothChatInsecure";

        //private final BluetoothServerSocket mmServerSocket;


        public AcceptThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                try {
                    pairDevice(device);
                    tmp = createBluetoothSocket(device);
                } catch (Exception e) {
                    Log.e(TAG, "Could not create Insecure RFComm Connection", e);
                }
            mmSocket = tmp;
            System.out.println("mmSocket Name: "+ mmSocket.getConnectionType());
            System.out.println("mmSocket Remote: " + mmSocket.getRemoteDevice().getName());
        }


        private void pairDevice(BluetoothDevice device) {
            try {
                Method method = device.getClass().getMethod("createBond", (Class[]) null);
                method.invoke(device, (Object[]) null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
            if(Build.VERSION.SDK_INT >= 10){
                try {
                    final Method  m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                    return (BluetoothSocket) m.invoke(device, MY_UUID_SECURE);
                } catch (Exception e) {
                    Log.e(TAG, "Could not create Insecure RFComm Connection",e);
                }
            }
            return  device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
        }

            public void run() {
            // Cancel discovery because it otherwise slows down the connection.
                mAdapter.cancelDiscovery();
                System.out.println(mmSocket);
                    try {
                        // Connect to the remote device through the socket. This call blocks
                        // until it succeeds or throws an exception.
                        mmSocket.connect();
                    } catch (IOException connectException) {
                        // Unable to connect; close the socket and return.
                        try {
                            mmSocket.close();
                        } catch (IOException closeException) {
                            Log.e(TAG, "Could not close the client socket", closeException);
                        }
                        return;
                    }

                    // The connection attempt succeeded. Perform work associated with
                    // the connection in a separate thread.

                }

                // Closes the client socket and causes the thread to finish.
                public void cancel() {
                    try {
                        mmSocket.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Could not close the client socket", e);
                    }
                }
    }



}
