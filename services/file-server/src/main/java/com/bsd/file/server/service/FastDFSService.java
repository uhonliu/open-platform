package com.bsd.file.server.service;

import com.bsd.file.server.configuration.FastDFSProperties;
import com.opencloud.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

/**
 * @author liujianhong
 */
@Slf4j
@Service
public class FastDFSService {
    private TrackerClient trackerClient = null;
    private TrackerServer trackerServer = null;
    private StorageServer storageServer = null;
    private StorageClient storageClient = null;

    @Autowired
    private FastDFSProperties fastDFSProperties;

    @Autowired
    public void init() throws IOException, MyException {
        Properties props = new Properties();
        props.put(ClientGlobal.PROP_KEY_CONNECT_TIMEOUT_IN_SECONDS, fastDFSProperties.getConnectTimeoutInSeconds());
        props.put(ClientGlobal.PROP_KEY_NETWORK_TIMEOUT_IN_SECONDS, fastDFSProperties.getNetworkTimeoutInSeconds());
        props.put(ClientGlobal.PROP_KEY_CHARSET, fastDFSProperties.getCharset());
        props.put(ClientGlobal.PROP_KEY_HTTP_ANTI_STEAL_TOKEN, fastDFSProperties.getHttpAntiStealToken());
        props.put(ClientGlobal.PROP_KEY_HTTP_SECRET_KEY, fastDFSProperties.getHttpSecretKey());
        props.put(ClientGlobal.PROP_KEY_HTTP_TRACKER_HTTP_PORT, fastDFSProperties.getHttpTrackerHttpPort());
        props.put(ClientGlobal.PROP_KEY_TRACKER_SERVERS, fastDFSProperties.getTrackerServers());
        ClientGlobal.initByProperties(props);
        trackerClient = new TrackerClient(ClientGlobal.g_tracker_group);
        trackerServer = trackerClient.getConnection();
        storageServer = trackerClient.getStoreStorage(trackerServer);
        storageClient = new StorageClient(trackerServer, storageServer);
    }

    /**
     * 上传文件
     *
     * @param file 文件对象
     * @return
     */
    public String[] uploadFile(String groupName, File file) {
        return uploadFile(groupName, file, null);
    }

    public String[] uploadFile(String groupName, MultipartFile file) {
        return uploadFile(groupName, file, null);
    }

    /**
     * 上传文件
     *
     * @param file     文件对象
     * @param metaList 文件元数据
     * @return
     */
    public String[] uploadFile(String groupName, File file, Map<String, String> metaList) {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            byte[] buff = IOUtils.toByteArray(inputStream);
            NameValuePair[] nameValuePairs = null;
            if (metaList != null) {
                nameValuePairs = new NameValuePair[metaList.size()];
                int index = 0;
                for (Iterator<Map.Entry<String, String>> iterator = metaList.entrySet().iterator(); iterator.hasNext(); ) {
                    Map.Entry<String, String> entry = iterator.next();
                    String name = entry.getKey();
                    String value = entry.getValue();
                    nameValuePairs[index++] = new NameValuePair(name, value);
                }
            }
            inputStream.close();
            String extName = file.getName().substring(file.getName().lastIndexOf(".") + 1);
            file.delete();
            return storageClient.upload_file(groupName, buff, extName, nameValuePairs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String[] uploadFile(String groupName, MultipartFile file, Map<String, String> metaList) {
        try {
            byte[] buff = IOUtils.toByteArray(file.getInputStream());
            NameValuePair[] nameValuePairs = null;
            if (metaList != null) {
                nameValuePairs = new NameValuePair[metaList.size()];
                int index = 0;
                for (Iterator<Map.Entry<String, String>> iterator = metaList.entrySet().iterator(); iterator.hasNext(); ) {
                    Map.Entry<String, String> entry = iterator.next();
                    String name = entry.getKey();
                    String value = entry.getValue();
                    nameValuePairs[index++] = new NameValuePair(name, value);
                }
            }
            String extName = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".") + 1);
            return storageClient.upload_file(groupName, buff, extName, nameValuePairs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取文件元数据
     *
     * @param fileId 文件ID
     * @return
     */
    public Map<String, String> getFileMetadata(String groupName, String fileId) {
        try {
            NameValuePair[] metaList = storageClient.get_metadata(groupName, fileId);
            if (metaList != null) {
                HashMap<String, String> map = new HashMap<String, String>();
                for (NameValuePair metaItem : metaList) {
                    map.put(metaItem.getName(), metaItem.getValue());
                }
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除文件
     *
     * @param fileId 文件ID
     * @return 删除失败返回-1，否则返回0
     */
    public int deleteFile(String groupName, String fileId) {
        try {
            return storageClient.delete_file(groupName, fileId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 下载文件
     *
     * @param fileId  文件ID（上传文件成功后返回的ID）
     * @param outFile 文件下载保存位置
     * @return
     */
    public int downloadFile(String groupName, String fileId, File outFile) {
        FileOutputStream fos = null;
        try {
            byte[] content = storageClient.download_file(groupName, fileId);
            fos = new FileOutputStream(outFile);
            InputStream ips = new ByteArrayInputStream(content);
            IOUtils.copy(ips, fos);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return -1;
    }

    public InputStream downFile(String groupName, String remoteFileName) {
        try {
            byte[] fileByte = storageClient.download_file(groupName, remoteFileName);
            InputStream ins = new ByteArrayInputStream(fileByte);
            return ins;
        } catch (IOException e) {
            log.error("IO Exception: Get File from Fast DFS failed", e);
        } catch (Exception e) {
            log.error("Non IO Exception: Get File from Fast DFS failed", e);
        }
        return null;
    }

    public FileInfo getFile(String groupName, String remoteFileName) {
        try {
            return storageClient.get_file_info(groupName, remoteFileName);
        } catch (IOException e) {
            log.error("IO Exception: Get File from Fast DFS failed", e);
        } catch (Exception e) {
            log.error("Non IO Exception: Get File from Fast DFS failed", e);
        }
        return null;
    }

    public StorageServer[] getStoreStorages(String groupName) throws IOException {
        return trackerClient.getStoreStorages(trackerServer, groupName);
    }

    public ServerInfo[] getFetchStorages(String groupName, String remoteFileName) throws IOException {
        return trackerClient.getFetchStorages(trackerServer, groupName, remoteFileName);
    }

    public String getTrackerUrl() {
        String trackerUrl = fastDFSProperties.getTrackerUrl();
        if (StringUtils.isBlank(trackerUrl)) {
            trackerUrl = "http://" + trackerServer.getInetSocketAddress().getHostString() + ":" + ClientGlobal.getG_tracker_http_port();
        }
        return trackerUrl + "/";
    }
}