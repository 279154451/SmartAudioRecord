package com.singlecode.audiorecord.project1;

/**
 * 创建时间：2020/10/12
 * 创建人：singleCode
 * 功能描述：
 * 获取录音的音频流,用于拓展的处理
 */
public interface RecordStreamListener {
    void recordOfByte(byte[] data, int begin, int end);
}