package com.example.petshop22.data.model;

public class CartItem {
    private long id;
    private long userId;
    private long productId;
    private int quantity;
    private Product product;

    public CartItem() {}

    public CartItem(long id, long userId, long productId, int quantity, Product product) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.product = product;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public long getProductId() { return productId; }
    public void setProductId(long productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public double getSubtotal() {
        if (product != null) {
            return product.getPrice() * quantity;
        }
        return 0;
    }
}
