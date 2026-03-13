package com.example.petshop22.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petshop22.R;
import com.example.petshop22.data.model.Product;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private List<Product> products = new ArrayList<>();
    private final OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public ProductAdapter(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        Context context = holder.itemView.getContext();

        holder.tvProductName.setText(product.getName());
        holder.tvPrice.setText(formatPrice(product.getPrice()));
        holder.tvCategory.setText(product.getCategory());

        int categoryColor = getCategoryColor(product.getCategory());
        holder.tvCategory.setTextColor(categoryColor);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(adjustAlpha(categoryColor, 0.15f));
        bg.setCornerRadius(8f);
        holder.tvCategory.setBackground(bg);

        if (product.getStock() > 0) {
            holder.tvStock.setText(String.format(Locale.getDefault(), "Còn %d sản phẩm", product.getStock()));
            holder.tvStock.setTextColor(context.getColor(R.color.text_secondary));
        } else {
            holder.tvStock.setText("Hết hàng");
            holder.tvStock.setTextColor(context.getColor(R.color.error));
        }

        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .centerCrop()
                .into(holder.imgProduct);

        holder.itemView.setOnClickListener(v -> listener.onProductClick(product));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvCategory, tvProductName, tvPrice, tvStock;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvStock = itemView.findViewById(R.id.tvStock);
        }
    }

    public static String formatPrice(double price) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        DecimalFormat formatter = new DecimalFormat("#,###", symbols);
        return formatter.format(price) + " VNĐ";
    }

    public static int getCategoryColor(String category) {
        switch (category) {
            case "Thức ăn": return Color.parseColor("#FFFF7043");
            case "Đồ chơi": return Color.parseColor("#FF42A5F5");
            case "Phụ kiện": return Color.parseColor("#FFAB47BC");
            case "Chăm sóc": return Color.parseColor("#FF66BB6A");
            case "Nhà ở":   return Color.parseColor("#FF8D6E63");
            default:        return Color.parseColor("#FFFF7043");
        }
    }

    private static int adjustAlpha(int color, float factor) {
        int alpha = Math.round(255 * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }
}
