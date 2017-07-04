import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.iflytek.cloud.speech.RecognizerListener;
import com.iflytek.cloud.speech.RecognizerResult;
import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechRecognizer;

//音频流听写
class AudioToText {

    private StringBuffer mResult = new StringBuffer();
    private boolean mIsEndOfSpeech = false;

    String transform(String filePath) {
        mIsEndOfSpeech = false;
        recognizePcmfileByte(filePath);

        try {
            synchronized (this) {
                this.wait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mResult.toString();
    }

    private void recognizePcmfileByte(String filePath) {
        if (SpeechRecognizer.getRecognizer() == null) SpeechRecognizer.createRecognizer();
        SpeechRecognizer recognizer = SpeechRecognizer.getRecognizer();
        recognizer.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
        recognizer.setParameter(SpeechConstant.RESULT_TYPE, "plain");
        recognizer.startListening(recListener);

        FileInputStream fis = null;
        final byte[] buffer = new byte[64 * 1024];
        try {
            fis = new FileInputStream(new File(filePath));
            if (0 == fis.available()) {
                mResult.append("[error]no audio avaible!");
                recognizer.cancel();
            } else {
                int lenRead = buffer.length;
                while (buffer.length == lenRead && !mIsEndOfSpeech) {
                    lenRead = fis.read(buffer);
                    recognizer.writeAudio(buffer, 0, lenRead);
                }

                recognizer.stopListening();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fis) {
                    fis.close();
                    fis = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 听写监听器
     */
    private RecognizerListener recListener = new RecognizerListener() {

        public void onBeginOfSpeech() {
            DebugLog.log("onBeginOfSpeech enter");
            DebugLog.log("*************开始录音*************");
        }

        public void onEndOfSpeech() {
            DebugLog.log("onEndOfSpeech enter");
            mIsEndOfSpeech = true;
        }

        public void onVolumeChanged(int volume) {
            DebugLog.log("onVolumeChanged enter");
            if (volume > 0)
                DebugLog.log("*************音量值:" + volume + "*************");

        }

        public void onResult(RecognizerResult result, boolean islast) {
            DebugLog.log("onResult enter");
            mResult.append(result.getResultString());

            if (islast) {
                DebugLog.log("识别结果为:" + mResult.toString());
                mIsEndOfSpeech = true;
                waitupLoop();
            }
        }

        public void onError(SpeechError error) {
            mIsEndOfSpeech = true;
            DebugLog.log("*************" + error.getErrorCode()
                    + "*************");
            waitupLoop();
        }

        public void onEvent(int eventType, int arg1, int agr2, String msg) {
            DebugLog.log("onEvent enter");
        }

    };


    private void waitupLoop() {
        synchronized (this) {
            AudioToText.this.notify();
        }
    }
}
