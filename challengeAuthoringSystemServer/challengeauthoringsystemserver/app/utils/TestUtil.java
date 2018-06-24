package utils;

import play.Logger;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class TestUtil {

    private final static String TESTFILE_PATH = "/test/resources/";

    public static Object genericInvokeMethod(Object obj, String methodName,
                                            int paramCount, Object... params) {
        Method method;
        Object requiredObj = null;
        Object[] parameters = new Object[paramCount];
        Class<?>[] classArray = new Class<?>[paramCount];
        for (int i = 0; i < paramCount; i++) {
            parameters[i] = params[i];
            if(params[i] != null)
                classArray[i] = params[i].getClass();
            else
                classArray[i] = Object.class;
        }
        try {
            method = obj.getClass().getDeclaredMethod(methodName, classArray);
            method.setAccessible(true);
            requiredObj = method.invoke(obj, parameters);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return requiredObj;
    }

    public static String readTestFile(String fileName) {
        if(fileName == null || fileName.equals("")) {
            return null;
        }
        FileInputStream fis = null;
        StringBuilder testFileData = new StringBuilder();
        String absolutePath = play.Environment.simple().rootPath().getAbsolutePath();
        if(absolutePath.indexOf("\\.idea") > 0){
            absolutePath = absolutePath.substring(0, absolutePath.indexOf("\\.idea"));
        }
        File f = new File(absolutePath + TESTFILE_PATH + fileName);
        try{
            fis = new FileInputStream(f);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                //String validatedLine = validateLine(line);
                //if(validatedLine != null && !validatedLine.equals(""))
                    testFileData.append(line);
            }
        } catch (IOException e) {
            Logger.error(e.getMessage());
        }

        return testFileData.toString();
    }
}
