package com.ahnu.server.helper;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

import java.io.*;

public class FastDFSHelper {
    private static final Logger logger = LoggerFactory.getLogger(FastDFSHelper.class);
    private static TrackerClient trackerClient;

    public FastDFSHelper(String configFilePath) throws MyException, IOException {
        ClientGlobal.init(configFilePath);
        trackerClient = new TrackerClient();
        logger.info("dfs created!");
    }

    public String[] uploadFile(String localFilename) throws IOException, MyException {
        TrackerServer trackerServer = trackerClient.getTrackerServer();
        StorageClient storageClient = new StorageClient(trackerServer);
        String[] arr = storageClient.upload_file(localFilename, null, null);
        if (arr == null || arr.length != 2) {
            logger.error("向FastDFS上传文件失败");
        } else {
            logger.info("向FastDFS上传文件成功");
        }
        closeTrackerServer(trackerServer);
        return arr;
    }

    public static byte[] getFileByteArray(File file) {
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            System.out.println("file too big...");
            return null;
        }
        byte[] buffer = null;
        try (FileInputStream fi = new FileInputStream(file)) {
            buffer = new byte[(int) fileSize];
            int offset = 0;
            int numRead = 0;
            while (offset < buffer.length
                    && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
                offset += numRead;
            }
            // 确保所有数据均被读取
            if (offset != buffer.length) {
                throw new IOException("Could not completely read file "
                        + file.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }

    public void uploadFile(byte[] bytes) {
        TrackerServer trackerServer;
        try {
            trackerServer = trackerClient.getTrackerServer();
        } catch (IOException e) {
            logger.error("error", e);
            return;
        }
        StorageClient storageClient = new StorageClient(trackerServer, null);
        try {
            String[] arr = storageClient.upload_file(bytes, null, null);
            if (arr == null || arr.length != 2) {
                logger.error("向FastDFS上传文件失败");
            } else {
                logger.info("向FastDFS上传文件成功");
            }
        } catch (IOException | MyException e) {
            logger.error("error", e);
        } finally {
            closeTrackerServer(trackerServer);
        }
    }

    /**
     * 从FastDFS下载文件
     *
     * @param localFilename  本地文件名
     * @param groupName      文件在FastDFS中的组名
     * @param remoteFilename 文件在FastDFS中的名称
     */
    public void downloadFile(String localFilename, String groupName, String remoteFilename) {
        TrackerServer trackerServer;
        try {
            trackerServer = trackerClient.getTrackerServer();
        } catch (IOException e) {
            logger.error("error", e);
            return;
        }
        StorageClient storageClient = new StorageClient(trackerServer, null);
        File file = new File(localFilename);
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
            byte[] content = storageClient.download_file(groupName, remoteFilename);
            if (content == null || content.length == 0) {
                logger.error("文件大小为空！");
                boolean flag = file.delete();
                logger.info("删除文件结果：{}", flag);
                return;
            }
            bos.write(content);
            logger.info("成功下载文件： " + localFilename);
        } catch (IOException | MyException e) {
            logger.error("error", e);
        } finally {
            closeTrackerServer(trackerServer);
        }
    }

    /**
     * 从FastDFS删除文件
     *
     * @param localFilename  本地文件名
     * @param groupName      文件在FastDFS中的组名
     * @param remoteFilename 文件在FastDFS中的名称
     */
    public void deleteFile(String localFilename, String groupName, String remoteFilename) {
        TrackerServer trackerServer;
        try {
            trackerServer = trackerClient.getTrackerServer();
        } catch (IOException e) {
            logger.error("error", e);
            return;
        }
        StorageClient storageClient = new StorageClient(trackerServer, null);
        try {
            int i = storageClient.delete_file(groupName, remoteFilename);
            if (i == 0) {
                logger.info("FastDFS删除文件成功");
            } else {
                logger.info("FastDFS删除文件失败");
            }
        } catch (IOException | MyException e) {
            logger.error("error", e);
        } finally {
            closeTrackerServer(trackerServer);
        }
    }

    private void closeTrackerServer(TrackerServer trackerServer) {
        try {
            if (trackerServer != null) {
                logger.info("关闭trackerServer连接");
                trackerServer.getConnection().close();
            }
        } catch (IOException | MyException e) {
            logger.error("error", e);
        }
    }
}
