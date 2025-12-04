package com.fixsy.productos.config;

import com.fixsy.productos.model.Product;
import com.fixsy.productos.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Carga productos de ejemplo solo si la tabla está vacía.
 */
@Component
@RequiredArgsConstructor
public class ProductosDataLoader implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() != 0) {
            return;
        }
        seedProducts();
    }

    private void seedProducts() {
        List<Product> products = Arrays.asList(
                product("Filtro de aceite 1.6-2.0L", "Filtro premium para motores gasolinera 1.6 a 2.0L", 9990, 7990,
                        40, "motor,mantenimiento,filtros", "/images/filtros2.webp", "Filtros",
                        "Bosch", "FLT-001", true),
                product("Pastillas de freno delantera", "Juego de pastillas ceramicas delanteras", 34990, 29990,
                        30, "frenos,seguridad", "/images/frenos2.png.jpg", "Frenos",
                        "Brembo", "BRK-010", true),
                product("Amortiguador delantero gas", "Amortiguador gas-oil para uso urbano y carretera", 59990, null,
                        25, "suspension,confort", "/images/suspension.png", "Suspension",
                        "Monroe", "SUS-020", false),
                product("Bateria 60Ah libre mantencion", "Bateria sellada de larga duracion 12V 60Ah", 89990, 82990,
                        18, "electricidad,partida", "/images/electricidad.jpg", "Baterias",
                        "ACDelco", "ELE-030", true),
                product("Aceite sintetico 5W-30 4L", "Aceite sintetico API SN Plus, protege turbo", 25990, 21990,
                        50, "motor,lubricantes", "/images/png-transparent-car-oil-motor-oil-lubricant-engine-twostroke-engine-lubrication-base.png", "Aceites",
                        "Mobil", "OIL-005", true),
                product("Filtro de aire panel", "Filtro de aire alto flujo para motores 1.6-2.4L", 14990, null,
                        35, "motor,aire", "/images/pngtree-truck-fuel-oil-filter-png-image_11484952.png", "Filtros",
                        "Mann", "FLT-015", false),
                product("Bujias iridium x4", "Pack 4 bujias iridium vida util extendida", 32990, 29990,
                        28, "encendido,mantenimiento", "/images/electricidad.jpg", "Electrico",
                        "NGK", "IGN-040", false),
                product("Disco de freno ventilado", "Disco ventilado 280mm con recubrimiento anticorrosion", 45990, null,
                        22, "frenos,seguridad", "/images/png-clipart-car-brake-pad-disc-brake-vehicle-car-car-automobile-repair-shop.png", "Frenos",
                        "Zimmermann", "BRK-050", false),
                product("Kit correa de distribucion", "Kit con tensor y bomba de agua incluido", 119990, 109990,
                        12, "motor,correas", "/images/kit tensor motor.jpg", "Accesorios",
                        "Gates", "ENG-060", true),
                product("Liquido de frenos DOT4 1L", "Punto ebullicion alto, apto ABS", 6990, null,
                        45, "frenos,fluido", "/images/las-mejores-7-marcas-de-los-mejores-liquidos-de-frenos.jpg", "Frenos",
                        "Motul", "BRK-070", false),
                product("Amortiguador trasero gas", "Amortiguador trasero reforzado pick-up", 63990, 57990,
                        16, "suspension,carga", "/images/suspension.png", "Suspension",
                        "KYB", "SUS-080", false),
                product("Filtro de combustible diesel", "Filtro de combustible de alta eficiencia", 19990, null,
                        32, "motor,combustible", "/images/tres-filtros-aceite-motor-automovil_207928-40.avif", "Filtros",
                        "Mahle", "FLT-090", false)
        );

        productRepository.saveAll(products);
    }

    private Product product(String nombre, String descripcion, Number precio, Number precioOferta, int stock,
                            String tags, String imagen, String categoria, String marca, String sku, boolean featured) {
        Product p = new Product();
        p.setNombre(nombre);
        p.setDescripcionCorta(descripcion);
        p.setDescripcionLarga(descripcion);
        p.setPrecioNormal(precio != null ? BigDecimal.valueOf(precio.doubleValue()) : null);
        p.setPrecioOferta(precioOferta != null ? BigDecimal.valueOf(precioOferta.doubleValue()) : null);
        p.setStock(stock);
        p.setTags(Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList());
        p.setImageUrl(imagen);
        p.setCategoria(categoria);
        p.setMarca(marca);
        p.setSku(sku);
        p.setIsFeatured(featured);
        p.setIsActive(true);
        return p;
    }
}
