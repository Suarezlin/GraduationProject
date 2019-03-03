package com.suarezlin.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MergeVideoBgm {

    private String ffmpegPath;

    public MergeVideoBgm() {

    }

    public MergeVideoBgm(String ffmpegPath) {
        this.ffmpegPath = ffmpegPath;
    }

    public String getFfmpegPath() {
        return ffmpegPath;
    }

    public void setFfmpegPath(String ffmpegPath) {
        this.ffmpegPath = ffmpegPath;
    }

    public void convert(String inputVideoPath, String inputBgmPath, String outputVideoPath, float seconds) throws IOException {
        List<String> commands = new ArrayList<>();
        commands.add(ffmpegPath);
        commands.add("-i");
        commands.add(inputVideoPath);
        commands.add("-i");
        commands.add(inputBgmPath);
        commands.add("-t");
        commands.add(String.valueOf(seconds));
        commands.add("-y");
        commands.add(outputVideoPath);

//        for (String s : commands) {
//            System.out.print(s);
//        }

        ProcessBuilder builder = new ProcessBuilder(commands);
        Process process = builder.start();
        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line = "";

        while ((line = bufferedReader.readLine()) != null) {
            //System.out.println(line);
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

    public void getScreenShot(String inputVideoPath, String outputImgPath) throws IOException {
        // ffmpeg -i demo.mp4 -ss 00:00:01 -vframes 1 -y out.jpg
        List<String> commands = new ArrayList<>();
        commands.add(ffmpegPath);
        commands.add("-i");
        commands.add(inputVideoPath);
        commands.add("-ss");
        commands.add("00:00:01");
        commands.add("-vframes");
        commands.add("1");
        commands.add("-y");
        commands.add("-q:v");
        commands.add("2");
        commands.add(outputImgPath);

//        for (String s : commands) {
//            System.out.print(s);
//        }

        ProcessBuilder builder = new ProcessBuilder(commands);
        Process process = builder.start();
        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line = "";

        while ((line = bufferedReader.readLine()) != null) {
            //System.out.println(line);
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
        MergeVideoBgm ffMpeg = new MergeVideoBgm("/Users/hayashikoushi/Downloads/ffmpeg-static/bin/ffmpeg");
        ffMpeg.convert("/Users/hayashikoushi/Downloads/IMG_0908.MOV", "/Users/hayashikoushi/Documents/code/GraduationProject/ProjectFile/bgm/Panzerlied(少战).mp3","/Users/hayashikoushi/Downloads/新视频.avi", 3);

    }

}
