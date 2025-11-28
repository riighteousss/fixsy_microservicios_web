package com.fixsy.productos.repository;

import com.fixsy.productos.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Buscar productos activos
    List<Product> findByIsActiveTrue();
    
    // Buscar productos destacados
    List<Product> findByIsFeaturedTrueAndIsActiveTrue();
    
    // Buscar por categoría
    List<Product> findByCategoriaAndIsActiveTrue(String categoria);
    
    // Buscar por marca
    List<Product> findByMarcaAndIsActiveTrue(String marca);
    
    // Buscar por SKU
    Optional<Product> findBySku(String sku);
    boolean existsBySku(String sku);
    
    // Buscar productos con stock
    List<Product> findByStockGreaterThanAndIsActiveTrue(Integer minStock);
    
    // Buscar productos sin stock
    List<Product> findByStockLessThanEqualAndIsActiveTrue(Integer maxStock);
    
    // Buscar productos en oferta
    @Query("SELECT p FROM Product p WHERE p.precioOferta IS NOT NULL AND p.isActive = true")
    List<Product> findProductsOnSale();
    
    // Buscar por nombre (contiene)
    @Query("SELECT p FROM Product p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND p.isActive = true")
    List<Product> searchByNombre(@Param("searchTerm") String searchTerm);
    
    // Buscar por tags (contiene)
    @Query("SELECT p FROM Product p WHERE LOWER(p.tags) LIKE LOWER(CONCAT('%', :tag, '%')) AND p.isActive = true")
    List<Product> findByTag(@Param("tag") String tag);
    
    // Contar productos por categoría
    Long countByCategoriaAndIsActiveTrue(String categoria);
    
    // Obtener categorías únicas
    @Query("SELECT DISTINCT p.categoria FROM Product p WHERE p.categoria IS NOT NULL AND p.isActive = true")
    List<String> findAllCategorias();
    
    // Obtener marcas únicas
    @Query("SELECT DISTINCT p.marca FROM Product p WHERE p.marca IS NOT NULL AND p.isActive = true")
    List<String> findAllMarcas();
}

