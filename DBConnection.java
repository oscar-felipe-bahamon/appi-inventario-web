import java.sql.*;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/inventario_tech?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "root"; // Cambia esto por la contraseña que estableciste en MySQL

    public static Connection getConnection() throws SQLException {
        try {
            // Intentar cargar el driver moderno (MySQL 8+)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e1) {
            try {
                // Fallback al driver antiguo (MySQL 5.x)
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e2) {
                throw new SQLException(
                    "Driver JDBC de MySQL no encontrado. Agrega mysql-connector-j al classpath.", e2
                );
            }
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Crear tabla de categorías
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS categorias (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "nombre VARCHAR(50) NOT NULL," +
                "descripcion TEXT" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci"
            );

            // Crear tabla de productos
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS productos (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "nombre VARCHAR(100) NOT NULL," +
                "descripcion TEXT," +
                "precio DECIMAL(10,2) NOT NULL," +
                "stock INT NOT NULL," +
                "stock_minimo INT NOT NULL," +
                "categoria_id INT," +
                "fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON UPDATE CASCADE ON DELETE SET NULL" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci"
            );

            // Insertar categorías base
            stmt.execute(
                "INSERT IGNORE INTO categorias (id, nombre, descripcion) VALUES " +
                "(1, 'Laptops', 'Computadoras portátiles')," +
                "(2, 'Monitores', 'Pantallas y displays')," +
                "(3, 'Smartphones', 'Teléfonos inteligentes')," +
                "(4, 'Accesorios', 'Periféricos y accesorios')," +
                "(5, 'Componentes', 'Componentes de hardware')"
            );

            // Insertar productos de ejemplo
            stmt.execute(
                "INSERT IGNORE INTO productos (nombre, descripcion, precio, stock, stock_minimo, categoria_id) VALUES " +
                "('Laptop HP Pavilion 15', '15.6\\\", Intel i7, 16GB RAM, 512GB SSD', 899.99, 2, 5, 1)," +
                "('Monitor DELL 27\\\" 4K', 'Monitor UHD 4K, 60Hz, HDR', 449.99, 3, 8, 2)," +
                "('Apple AirPods Pro', 'Auriculares inalámbricos con cancelación de ruido', 249.99, 4, 10, 4)," +
                "('Samsung Galaxy S23 Ultra', '256GB, 12GB RAM, 200MP cámara', 1199.99, 0, 3, 3)," +
                "('NVIDIA RTX 4080', 'Tarjeta gráfica 16GB GDDR6X', 1099.99, 0, 2, 5)"
            );

            System.out.println("Base de datos inicializada correctamente");

        } catch (SQLException e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
        }
    }
}
