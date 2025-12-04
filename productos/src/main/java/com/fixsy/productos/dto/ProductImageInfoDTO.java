package com.fixsy.productos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Representa las URLs publicas de imagenes asociadas a un producto")
public class ProductImageInfoDTO {

    @Schema(description = "ID del producto", example = "1")
    private Long productId;

    @Schema(description = "URL publica de la imagen principal", example = "/images/product_1_main_1701370000000.jpg")
    private String imageUrl;

    @Schema(description = "URLs publicas adicionales (galeria)", example = "[\"/images/product_1_g1_1701370000100.jpg\"]")
    private List<String> images;
}
