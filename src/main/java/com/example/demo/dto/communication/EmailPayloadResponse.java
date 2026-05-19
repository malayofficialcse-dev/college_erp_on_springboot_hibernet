package com.example.demo.dto.communication;

import java.util.List;

public record EmailPayloadResponse(
        String subject,
        String body,
        List<String> recipients,
        String audience
) {}
