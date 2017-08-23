package com.earth.data.down;

/**
 * Created by Frapo on 2017/4/22.
 * Earth 17:24
 */

class FileInfo {



    private String url;
    private String md5;
    private long length;
    private String fileName;

    public FileInfo(int i, String absolutePath, String name, long length, int i1, String fileMD5String) {
        this.url = absolutePath;
        this.fileName = name;
        this.length = length;
        this.md5 = fileMD5String;
    }

    public FileInfo(String path, String name, long length, String md5) {
        this.url = path;
        this.fileName = name;
        this.length = length;
        this.md5 = md5;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public long getLength() {
        return length;
    }

    public String getMd5() {
        return md5;
    }

    public String getFileName() {
        return fileName;
    }
}
