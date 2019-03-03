package com.suarezlin.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FFMpegTest {

    private String ffmpegPath;

    public FFMpegTest(String ffmpegPath) {
        this.ffmpegPath = ffmpegPath;
    }

    public void convert(String inputVideoPath, String outputVideoPath) throws IOException {
        List<String> commands = new ArrayList<>();
        commands.add(ffmpegPath);
        commands.add("-i");
        commands.add(inputVideoPath);
        commands.add(outputVideoPath);

        for (String s : commands) {
            System.out.println(s);
        }

        ProcessBuilder builder = new ProcessBuilder(commands);
        Process process = builder.start();
        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line = "";

        while ((line = bufferedReader.readLine()) != null) {
            System.out.print(line);
        }

        if (bufferedReader != null) {
            bufferedReader.close();
        }
        if (inputStreamReader != null) {
            inputStreamReader.close();
        }
        if (errorStream != null) {
            errorStream.close();
        }

    }

    public static void main(String[] args) throws IOException {
        FFMpegTest ffMpeg = new FFMpegTest("/Users/hayashikoushi/Downloads/ffmpeg-static/bin/ffmpeg");
        ffMpeg.convert("/Users/hayashikoushi/Downloads/IMG_0908.MOV", "/Users/hayashikoushi/Downloads/IMG_0908.avi");

    }

}
