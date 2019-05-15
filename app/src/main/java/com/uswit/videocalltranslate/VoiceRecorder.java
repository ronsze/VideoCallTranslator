/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.uswit.videocalltranslate;

import android.media.AudioFormat;
import android.util.Log;

import androidx.annotation.NonNull;

import org.webrtc.audio.JavaAudioDeviceModule;
import org.webrtc.audio.JavaAudioDeviceModule.SamplesReadyCallback;

import java.util.concurrent.ExecutorService;


/**
 * Continuously records audio and notifies the {@link VoiceRecorder.Callback} when voice (or any
 * sound) is heard.
 *
 * <p>The recorded audio format is always {@link AudioFormat#ENCODING_PCM_16BIT} and
 * {@link AudioFormat#CHANNEL_IN_MONO}. This class will automatically pick the right sample rate
 */
public class VoiceRecorder implements SamplesReadyCallback {

    private static final String TAG = "VoiceRecorder";

    private static final int AMPLITUDE_THRESHOLD = 1500;
    private static final int SPEECH_TIMEOUT_MILLIS = 2000;
    private static final int MAX_SPEECH_LENGTH_MILLIS = 30 * 1000;

    public static abstract class Callback {

        /**
         * Called when the recorder starts hearing voice.
         */
        public void onVoiceStart() {
        }

        /*
         * Called when the recorder is hearing voice.
         *
         * @param data The audio data in {@link AudioFormat#ENCODING_PCM_16BIT}.
         * @param size The size of the actual data in {@code data}.
         */
        public void onVoice(byte[] data, int size) {
        }

        /**
         * Called when the recorder stops hearing voice.
         */
        public void onVoiceEnd() {
        }
    }

    private final Callback mCallback;

    private byte[] mBuffer;

    private int size;

    private final Object mLock = new Object();

    private boolean isRunning;

    /** The timestamp of the last time that voice is heard. */
    private long mLastVoiceHeardMillis = Long.MAX_VALUE;

    /** The timestamp when the current voice is started. */
    private long mVoiceStartedMillis;

    private final ExecutorService executor;

    public VoiceRecorder(@NonNull Callback callback, ExecutorService executor) {
        mCallback = callback;
        this.executor = executor;
    }

    /**
     * Starts recording audio.
     *
     * <p>The caller is responsible for calling {@link #stop()} later.</p>
     */
    public void start() {
        // Stop recording if it is currently ongoing.
        stop();

        synchronized (mLock) {
            isRunning = true;
        }
    }

    /**
     * Stops recording audio.
     */
    public void stop() {
        synchronized (mLock) {
            isRunning = false;

            dismiss();

            mBuffer = null;
        }
    }

    /**
     * Dismisses the currently ongoing utterance.
     */
    public void dismiss() {
        if (mLastVoiceHeardMillis != Long.MAX_VALUE) {
            mLastVoiceHeardMillis = Long.MAX_VALUE;
            mCallback.onVoiceEnd();
        }
    }

    @Override
    public void onWebRtcAudioRecordSamplesReady(JavaAudioDeviceModule.AudioSamples samples) {
        if (!isRunning) return;

        // The native audio layer on Android should use 16-bit PCM format.
        if (samples.getAudioFormat() != AudioFormat.ENCODING_PCM_16BIT) {
            Log.e("SamplesReady", "Invalid audio format");
            return;
        }

        // Append the recorded 16-bit audio samples to the open output file.
        executor.execute(() -> {
            mBuffer = null;
            mBuffer = samples.getData();
            size = samples.getData().length;

            synchronized (mLock) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }

                final long now = System.currentTimeMillis();
                if (isHearingVoice(mBuffer, size)) {
                    if (mLastVoiceHeardMillis == Long.MAX_VALUE) {
                        mVoiceStartedMillis = now;
                        mCallback.onVoiceStart();
                    }
                    mCallback.onVoice(mBuffer, size);
                    mLastVoiceHeardMillis = now;
                    if (now - mVoiceStartedMillis > MAX_SPEECH_LENGTH_MILLIS) {
                        end();
                    }
                } else if (mLastVoiceHeardMillis != Long.MAX_VALUE) {
                    mCallback.onVoice(mBuffer, size);
                    if (now - mLastVoiceHeardMillis > SPEECH_TIMEOUT_MILLIS) {
                        end();
                    }
                }
            }
        });
    }

    private void end() {
        mLastVoiceHeardMillis = Long.MAX_VALUE;
        mCallback.onVoiceEnd();
    }

    private boolean isHearingVoice(byte[] buffer, int size) {
        for (int i = 0; i < size - 1; i += 2) {
            // The buffer has LINEAR16 in little endian.
            int s = buffer[i + 1];
            if (s < 0) s *= -1;
            s <<= 8;
            s += Math.abs(buffer[i]);
            if (s > AMPLITUDE_THRESHOLD) {
                return true;
            }
        }
        return false;
    }
}