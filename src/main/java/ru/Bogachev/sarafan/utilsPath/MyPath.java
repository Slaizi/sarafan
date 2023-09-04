package ru.Bogachev.sarafan.utilsPath;

import org.springframework.stereotype.Service;

import java.nio.file.Paths;
@Service
public class MyPath {
    public String myPathForFile() {
        String currentWorkingDir = System.getProperty("user.dir");
        String relativeOutputDir = "uploads";
        return Paths.get(currentWorkingDir, relativeOutputDir).toString();
    }
}
