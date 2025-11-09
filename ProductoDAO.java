import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductoDAO {
    
    public static List<Producto> obtenerTodosLosProductos() throws SQLException {
        List<Producto> productos = new ArrayList<>();
        String query = "SELECT * FROM productos ORDER BY nombre";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Producto producto = new Producto(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getBigDecimal("precio"),
                    rs.getInt("stock"),
                    rs.getInt("stock_minimo"),
                    rs.getInt("categoria_id")
                );
                Timestamp ts = rs.getTimestamp("fecha_actualizacion");
                if (ts != null) {
                    producto.setFechaActualizacion(ts.toLocalDateTime());
                }
                productos.add(producto);
            }
        }
        return productos;
    }

    public static List<Producto> obtenerProductosStockBajo() throws SQLException {
        List<Producto> productos = new ArrayList<>();
        String query = "SELECT * FROM productos WHERE stock > 0 AND stock <= stock_minimo ORDER BY stock ASC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Producto producto = new Producto(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getBigDecimal("precio"),
                    rs.getInt("stock"),
                    rs.getInt("stock_minimo"),
                    rs.getInt("categoria_id")
                );
                Timestamp ts = rs.getTimestamp("fecha_actualizacion");
                if (ts != null) {
                    producto.setFechaActualizacion(ts.toLocalDateTime());
                }
                productos.add(producto);
            }
        }
        return productos;
    }

    public static List<Producto> obtenerProductosAgotados() throws SQLException {
        List<Producto> productos = new ArrayList<>();
        String query = "SELECT * FROM productos WHERE stock = 0 ORDER BY nombre";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Producto producto = new Producto(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getBigDecimal("precio"),
                    rs.getInt("stock"),
                    rs.getInt("stock_minimo"),
                    rs.getInt("categoria_id")
                );
                Timestamp ts = rs.getTimestamp("fecha_actualizacion");
                if (ts != null) {
                    producto.setFechaActualizacion(ts.toLocalDateTime());
                }
                productos.add(producto);
            }
        }
        return productos;
    }

    public static void actualizarStock(int productoId, int nuevoStock) throws SQLException {
        String query = "UPDATE productos SET stock = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, nuevoStock);
            pstmt.setInt(2, productoId);
            pstmt.executeUpdate();
        }
    }

    public static BigDecimal obtenerValorTotalInventario() throws SQLException {
        String query = "SELECT SUM(precio * stock) as valor_total FROM productos";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                BigDecimal total = rs.getBigDecimal("valor_total");
                return total != null ? total : BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }
}

