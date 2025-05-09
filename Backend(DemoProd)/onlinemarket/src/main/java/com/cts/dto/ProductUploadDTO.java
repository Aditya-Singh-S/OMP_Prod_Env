package com.cts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductUploadDTO {
    private String name;
    private String description;
    private String localImagePath;
}