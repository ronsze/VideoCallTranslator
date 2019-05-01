package com.uswit.videocalltranslate;
/*
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;

import com.uswit.videocalltranslate.R;

import io.skyway.Peer.Browser.Canvas;
import io.skyway.Peer.Browser.MediaConstraints;
import io.skyway.Peer.Browser.MediaStream;
import io.skyway.Peer.Browser.Navigator;
import io.skyway.Peer.CallOption;
import io.skyway.Peer.ConnectOption;
import io.skyway.Peer.DataConnection;
import io.skyway.Peer.MediaConnection;
import io.skyway.Peer.Peer;

class VideoCall{
    private Peer peer;
    private MediaStream localStream;
    private MediaStream remoteStream;
    private MediaConnection mediaConnection;
    private DataConnection dataConnection;
    private boolean bConnected;
    private boolean cConnected;

    private Canvas localView;
    private Canvas remoteView;

    private FrameLayout main;
    private FrameLayout call;
    private Button callBtn;

    private String myName;
    private String remotePeerID;

    private Context context;

    private Handler mMLhandler;

    private StringBuffer chatBuffer;
    private TextView chatView;

    VideoCall(Context context, Handler handler, Peer peer, String myName) {
        this.context = context;
        this.mMLhandler = handler;
        this.peer = peer;
        this.myName = myName;

        chatBuffer = new StringBuffer();
    }

    void setCanvas(Canvas localView, Canvas remoteView, FrameLayout main, FrameLayout call, Button callBtn, TextView chatView) {
        this.localView = localView;
        this.remoteView = remoteView;
        this.main = main;
        this.call = call;
        this.callBtn = callBtn;
        this.chatView = chatView;
    }

    void startLocalStream() {
        MediaConstraints constraints = new MediaConstraints();
        constraints.maxWidth = 480;
        constraints.maxHeight = 270;
        constraints.maxFrameRate = 5;
        constraints.cameraPosition = MediaConstraints.CameraPositionEnum.FRONT;
        Navigator.initialize(peer);
        localStream = Navigator.getUserMedia(constraints);

        localStream.addVideoRenderer(localView, 0);
        localView.setZOrderMediaOverlay(true);
    }

    private void setMediaCallbacks() {
        mediaConnection.on(MediaConnection.MediaEventEnum.STREAM, o -> {
            remoteStream = (MediaStream) o;

            remoteStream.addVideoRenderer(remoteView, 0);
        });

        mediaConnection.on(MediaConnection.MediaEventEnum.CLOSE, o -> {
            closeRemoteStream();
            bConnected = false;
        });

        mediaConnection.on(MediaConnection.MediaEventEnum.ERROR, o -> {
            //PeerError error = (PeerError) o;
            //Log.d(TAG, "[On/MediaError]" + error);
        });
    }

    void setDataCallbacks() {
        dataConnection.on(DataConnection.DataEventEnum.OPEN, o -> {
            cConnected = true;
            turnLayout();
        });

        dataConnection.on(DataConnection.DataEventEnum.CLOSE, o -> {
            cConnected = false;
            turnLayout();
            unsetDataCallbacks();
            dataConnection = null;
        });

        dataConnection.on(DataConnection.DataEventEnum.DATA, o -> {
            String strValue = null;
            if (o instanceof String) {
                strValue = (String) o;
            }
            chatBuffer.append(strValue);
            chatView.setText(strValue);
        });
    }

    void setDataConnection(DataConnection dataConnection) {
        this.dataConnection = dataConnection;
    }

    void setMediaConnection(MediaConnection mediaConnection) {
        this.mediaConnection = mediaConnection;
        setMediaCallbacks();

        mediaConnection.answer(localStream);
        bConnected = true;

        turnLayout();
    }

    void callBtnClick() {
        if(!bConnected){
            showPeerIDs();
        }else{
            closeRemoteStream();
            mediaConnection.close();
            turnLayout();
        }
    }

    void switchCameraAction() {
        if(null != localStream){
            boolean result = localStream.switchCamera();
            if(result)	{
                //Success
            }
            else {
                //Failed
            }
        }
    }

    void destroyPeer() {
        closeRemoteStream();

        if (localStream != null) {
            localStream.removeVideoRenderer(localView, 0);
            localStream.close();
        }

        if (mediaConnection != null) {
            if (mediaConnection.isOpen()) {
                mediaConnection.close();
            }
            unsetMediaCallbacks();
        }

        if (dataConnection != null) {
            if (dataConnection.isOpen()) {
                dataConnection.close();
            }
            unsetDataCallbacks();
        }

        Navigator.terminate();
        if (peer != null) {
            unsetPeerCallbacks();
            if (!peer.isDisconnected()) {
                peer.disconnect();
            }
            if (!peer.isDestroyed()) {
                peer.destroy();
            }
            peer = null;
        }
    }

    private void unsetPeerCallbacks() {
        if (peer == null) {
            return;
        }
        peer.on(Peer.PeerEventEnum.OPEN, null);
        peer.on(Peer.PeerEventEnum.CONNECTION, null);
        peer.on(Peer.PeerEventEnum.CALL, null);
        peer.on(Peer.PeerEventEnum.CLOSE, null);
        peer.on(Peer.PeerEventEnum.DISCONNECTED, null);
        peer.on(Peer.PeerEventEnum.ERROR, null);
    }

    private void unsetDataCallbacks() {
        if (null == dataConnection) {
            return;
        }

        dataConnection.on(DataConnection.DataEventEnum.OPEN, null);
        dataConnection.on(DataConnection.DataEventEnum.CLOSE, null);
        dataConnection.on(DataConnection.DataEventEnum.DATA, null);
        dataConnection.on(DataConnection.DataEventEnum.ERROR, null);

    }

    private void unsetMediaCallbacks() {
        if (null == mediaConnection) {
            return;
        }

        mediaConnection.on(MediaConnection.MediaEventEnum.STREAM, null);
        mediaConnection.on(MediaConnection.MediaEventEnum.CLOSE, null);
        mediaConnection.on(MediaConnection.MediaEventEnum.ERROR, null);
    }


    private void closeRemoteStream() {
        if (remoteStream == null) {
            return;
        }

        remoteStream.removeVideoRenderer(remoteView, 0);
        remoteStream.close();
        turnLayout();
    }

    private void onPeerSelected(String remoteID) {
        if (peer == null) {
            return;
        }
        if (mediaConnection != null) {
            mediaConnection.close();
        }

        if (dataConnection != null) {
            dataConnection.close();
        }

        ConnectOption cOption = new ConnectOption();
        cOption.label = "chat";
        dataConnection = peer.connect(remoteID, cOption);

        CallOption option = new CallOption();
        mediaConnection = peer.call(remoteID, localStream, option);

        if (mediaConnection != null) {
            setMediaCallbacks();
            setDataCallbacks();
            bConnected = true;
            cConnected = true;
        }

        turnLayout();
    }

    private void showPeerIDs() {
        if ((peer == null) || (myName == null) || (myName.length() == 0)) {
            Toast.makeText(context, R.string.id_invalid, Toast.LENGTH_SHORT).show();
            return;
        }

        peer.listAllPeers(o -> {
            if (!(o instanceof JSONArray)) {
                return;
            }
            JSONArray peers = (JSONArray) o;
            ArrayList<String> listPeerIDs = new ArrayList<>();
            String peerId;

            for (int i = 0; i < peers.length(); i++) {
                try {
                    peerId = peers.getString(i);
                    if (!(myName.equals(peerId))) {
                        listPeerIDs.add(peers.getString(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (listPeerIDs.size() > 0) {
                showIdDialog(listPeerIDs);
            } else {
                Toast.makeText(context, R.string.list_empty, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showIdDialog(ArrayList<String> list) {
        AlertDialog.Builder peerList = new AlertDialog.Builder(context);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        for (int i = 0; i < list.size(); i++) {
            adapter.add(list.get(i));
        }

        peerList.setAdapter(adapter, (dialog, which) -> {
            remotePeerID = adapter.getItem(which);

            mMLhandler.post(() -> onPeerSelected(remotePeerID));
        });

        peerList.show();
    }

    void turnLayout() {
        mMLhandler.post(() -> {
            if ((null != callBtn)) {
                if (bConnected) {
                    main.setVisibility(View.INVISIBLE);
                    call.setVisibility(View.VISIBLE);
                    callBtn.setText(R.string.btn_hangup);
                } else {
                    main.setVisibility(View.VISIBLE);
                    call.setVisibility(View.INVISIBLE);
                    callBtn.setText(R.string.btn_call);
                }
            }
        });
    }


    boolean sendData(String myText) {
        if (dataConnection == null) {
            return false;
        }
        boolean Result;

        Result = dataConnection.send(myText);

        return Result;
    }
}*/