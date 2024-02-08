package com.akilisha.reactive;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

public class WriteBytes {
    
    public static void toFile(byte[] bytes, String filePath) {
        // String packagePath = baseClass.getName().substring(0,
        // baseClass.getName().lastIndexOf("."));
        // String baseDirPath = packagePath.replace(".", "/");
        // Z:\js-html\reactive\app\build\classes\java\main\com\akilisha\reactive\asm\DataObs.class
        File file = Paths.get("Z:\\js-html\\reactive\\app\\build\\classes\\java\\main",
                filePath + ".class").toFile();
        try {
            file.createNewFile();
            // try with resources
            try (FileOutputStream stream = new FileOutputStream(file)) {
                stream.write(bytes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
