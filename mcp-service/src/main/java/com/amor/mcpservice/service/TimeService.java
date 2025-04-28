package com.amor.mcpservice.service;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Service
public class TimeService implements ToolService {


    @Tool(description = "获取当前时间，支持不同时区(默认北京时间)")
    public String getCurrentTime(@ToolParam(description = "可选时区参数（如 Asia/Shanghai）",required = false) String timezone) {
        String TIME_SERVER = "pool.ntp.org";
        NTPUDPClient client = new NTPUDPClient();
        client.setDefaultTimeout(10000);
        TimeInfo timeInfo = null;
        try {
            timeInfo = client.getTime(InetAddress.getByName(TIME_SERVER));
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        timeInfo.computeDetails();
        Instant ntpInstant = Instant.ofEpochMilli(timeInfo.getMessage().getTransmitTimeStamp().getTime());

        ZoneId zoneId = timezone != null ? ZoneId.of(timezone) : ZoneId.of("Asia/Shanghai");
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(zoneId)
                .format(ntpInstant);
    }
}
