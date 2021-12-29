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

package com.huawei.hms.videoeditor.ui.common.bean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class WavHeader {
    public final char[] fileID = {'R', 'I', 'F', 'F'};

    public int fileLength;

    public char[] wavTag = {'W', 'A', 'V', 'E'};

    public char[] fmtHdrID = {'f', 'm', 't', ' '};

    public int fmtHdrLeth;

    public short formatTag;

    public short channels;

    public int samplesPerSec;

    public int avgBytesPerSec;

    public short blockAlign;

    public short bitsPerSample;

    public char[] dataHdrID = {'d', 'a', 't', 'a'};

    public int dataHdrLeth;

    public byte[] getHeader() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        writeChar(bos, fileID);
        writeInt(bos, fileLength);
        writeChar(bos, wavTag);
        writeChar(bos, fmtHdrID);
        writeInt(bos, fmtHdrLeth);
        writeShort(bos, formatTag);
        writeShort(bos, channels);
        writeInt(bos, samplesPerSec);
        writeInt(bos, avgBytesPerSec);
        writeShort(bos, blockAlign);
        writeShort(bos, bitsPerSample);
        writeChar(bos, dataHdrID);
        writeInt(bos, dataHdrLeth);
        bos.flush();
        byte[] r = bos.toByteArray();
        bos.close();
        return r;
    }

    private void writeShort(ByteArrayOutputStream bos, int s) throws IOException {
        byte[] mybyte = new byte[2];
        mybyte[1] = (byte) ((s << 16) >> 24);
        mybyte[0] = (byte) ((s << 24) >> 24);
        bos.write(mybyte);
    }

    private void writeInt(ByteArrayOutputStream bos, int n) throws IOException {
        byte[] buf = new byte[4];
        buf[3] = (byte) (n >> 24);
        buf[2] = (byte) ((n << 8) >> 24);
        buf[1] = (byte) ((n << 16) >> 24);
        buf[0] = (byte) ((n << 24) >> 24);
        bos.write(buf);
    }

    private void writeChar(ByteArrayOutputStream bos, char[] id) {
        for (int i = 0; i < id.length; i++) {
            char c = id[i];
            bos.write(c);
        }
    }
}
