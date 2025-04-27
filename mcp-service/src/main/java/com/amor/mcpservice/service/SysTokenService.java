package com.amor.mcpservice.service;

import cn.hutool.core.util.StrUtil;
import com.amor.mcpservice.po.SysToken;
import com.amor.mcpservice.repository.SysTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SysTokenService implements ToolService {

    private final SysTokenRepository sysTokenRepository;
    /**
     * 获取token
     *
     * @return
     */
    public String getToken() {
        String key = "sysA:token";
        String token;

        // 1. 查 MongoDB 里有没有还没过期的 token
        SysToken sysToken = sysTokenRepository.findByKey(key);

        if (sysToken != null && sysToken.getExpireAt().after(new Date())) {
            // token存在且未过期
            token = sysToken.getToken();
        } else {
            // 2. 没有或过期了，重新获取
            token = UUID.randomUUID().toString();
            if (StrUtil.isNotBlank(token)) {
                // 3. 保存到MongoDB（带有效期）
                saveToken(key, token, 20 * 60); // 20分钟
            }
        }

        return token;
    }

    private void saveToken(String key,String token,int expireSeconds) {
        SysToken systemToken = new SysToken();
        systemToken.setToken(token);
        systemToken.setKey(key);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, expireSeconds);
        systemToken.setExpireAt(calendar.getTime());
        sysTokenRepository.save(systemToken);
    }
}
