package com.mobwal.walker.beautil.v1;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonObject;
import com.mobwal.walker.beautil.v1.model.AuthorizationMeta;
import com.mobwal.walker.beautil.v1.model.RPCItem;
import com.mobwal.walker.beautil.v1.model.RPCResult;
import com.mobwal.walker.beautil.v1.model.SingleItemQuery;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ServerBeaconDataProvider
        implements BeaconDataProvider {

    private final String baseUrl;
    private final Context context;
    private final String version;

    /**
     *
     * @param context
     * @param baseUrl
     * @param version версия приложения
     */
    public ServerBeaconDataProvider(@NotNull Context context, @NonNull String baseUrl, @NonNull String version) {
        this.baseUrl = baseUrl;
        this.context = context;
        this.version = version;
    }

    @Override
    public String auth(@NonNull String login, @NonNull String password) {
        RequestManager requestManager = new RequestManager();
        AuthorizationMeta meta = requestManager.authorization(context, baseUrl, login, password, version);

        return meta.getToken();
    }

    @Override
    @Nullable
    public wBeacon[] getBeacons(@NonNull String token) {
        SingleItemQuery query = new SingleItemQuery(100000, "");

        RPCItem item = new RPCItem("of_mui_cd_sensors.Select", query, true);
        item.schema = "dbo";

        String urlParams = item.toJsonString();
        byte[] postData = urlParams.getBytes(StandardCharsets.UTF_8);

        try {
            RPCResult[] rpcResults = RequestManager.rpc(baseUrl, token, postData);
            if (rpcResults != null) {
                if (rpcResults.length > 0) {
                    RPCResult result = rpcResults[0];
                    if (result.isSuccess()) {
                        wBeacon[] beacons = new wBeacon[result.result.total];
                        int idx = 0;
                        for(JsonObject jsonObject : result.result.records) {
                            wBeacon beacon = new wBeacon(jsonObject);
                            beacons[idx] = beacon;

                            idx++;
                        }

                        return beacons;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean push(@NonNull String token) {
        LocalSave localSave = new LocalSave(context, "tracker.csv");
        try {
            String[] lines = localSave.readLines();

            RPCItem rpcItem = new RPCItem();
            rpcItem.method = "trackerPush";
            rpcItem.action = "shell";
            rpcItem.data = new Object[1];
            rpcItem.data[0] = lines;

            String urlParams = rpcItem.toJsonString();
            byte[] postData = urlParams.getBytes(StandardCharsets.UTF_8);

            try {
                RPCResult[] rpcResults = RequestManager.rpc(baseUrl, token, postData);
                if (rpcResults != null) {
                    if (rpcResults.length > 0) {
                        RPCResult result = rpcResults[0];

                        boolean isSuccess = result.isSuccess();
                        if(isSuccess) {
                            localSave.truncate();
                        }

                        return isSuccess;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
