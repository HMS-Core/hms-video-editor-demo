
/*
 *  Copyright 2021. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.huawei.hms.videoeditor.ui.common.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import android.os.Build;

import com.huawei.hms.videoeditor.sdk.util.SmartLog;

public class SafeSecureRandom {
    private static final String TAG = "SafeRandom";

    public static final String SHA1PRNG = "SHA1PRNG";

    private static SecureRandom secureRandom = null;

    private SafeSecureRandom() {
    }

    private static SecureRandom getSecureRandom() throws NoSuchAlgorithmException {
        if (secureRandom != null) {
            return secureRandom;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            secureRandom = SecureRandom.getInstanceStrong();
        } else {
            secureRandom = SecureRandom.getInstance(SHA1PRNG);
        }

        return secureRandom;
    }

    public static int nextInt(int bound) {
        try {
            return getSecureRandom().nextInt(bound);
        } catch (IllegalArgumentException e) {
            SmartLog.e(TAG, "nextInt IllegalArgumentException bound=" + bound);
            return 0;
        } catch (NoSuchAlgorithmException e) {
            SmartLog.e(TAG, "nextInt NoSuchAlgorithmException");
            return 0;
        }
    }
}
