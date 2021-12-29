/*
 *   Copyright 2021. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.huawei.hms.videoeditor.ui.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import android.annotation.SuppressLint;

import com.huawei.hms.videoeditor.sdk.util.SmartLog;

public class CpuUtils {
    private static final String TAG = "CpuUtils";

    public static final String CPU_ARCHITECTURE_TYPE_32 = "32";

    public static final String CPU_ARCHITECTURE_TYPE_64 = "64";

    private static final int EI_CLASS = 4;

    private static final int ELFCLASS64 = 2;

    /**
     * The system property key of CPU arch type
     */
    private static final String CPU_ARCHITECTURE_KEY_64 = "ro.product.cpu.abilist64";

    /**
     * The system libc.so file path
     */
    private static String SYSTEM_LIB_C_PATH = "/system/lib/libc.so";

    private static String SYSTEM_LIB_C_PATH_64 = "/system/lib64/libc.so";

    private static String PROC_CPU_INFO_PATH = "/proc/cpuinfo";

    /**
     * * Get the CPU arch type: x32 or x64
     */
    public static String getArchType() throws IOException {
        if (getSystemProperty(CPU_ARCHITECTURE_KEY_64).length() > 0) {
            SmartLog.d(TAG, "CPU arch is 64bit");
            return CPU_ARCHITECTURE_TYPE_64;
        } else if (isCPUInfo64()) {
            return CPU_ARCHITECTURE_TYPE_64;
        } else if (isLibc64()) {
            return CPU_ARCHITECTURE_TYPE_64;
        } else {
            SmartLog.d(TAG, "CPU default 32bit!");
            return CPU_ARCHITECTURE_TYPE_32;
        }
    }

    private static String getSystemProperty(String key) {
        String value = null;
        try {
            @SuppressLint("PrivateApi")
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            Method get = clazz.getMethod("get", String.class, String.class);
            value = (String) (get.invoke(clazz, key, ""));
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException
            | InvocationTargetException e) {
            SmartLog.d(TAG, "key = " + key + ", error = " + e.getMessage());
        }
        SmartLog.d(TAG, key + " = " + value);
        return value;
    }

    /**
     * Read the first line of "/proc/cpuinfo" file, and check if it is 64 bit.
     * 
     */
    private static boolean isCPUInfo64() throws FileNotFoundException {
        File cpuInfo = new File(PROC_CPU_INFO_PATH);
        if (cpuInfo.exists()) {
            InputStream inputStream = new FileInputStream(cpuInfo);
            try (BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8), 512)) {
                String line = bufferedReader.readLine();
                if (line != null && line.length() > 0 && line.toLowerCase(Locale.US).contains("arch64")) {
                    SmartLog.d(TAG, PROC_CPU_INFO_PATH + " contains is arch64");
                    return true;
                } else {
                    SmartLog.d(TAG, PROC_CPU_INFO_PATH + " is not arch64");
                }
            } catch (IOException e) {
                SmartLog.e("TAG", e.getMessage());
            }
        }
        return false;
    }

    /**
     * Check if system libc.so is 32 bit or 64 bit
     */
    private static boolean isLibc64() {
        File libcFile = new File(SYSTEM_LIB_C_PATH);
        if (libcFile.exists()) {
            byte[] header = getTAG(libcFile);
            if (header[EI_CLASS] == ELFCLASS64) {
                SmartLog.d(TAG, SYSTEM_LIB_C_PATH + " is 64bit");
                return true;
            }
        }

        File libcFile64 = new File(SYSTEM_LIB_C_PATH_64);
        if (libcFile64.exists()) {
            byte[] header = getTAG(libcFile64);
            if (header[EI_CLASS] == ELFCLASS64) {
                SmartLog.d(TAG, SYSTEM_LIB_C_PATH_64 + " is 64bit");
                return true;
            }
        }

        return false;
    }

    private static byte[] getTAG(File libFile) {
        if (libFile != null && libFile.exists()) {
            try (FileInputStream inputStream = new FileInputStream(libFile)) {
                byte[] tempBuffer = new byte[16];
                int count = inputStream.read(tempBuffer, 0, 16);
                if (count == 16) {
                    return tempBuffer;
                } else {
                    SmartLog.e("TAG", "Error: e_indent lenght should be 16, but actual is " + count);
                }
            } catch (IOException e) {
                SmartLog.e("TAG", e.getMessage());
            }
        }
        return new byte[16];
    }
}
