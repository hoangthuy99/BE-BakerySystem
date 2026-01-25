package com.ra.bakerysystem.model.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class FactoryRequestDTO {
    private Long productId;
    private Integer requestQuantity;
    @JsonProperty("etaAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime etaAt;
    private String note;
}
