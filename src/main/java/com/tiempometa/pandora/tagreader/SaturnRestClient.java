package com.tiempometa.pandora.tagreader;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpMessage;
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
import com.tiempometa.timing.local.LocalDataContext;
import com.tiempometa.timing.local.RawChipReadDto;
import com.tiempometa.timing.local.SnapshotDto;
import com.tiempometa.timing.model.RawChipRead;
import com.tiempometa.webservice.ApiPaths;

/**
 * HTTP client for the saturnPandora REST API (port 9001).
 * All methods are stateless — they derive the URL from the given serverAddress.
 * Every request except {@code GET /api/info} carries {@code X-Base-DB} so the
 * server can reject calls that target the wrong database.
 */
public final class SaturnRestClient {

    private static final Logger logger = LogManager.getLogger(SaturnRestClient.class);
    private static final Gson gson = new Gson();

    private SaturnRestClient() {}

    private static String baseUrl(String serverAddress) {
        return "http://" + serverAddress + ":9001";
    }

    /** Adds X-Base-DB header from the locally paired db name, if known. */
    private static void addDbHeader(HttpMessage request) {
        String dbName = LocalDataContext.getBaseDbName();
        if (dbName != null) request.setHeader("X-Base-DB", dbName);
    }

    public static InfoDto getInfo(String serverAddress) {
        String url = baseUrl(serverAddress) + ApiPaths.INFO;
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
        String url = baseUrl(serverAddress) + ApiPaths.PARTICIPANTS_BY_RFID + encode(rfidString);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(url);
            addDbHeader(get);
            try (CloseableHttpResponse response = client.execute(get)) {
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    return gson.fromJson(
                            EntityUtils.toString(response.getEntity()),
                            new TypeToken<List<ParticipantDetailDto>>(){}.getType());
                }
                if (status == 404) return Collections.emptyList();
                logger.warn("GET /api/participants/by-rfid returned status {}", status);
                return null; // 409 db_mismatch or other error → H2 fallback + markRestDisconnected
            }
        } catch (Exception e) {
            logger.error("GET /api/participants/by-rfid failed: {}", e.getMessage());
            return null;
        }
    }

    public static List<ParticipantDetailDto> findParticipantByBib(
            String serverAddress, String bib) {
        String url = baseUrl(serverAddress) + ApiPaths.PARTICIPANTS_BY_BIB + encode(bib);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(url);
            addDbHeader(get);
            try (CloseableHttpResponse response = client.execute(get)) {
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    return gson.fromJson(
                            EntityUtils.toString(response.getEntity()),
                            new TypeToken<List<ParticipantDetailDto>>(){}.getType());
                }
                if (status == 404) return Collections.emptyList();
                logger.warn("GET /api/participants/by-bib returned status {}", status);
                return null;
            }
        } catch (Exception e) {
            logger.error("GET /api/participants/by-bib failed: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Returns {@code null} on success; the server's db name on 409 mismatch; {@code ""} on other failure.
     */
    public static String pushRawReads(String serverAddress, List<RawChipRead> reads) {
        if (reads == null || reads.isEmpty()) return null;
        String url = baseUrl(serverAddress) + ApiPaths.READS;
        List<RawChipReadDto> dtos = new ArrayList<>(reads.size());
        for (RawChipRead r : reads) {
            dtos.add(toDto(r));
        }
        String body = gson.toJson(dtos);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            post.setHeader("Content-Type", "application/json");
            addDbHeader(post);
            post.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
            try (CloseableHttpResponse response = client.execute(post)) {
                int status = response.getStatusLine().getStatusCode();
                if (status == 200 || status == 204) return null;
                if (status == 409) {
                    logger.error("POST /api/reads rejected — X-Base-DB mismatch; reads held in local H2");
                    try {
                        String responseBody = EntityUtils.toString(response.getEntity());
                        com.google.gson.JsonObject json = gson.fromJson(responseBody, com.google.gson.JsonObject.class);
                        return json.has("server") ? json.get("server").getAsString() : "(desconocida)";
                    } catch (Exception ignored) {
                        return "(desconocida)";
                    }
                }
                logger.warn("POST /api/reads returned status {}", status);
                return "";
            }
        } catch (Exception e) {
            logger.error("POST /api/reads failed: {}", e.getMessage());
            return "";
        }
    }

    public static List<String> getCheckpoints(String serverAddress) {
        String url = baseUrl(serverAddress) + ApiPaths.CHECKPOINTS;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(url);
            addDbHeader(get);
            try (CloseableHttpResponse response = client.execute(get)) {
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    return gson.fromJson(
                            EntityUtils.toString(response.getEntity()),
                            new TypeToken<List<String>>(){}.getType());
                }
                logger.warn("GET /api/checkpoints returned status {}", status);
                return Collections.emptyList();
            }
        } catch (Exception e) {
            logger.error("GET /api/checkpoints failed: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public static SnapshotDto downloadSnapshot(String serverAddress) {
        String url = baseUrl(serverAddress) + ApiPaths.SNAPSHOT;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(url);
            addDbHeader(get);
            try (CloseableHttpResponse response = client.execute(get)) {
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    return gson.fromJson(EntityUtils.toString(response.getEntity()), SnapshotDto.class);
                }
                logger.warn("GET /api/snapshot returned status {}", status);
                return null;
            }
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
        dto.setChipNumber(r.getChipNumber() == null ? null : r.getChipNumber().toString());
        dto.setDistance(r.getDistance());
        dto.setCalories(r.getCalories() == null ? null : r.getCalories().doubleValue());
        dto.setSteps(r.getSteps() == null ? null : r.getSteps().doubleValue());
        dto.setRunTime(r.getRunTime() == null ? null : r.getRunTime().toString());
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
