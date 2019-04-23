package demo.speech_to_text;// Imports the Google Cloud client library
import com.google.cloud.speech.v1p1beta1.RecognitionAudio;
import com.google.cloud.speech.v1p1beta1.RecognitionConfig;
import com.google.cloud.speech.v1p1beta1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1p1beta1.RecognizeResponse;
import com.google.cloud.speech.v1p1beta1.SpeechClient;
import com.google.cloud.speech.v1p1beta1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1p1beta1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import ie.corballis.sox.SoXEffect;
import ie.corballis.sox.SoXEncoding;
import ie.corballis.sox.Sox;
import ie.corballis.sox.WrongParametersException;

import java.io.IOException;

public class speech_to_text {

    /**
     * Demonstrates using the Speech API to transcribe an audio file.
     */
    public  void SpeechToText() throws Exception {

//        Effect effect = new Effect();
//        try {
//            effect.reduceNoise("testspeech.wav", "reduced.wav");
//            effect.gain("reduced.wav", "output.wav");
//        } catch (WrongParametersException e) {
//            e.printStackTrace();
//        }

        Sox sox = new Sox("/usr/local/bin/sox");
        try {
            sox
                    .sampleRate(16000)
                    .inputFile("testspeech.wav")
                    .encoding(SoXEncoding.SIGNED_INTEGER)
                    .bits(16)
                    .outputFile("testspeech.wav")
                    .effect(SoXEffect.REMIX,"1")
                    .execute();
        } catch (WrongParametersException e) {
            e.printStackTrace();
        }


        //google speech to text
        // Instantiates a client
        try (SpeechClient speechClient = SpeechClient.create()) {

            // The path to the audio file to transcribe
            String fileName = "testspeech.wav";

            // Reads the audio file into memory
            Path path = Paths.get(fileName);
            byte[] data = Files.readAllBytes(path);
            ByteString audioBytes = ByteString.copyFrom(data);

            // Builds the sync recognize request
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(AudioEncoding.LINEAR16)
                    .setSampleRateHertz(16000)
                    .setLanguageCode("en-US")
                    .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();

            // Performs speech recognition on the audio file
            RecognizeResponse response = speechClient.recognize(config, audio);
            List<SpeechRecognitionResult> results = response.getResultsList();


            System.out.println(results);
            for (SpeechRecognitionResult result : results) {
                // There can be several alternative transcripts for a given chunk of speech. Just use the
                // first (most likely) one here.
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                System.out.printf("Transcription: %s%n", alternative.getTranscript());
            }
        }

    }


}