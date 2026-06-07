package com.tiempometa.pandora.tagreader;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tiempometa.pandora.webservice.api.ParticipantDetailDto;
import com.tiempometa.timing.local.InfoDto;
import com.tiempometa.timing.local.RawChipReadDto;
import com.tiempometa.timing.local.SnapshotDto;
import com.tiempometa.timing.model.RawChipRead;

/**
 * HTTP client for the saturnPandora REST API (port 9001).
 * All methods are stateless — they derive the URL from the given serverAddress.
 */
public final class SaturnRestClient {

    private static final Logger logger = LogManager.getLogger(SaturnRestClient.class);
    private static final Gson gson = new Gson();

    private SaturnRestClient() {}

    private static String baseUrl(String serverAddress) {
        return "http://" + serverAddress + ":9001";
    }

    public static InfoDto getInfo(String serverAddress) {
        String url = baseUrl(serverAddress) + "/api/info";
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(new HttpGet(url))) {
            int status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                return gson.fromJson(EntityUtils.toString(response.getEntity()), InfoDto.class);
            }
            logger.warn("GET /api/info returned status {}", status);
            return null;
        } catch (Exception e) {
            logger.error("GET /api/info failed: {}", e.getMessage());
            return null;
        }
    }

    public static List<ParticipantDetailDto> findParticipantByRfid(
            String serverAddress, String rfidString) {
        String url = baseUrl(serverAddress) + "/api/participants/by-rfid/" + encode(rfidString);
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(new HttpGet(url))) {
            int status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                return gson.fromJson(
                        EntityUtils.toString(response.getEntity()),
                        new TypeToken<List<ParticipantDetailDto>>(){}.getType());
            }
            if (status != 404) {
                logger.warn("GET /api/participants/by-rfid returned status {}", status);
            }
            return Collections.emptyList();
        } catch (Exception e) {
            logger.error("GET /api/participants/by-rfid failed: {}", e.getMessage());
            return null; // null = error (vs empty = not found)
        }
    }

    public static boolean pushRawReads(String serverAddress, List<RawChipRead> reads) {
        if (reads == null || reads.isEmpty()) return true;
        String url = baseUrl(serverAddress) + "/api/reads";
        List<RawChipReadDto> dtos = new ArrayList<>(reads.size());
        for (RawChipRead r : reads) {
            dtos.add(toDto(r));
        }
        String body = gson.toJson(dtos);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            post.setHeader("Content-Type", "application/json");
            post.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
            try (CloseableHttpResponse response = client.execute(post)) {
                int status = response.getStatusLine().getStatusCode();
                if (status == 200 || status == 204) return true;
                logger.warn("POST /api/reads returned status {}", status);
                return false;
            }
        } catch (Exception e) {
            logger.error("POST /api/reads failed: {}", e.getMessage());
            return false;
        }
    }

    public static List<String> getCheckpoints(String serverAddress) {
        String url = baseUrl(serverAddress) + "/api/checkpoints";
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(new HttpGet(url))) {
            int status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                return gson.fromJson(
                        EntityUtils.toString(response.getEntity()),
                        new TypeToken<List<String>>(){}.getType());
            }
            logger.warn("GET /api/checkpoints returned status {}", status);
            return Collections.emptyList();
        } catch (Exception e) {
            logger.error("GET /api/checkpoints failed: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public static SnapshotDto downloadSnapshot(String serverAddress) {
        String url = baseUrl(serverAddress) + "/api/snapshot";
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(new HttpGet(url))) {
            int status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                return gson.fromJson(EntityUtils.toString(response.getEntity()), SnapshotDto.class);
            }
            logger.warn("GET /api/snapshot returned status {}", status);
            return null;
        } catch (Exception e) {
            logger.error("GET /api/snapshot failed: {}", e.getMessage());
            return null;
        }
    }

    private static RawChipReadDto toDto(RawChipRead r) {
        RawChipReadDto dto = new RawChipReadDto();
        dto.setRfidString(r.getRfidString());
        dto.setTimeMillis(r.getTimeMillis());
        dto.setCheckPoint(r.getCheckPoint());
        dto.setLoadName(r.getLoadName());
        dto.setReadType(r.getReadType());
        dto.setDevice(r.getDevice());
        dto.setMobileApp(r.getMobileApp());
        dto.setChipNumber(r.getChipNumber());
        dto.setDistance(r.getDistance());
        dto.setCalories(r.getCalories());
        dto.setSteps(r.getSteps());
        dto.setRunTime(r.getRunTime());
        return dto;
    }

    private static String encode(String s) {
        try {
            return java.net.URLEncoder.encode(s, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            return s;
        }
    }
}
