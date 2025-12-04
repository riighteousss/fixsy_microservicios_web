-- Esquema de referencia para alinear la tabla products con el contrato del frontend Fixsy Parts.
-- No ejecutar automaticamente; revisar y aplicar manualmente en el entorno correspondiente.

-- Crear tabla nueva (ajustar si ya existe):
CREATE TABLE IF NOT EXISTS products (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    slug VARCHAR(255) UNIQUE,
    nombre VARCHAR(255) NOT NULL,
    descripcion_corta TEXT,
    descripcion_larga TEXT,
    precio DECIMAL(10,2) NOT NULL,
    precio_oferta DECIMAL(10,2),
    stock INT NOT NULL DEFAULT 0,
    tags TEXT,
    imagen TEXT,
    images TEXT,
    categoria VARCHAR(100) DEFAULT 'Accesorios',
    marca VARCHAR(100),
    sku VARCHAR(100) NOT NULL UNIQUE,
    is_featured BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME,
    INDEX uk_products_sku (sku)
);

-- Si la tabla ya existe y requiere alineacion, usar ALTER TABLE como referencia:
-- ALTER TABLE products ADD COLUMN slug VARCHAR(255) UNIQUE AFTER id;
-- ALTER TABLE products CHANGE COLUMN descripcion descripcion_corta TEXT;
-- ALTER TABLE products ADD COLUMN descripcion_larga TEXT AFTER descripcion_corta;
-- ALTER TABLE products MODIFY precio DECIMAL(10,2) NOT NULL;
-- ALTER TABLE products ADD COLUMN precio_oferta DECIMAL(10,2) NULL AFTER precio;
-- ALTER TABLE products MODIFY stock INT NOT NULL DEFAULT 0;
-- ALTER TABLE products ADD COLUMN tags TEXT NULL AFTER stock;
-- ALTER TABLE products MODIFY imagen TEXT NULL;
-- ALTER TABLE products ADD COLUMN images TEXT NULL AFTER imagen;
-- ALTER TABLE products MODIFY categoria VARCHAR(100) DEFAULT 'Accesorios';
-- ALTER TABLE products ADD COLUMN is_featured BOOLEAN DEFAULT FALSE AFTER sku;
-- ALTER TABLE products ADD COLUMN is_active BOOLEAN DEFAULT TRUE AFTER is_featured;
-- ALTER TABLE products ADD COLUMN created_at DATETIME NULL AFTER is_active;
-- ALTER TABLE products ADD COLUMN updated_at DATETIME NULL AFTER created_at;

-- Las columnas tags e images se guardan como texto separado por comas para mantener el modelo simple.
