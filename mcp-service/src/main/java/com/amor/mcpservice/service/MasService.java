package com.amor.mcpservice.service;

import com.amor.mcpservice.feign.MasSystemFeign;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MasService implements ToolService {

    private final MasSystemFeign masSystemFeign;
}
