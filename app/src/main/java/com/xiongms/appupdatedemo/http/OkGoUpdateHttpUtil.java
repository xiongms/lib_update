package com.xiongms.appupdatedemo.http;

import android.support.annotation.NonNull;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Progress;
import com.xiongms.update.HttpManager;
import com.xiongms.update.UpdateAppBean;

import org.json.JSONObject;

import java.io.File;
import java.util.Map;

/**
 * 使用OkGo实现接口
 */

public class OkGoUpdateHttpUtil implements HttpManager {
    /**
     * 异步get
     *
     * @param url      get请求地址
     * @param params   get参数
     * @param callBack 回调
     */
    @Override
    public void asyncRequest(@NonNull String url, @NonNull Map<String, String> params, @NonNull final Callback callBack) {
        OkGo.<String>get(url).params(params).execute(new com.lzy.okgo.callback.StringCallback() {
            @Override
            public void onSuccess(com.lzy.okgo.model.Response<String> response) {

                UpdateAppBean updateAppBean = new UpdateAppBean();
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    updateAppBean.setUpdate(jsonObject.optBoolean("update"))
                            .setNewVersion(jsonObject.optString("new_version"))
                            .setApkFileUrl(jsonObject.optString("apk_file_url"))
                            .setTargetSize(jsonObject.optString("target_size"))
                            .setUpdateLog(jsonObject.optString("update_log"))
                            .setConstraint(jsonObject.optBoolean("constraint"))
                            .setNewMd5(jsonObject.optString("new_md5"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                callBack.onResponse(updateAppBean);
            }

            @Override
            public void onError(com.lzy.okgo.model.Response<String> response) {
                super.onError(response);
                callBack.onError("异常");
            }
        });
    }

    /**
     * 下载
     *
     * @param url      下载地址
     * @param path     文件保存路径
     * @param fileName 文件名称
     * @param callback 回调
     */
    @Override
    public void download(@NonNull String url, @NonNull String path, @NonNull String fileName, @NonNull final FileCallback callback) {
        OkGo.<File>get(url).execute(new com.lzy.okgo.callback.FileCallback(path, fileName) {
            @Override
            public void onSuccess(com.lzy.okgo.model.Response<File> response) {
                callback.onResponse(response.body());
            }

            @Override
            public void onStart(com.lzy.okgo.request.base.Request<File, ? extends com.lzy.okgo.request.base.Request> request) {
                super.onStart(request);
                callback.onBefore();
            }

            @Override
            public void onError(com.lzy.okgo.model.Response<File> response) {
                super.onError(response);
                callback.onError("异常");
            }

            @Override
            public void downloadProgress(Progress progress) {
                super.downloadProgress(progress);

                callback.onProgress(progress.fraction, progress.totalSize);
            }
        });
    }
}