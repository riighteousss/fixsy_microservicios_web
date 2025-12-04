package com.fixsy.productos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para actualizar la URL de la imagen principal de un producto")
public class ImageUrlUpdateDTO {
    @Schema(description = "Nueva URL de la imagen principal", example = "https://url.de.tu/imagen.jpg")
    private String newImageUrl;
}
