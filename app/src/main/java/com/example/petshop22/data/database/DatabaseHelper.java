package com.example.petshop22.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.petshop22.data.model.Message;
import com.example.petshop22.data.model.CartItem;
import com.example.petshop22.data.model.Order;
import com.example.petshop22.data.model.OrderItem;
import com.example.petshop22.data.model.Product;
import com.example.petshop22.data.model.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "petshop22.db";
    private static final int DATABASE_VERSION = 7;

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "full_name TEXT NOT NULL, " +
                "email TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL, " +
                "phone TEXT DEFAULT '')");
        db.execSQL("CREATE TABLE messages (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "userId INTEGER," +
                "productId INTEGER," +
                "message TEXT," +
                "sender TEXT," +
                "timestamp INTEGER)");
        db.execSQL("CREATE TABLE products (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "description TEXT DEFAULT '', " +
                "price REAL NOT NULL DEFAULT 0, " +
                "category TEXT DEFAULT '', " +
                "image_url TEXT DEFAULT '', " +
                "stock INTEGER DEFAULT 0)");

        db.execSQL("CREATE TABLE cart_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "product_id INTEGER NOT NULL, " +
                "quantity INTEGER DEFAULT 1, " +
                "FOREIGN KEY (user_id) REFERENCES users(id), " +
                "FOREIGN KEY (product_id) REFERENCES products(id))");

        db.execSQL("CREATE TABLE orders (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "customer_name TEXT NOT NULL, " +
                "customer_phone TEXT NOT NULL, " +
                "shipping_address TEXT NOT NULL, " +
                "payment_method TEXT NOT NULL, " +
                "total_amount REAL NOT NULL, " +
                "status TEXT DEFAULT 'pending', " +
                "created_at INTEGER NOT NULL, " +
                "FOREIGN KEY (user_id) REFERENCES users(id))");

        db.execSQL("CREATE TABLE order_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "order_id INTEGER NOT NULL, " +
                "product_id INTEGER NOT NULL, " +
                "product_name TEXT NOT NULL, " +
                "quantity INTEGER NOT NULL, " +
                "unit_price REAL NOT NULL, " +
                "FOREIGN KEY (order_id) REFERENCES orders(id))");

        insertSampleProducts(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Giữ lại bảng users để không mất tài khoản đã đăng ký
        db.execSQL("CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "full_name TEXT NOT NULL, " +
                "email TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL, " +
                "phone TEXT DEFAULT '')");

        // Chỉ xóa và tạo lại các bảng khác
        db.execSQL("DROP TABLE IF EXISTS messages");
        db.execSQL("DROP TABLE IF EXISTS order_items");
        db.execSQL("DROP TABLE IF EXISTS orders");
        db.execSQL("DROP TABLE IF EXISTS cart_items");
        db.execSQL("DROP TABLE IF EXISTS products");

        db.execSQL("CREATE TABLE messages (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "userId INTEGER," +
                "productId INTEGER," +
                "message TEXT," +
                "sender TEXT," +
                "timestamp INTEGER)");
        db.execSQL("CREATE TABLE products (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "description TEXT DEFAULT '', " +
                "price REAL NOT NULL DEFAULT 0, " +
                "category TEXT DEFAULT '', " +
                "image_url TEXT DEFAULT '', " +
                "stock INTEGER DEFAULT 0)");

        db.execSQL("CREATE TABLE cart_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "product_id INTEGER NOT NULL, " +
                "quantity INTEGER DEFAULT 1, " +
                "FOREIGN KEY (user_id) REFERENCES users(id), " +
                "FOREIGN KEY (product_id) REFERENCES products(id))");

        db.execSQL("CREATE TABLE orders (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "customer_name TEXT NOT NULL, " +
                "customer_phone TEXT NOT NULL, " +
                "shipping_address TEXT NOT NULL, " +
                "payment_method TEXT NOT NULL, " +
                "total_amount REAL NOT NULL, " +
                "status TEXT DEFAULT 'pending', " +
                "created_at INTEGER NOT NULL, " +
                "FOREIGN KEY (user_id) REFERENCES users(id))");

        db.execSQL("CREATE TABLE order_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "order_id INTEGER NOT NULL, " +
                "product_id INTEGER NOT NULL, " +
                "product_name TEXT NOT NULL, " +
                "quantity INTEGER NOT NULL, " +
                "unit_price REAL NOT NULL, " +
                "FOREIGN KEY (order_id) REFERENCES orders(id))");

        insertSampleProducts(db);
    }
    public void insertMessage(long userId, long productId, String message, String sender) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("userId", userId);
        values.put("productId", productId);
        values.put("message", message);
        values.put("sender", sender);
        values.put("timestamp", System.currentTimeMillis());

        db.insert("messages", null, values);
    }
    private void insertSampleProducts(SQLiteDatabase db) {
        insertProduct(db, "Royal Canin - Thức ăn chó trưởng thành",
                "Thức ăn hạt cao cấp dành cho chó trưởng thành, giàu dinh dưỡng, hỗ trợ tiêu hóa tốt. Trọng lượng 2kg.",
                350000, "Thức ăn", "https://images.unsplash.com/photo-1697422836692-043e0fee1fd5?w=400&q=80", 50);
        insertProduct(db, "Whiskas - Thức ăn mèo vị cá",
                "Thức ăn cho mèo vị cá biển thơm ngon, bổ sung vitamin và khoáng chất thiết yếu. Trọng lượng 1.2kg.",
                180000, "Thức ăn", "https://www.lottemart.vn/media/catalog/product/cache/0x0/8/8/8853301550048.jpg.webp", 80);
        insertProduct(db, "Pedigree - Thức ăn chó con",
                "Thức ăn dành riêng cho chó con dưới 12 tháng tuổi, hỗ trợ phát triển xương và răng. Trọng lượng 1.5kg.",
                250000, "Thức ăn", "https://images.unsplash.com/photo-1764249453855-2e575db33354?w=400&q=80", 60);
        insertProduct(db, "Me-O - Thức ăn mèo hải sản",
                "Thức ăn cho mèo vị hải sản tổng hợp, giàu Omega 3 & 6 giúp lông mượt. Trọng lượng 1.1kg.",
                160000, "Thức ăn", "https://images.unsplash.com/photo-1694371292618-ca1931ae230e?w=400&q=80", 45);
        insertProduct(db, "Bóng cao su cho chó",
                "Bóng cao su đặc bền bỉ, an toàn cho chó nhai và chơi. Đường kính 7cm, màu sắc ngẫu nhiên.",
                45000, "Đồ chơi", "https://images.unsplash.com/photo-1758543535656-4413fc336b78?w=400&q=80", 100);
        insertProduct(db, "Cần câu đồ chơi cho mèo",
                "Cần câu có lông vũ và chuông nhỏ thu hút mèo, giúp mèo vận động và giải trí. Dài 40cm.",
                65000, "Đồ chơi", "https://images.unsplash.com/photo-1572513736580-4ed300379b05?w=400&q=80", 70);
        insertProduct(db, "Vòng cổ cho chó",
                "Vòng cổ nylon bền đẹp có khóa an toàn, điều chỉnh được kích thước. Phù hợp chó nhỏ và vừa.",
                85000, "Phụ kiện", "https://images.unsplash.com/photo-1626653136079-13c09cfb2a4a?w=400&q=80", 90);
        insertProduct(db, "Dây dắt chó đi dạo",
                "Dây dắt co giãn tự động, dài tối đa 5m, có nút khóa an toàn. Phù hợp chó dưới 20kg.",
                120000, "Phụ kiện", "https://images.unsplash.com/photo-1716500618201-7afa1ee4d79b?w=400&q=80", 55);
        insertProduct(db, "Sữa tắm cho chó mèo",
                "Sữa tắm thảo dược nhẹ nhàng, khử mùi, diệt khuẩn và làm mềm lông. Dung tích 500ml.",
                95000, "Chăm sóc", "https://images.unsplash.com/photo-1647002380358-fc70ed2f04e0?w=400&q=80", 65);
        insertProduct(db, "Lược chải lông thú cưng",
                "Lược chải lông inox chống gỉ, giúp gỡ rối và loại bỏ lông rụng hiệu quả.",
                75000, "Chăm sóc", "https://images.unsplash.com/photo-1647002380358-fc70ed2f04e0?w=400&q=80", 40);
        insertProduct(db, "Nhà cho mèo",
                "Nhà mèo bằng gỗ thiết kế hiện đại, có đệm lót êm ái. Kích thước 40x35x35cm.",
                450000, "Nhà ở", "https://images.unsplash.com/photo-1653828950828-ff635393f11d?w=400&q=80", 20);
        insertProduct(db, "Khay vệ sinh cho mèo",
                "Khay vệ sinh nhựa PP cao cấp, dễ vệ sinh, có nắp chống mùi. Kích thước 45x35x15cm.",
                180000, "Nhà ở", "https://images.unsplash.com/photo-1727510153658-643787acb16a?w=400&q=80", 35);
    }

    private void insertProduct(SQLiteDatabase db, String name, String desc,
                               double price, String category, String imageUrl, int stock) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("description", desc);
        values.put("price", price);
        values.put("category", category);
        values.put("image_url", imageUrl);
        values.put("stock", stock);
        db.insert("products", null, values);
    }

    // ==================== USER ====================

    public long registerUser(String fullName, String email, String password, String phone) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("full_name", fullName);
        values.put("email", email);
        values.put("password", password);
        values.put("phone", phone);
        return db.insert("users", null, values);
    }

    public User loginUser(String email, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("users", null,
                "email = ? AND password = ?", new String[]{email, password},
                null, null, null);
        User user = null;
        if (cursor.moveToFirst()) {
            user = cursorToUser(cursor);
        }
        cursor.close();
        return user;
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("users", new String[]{"id"},
                "email = ?", new String[]{email}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    private User cursorToUser(Cursor c) {
        User user = new User();
        user.setId(c.getLong(c.getColumnIndexOrThrow("id")));
        user.setFullName(c.getString(c.getColumnIndexOrThrow("full_name")));
        user.setEmail(c.getString(c.getColumnIndexOrThrow("email")));
        user.setPassword(c.getString(c.getColumnIndexOrThrow("password")));
        user.setPhone(c.getString(c.getColumnIndexOrThrow("phone")));
        return user;
    }

    // ==================== PRODUCT ====================

    public List<Product> getAllProducts() {
        return queryProducts(null, null);
    }

    public List<Product> getProductsByCategory(String category) {
        return queryProducts("category = ?", new String[]{category});
    }

    public List<Product> searchProducts(String query) {

        query = removeAccent(query.toLowerCase());

        List<Product> allProducts = getAllProducts();

        List<Product> result = new ArrayList<>();

        for (Product p : allProducts) {

            String name = removeAccent(p.getName().toLowerCase());

            if (name.contains(query)) {
                result.add(p);
            }
        }

        return result;
    }
    public List<Message> getMessages(long userId, long productId) {

        List<Message> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM messages WHERE userId=? AND productId=? ORDER BY timestamp",
                new String[]{String.valueOf(userId), String.valueOf(productId)}
        );

        while (cursor.moveToNext()) {

            Message m = new Message();

            m.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
            m.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow("userId")));
            m.setProductId(cursor.getLong(cursor.getColumnIndexOrThrow("productId")));
            m.setMessage(cursor.getString(cursor.getColumnIndexOrThrow("message")));
            m.setSender(cursor.getString(cursor.getColumnIndexOrThrow("sender")));
            m.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")));

            list.add(m);
        }

        cursor.close();

        return list;
    }
    public static String removeAccent(String s) {
        s = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD);
        return s.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    public Product getProductById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("products", null,
                "id = ?", new String[]{String.valueOf(id)}, null, null, null);
        Product product = null;
        if (cursor.moveToFirst()) {
            product = cursorToProduct(cursor);
        }
        cursor.close();
        return product;
    }

    public List<String> getCategories() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT DISTINCT category FROM products ORDER BY category", null);
        List<String> categories = new ArrayList<>();
        while (cursor.moveToNext()) {
            categories.add(cursor.getString(0));
        }
        cursor.close();
        return categories;
    }

    private List<Product> queryProducts(String selection, String[] args) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("products", null,
                selection, args, null, null, "name ASC");
        List<Product> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            list.add(cursorToProduct(cursor));
        }
        cursor.close();
        return list;
    }

    private Product cursorToProduct(Cursor c) {
        Product p = new Product();
        p.setId(c.getLong(c.getColumnIndexOrThrow("id")));
        p.setName(c.getString(c.getColumnIndexOrThrow("name")));
        p.setDescription(c.getString(c.getColumnIndexOrThrow("description")));
        p.setPrice(c.getDouble(c.getColumnIndexOrThrow("price")));
        p.setCategory(c.getString(c.getColumnIndexOrThrow("category")));
        p.setImageUrl(c.getString(c.getColumnIndexOrThrow("image_url")));
        p.setStock(c.getInt(c.getColumnIndexOrThrow("stock")));
        return p;
    }

    // ==================== CART ====================

    public long addToCart(long userId, long productId, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query("cart_items", null,
                "user_id = ? AND product_id = ?",
                new String[]{String.valueOf(userId), String.valueOf(productId)},
                null, null, null);
        long result;
        if (cursor.moveToFirst()) {
            int currentQty = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
            long cartId = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            ContentValues values = new ContentValues();
            values.put("quantity", currentQty + quantity);
            db.update("cart_items", values, "id = ?", new String[]{String.valueOf(cartId)});
            result = cartId;
        } else {
            ContentValues values = new ContentValues();
            values.put("user_id", userId);
            values.put("product_id", productId);
            values.put("quantity", quantity);
            result = db.insert("cart_items", null, values);
        }
        cursor.close();
        return result;
    }

    public List<CartItem> getCartItems(long userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT c.id, c.user_id, c.product_id, c.quantity, " +
                        "p.name, p.description, p.price, p.category, p.image_url, p.stock " +
                        "FROM cart_items c INNER JOIN products p ON c.product_id = p.id " +
                        "WHERE c.user_id = ?",
                new String[]{String.valueOf(userId)});
        List<CartItem> items = new ArrayList<>();
        while (cursor.moveToNext()) {
            Product product = new Product();
            product.setId(cursor.getLong(cursor.getColumnIndexOrThrow("product_id")));
            product.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            product.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
            product.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
            product.setCategory(cursor.getString(cursor.getColumnIndexOrThrow("category")));
            product.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow("image_url")));
            product.setStock(cursor.getInt(cursor.getColumnIndexOrThrow("stock")));

            CartItem item = new CartItem();
            item.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
            item.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow("user_id")));
            item.setProductId(cursor.getLong(cursor.getColumnIndexOrThrow("product_id")));
            item.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow("quantity")));
            item.setProduct(product);
            items.add(item);
        }
        cursor.close();
        return items;
    }

    public void updateCartItemQuantity(long cartId, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        if (quantity <= 0) {
            db.delete("cart_items", "id = ?", new String[]{String.valueOf(cartId)});
        } else {
            ContentValues values = new ContentValues();
            values.put("quantity", quantity);
            db.update("cart_items", values, "id = ?", new String[]{String.valueOf(cartId)});
        }
    }

    public void removeFromCart(long cartId) {
        getWritableDatabase().delete("cart_items", "id = ?", new String[]{String.valueOf(cartId)});
    }

    public void clearCart(long userId) {
        getWritableDatabase().delete("cart_items", "user_id = ?", new String[]{String.valueOf(userId)});
    }

    public int getCartItemCount(long userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COALESCE(SUM(quantity), 0) FROM cart_items WHERE user_id = ?",
                new String[]{String.valueOf(userId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // ==================== ORDER ====================

    public long createOrder(long userId, String customerName, String customerPhone,
                            String shippingAddress, String paymentMethod,
                            List<CartItem> cartItems, double totalAmount) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues orderValues = new ContentValues();
        orderValues.put("user_id", userId);
        orderValues.put("customer_name", customerName);
        orderValues.put("customer_phone", customerPhone);
        orderValues.put("shipping_address", shippingAddress);
        orderValues.put("payment_method", paymentMethod);
        orderValues.put("total_amount", totalAmount);
        orderValues.put("status", "confirmed");
        orderValues.put("created_at", System.currentTimeMillis());

        long orderId = db.insert("orders", null, orderValues);

        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            if (product == null) continue;
            ContentValues itemValues = new ContentValues();
            itemValues.put("order_id", orderId);
            itemValues.put("product_id", product.getId());
            itemValues.put("product_name", product.getName());
            itemValues.put("quantity", item.getQuantity());
            itemValues.put("unit_price", product.getPrice());
            db.insert("order_items", null, itemValues);
        }
        return orderId;
    }

    public List<Order> getOrdersByUserId(long userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("orders", null,
                "user_id = ?", new String[]{String.valueOf(userId)},
                null, null, "created_at DESC");
        List<Order> orders = new ArrayList<>();
        while (cursor.moveToNext()) {
            Order order = new Order();
            long orderId = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            order.setId(orderId);
            order.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow("user_id")));
            order.setCustomerName(cursor.getString(cursor.getColumnIndexOrThrow("customer_name")));
            order.setCustomerPhone(cursor.getString(cursor.getColumnIndexOrThrow("customer_phone")));
            order.setShippingAddress(cursor.getString(cursor.getColumnIndexOrThrow("shipping_address")));
            order.setPaymentMethod(cursor.getString(cursor.getColumnIndexOrThrow("payment_method")));
            order.setTotalAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("total_amount")));
            order.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
            order.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow("created_at")));
            order.setItems(getOrderItems(orderId));
            orders.add(order);
        }
        cursor.close();
        return orders;
    }

    private List<OrderItem> getOrderItems(long orderId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("order_items", null,
                "order_id = ?", new String[]{String.valueOf(orderId)},
                null, null, "id ASC");
        List<OrderItem> items = new ArrayList<>();
        while (cursor.moveToNext()) {
            OrderItem item = new OrderItem();
            item.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
            item.setOrderId(cursor.getLong(cursor.getColumnIndexOrThrow("order_id")));
            item.setProductId(cursor.getLong(cursor.getColumnIndexOrThrow("product_id")));
            item.setProductName(cursor.getString(cursor.getColumnIndexOrThrow("product_name")));
            item.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow("quantity")));
            item.setUnitPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("unit_price")));
            items.add(item);
        }
        cursor.close();
        return items;
    }
}
