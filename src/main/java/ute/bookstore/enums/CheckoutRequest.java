package ute.bookstore.enums;

import java.util.List;

public class CheckoutRequest {
    public List<CheckoutItem> getItems() {
		return items;
	}

	public void setItems(List<CheckoutItem> items) {
		this.items = items;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	private List<CheckoutItem> items;
    private double totalAmount;

    // Getters and Setters

    public static class CheckoutItem {
        public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public int getQuantity() {
			return quantity;
		}
		public void setQuantity(int quantity) {
			this.quantity = quantity;
		}
		public double getPrice() {
			return price;
		}
		public void setPrice(double price) {
			this.price = price;
		}
		public double getTotal() {
			return total;
		}
		public void setTotal(double total) {
			this.total = total;
		}
		 public String getImage() {
	            return image;  // Getter cho hình ảnh
	        }

	        public void setImage(String image) {
	            this.image = image;  // Setter cho hình ảnh
	        }
		private Long id;
        private String title;
        private int quantity;
        private double price;
        private double total;
        private String image;  // Thêm thuộc tính hình ảnh

        // Getters and Setters
    }
}
