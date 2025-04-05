package com.haven.dto;

public class OrderRequestDTO {
    private Integer customerId;
    private String shipAddress;
    private Integer paymentId;
    private Integer shipperId;
    private Integer voucherId; // Có thể null nếu không dùng voucher
    private boolean buyAll; // True: mua tất cả, False: mua sản phẩm được chọn
    private Integer productId; // Dùng cho mua trực tiếp từ trang chủ
    private Integer quantity;

    // Getters and Setters
    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
    public String getShipAddress() { return shipAddress; }
    public void setShipAddress(String shipAddress) { this.shipAddress = shipAddress; }
    public Integer getPaymentId() { return paymentId; }
    public void setPaymentId(Integer paymentId) { this.paymentId = paymentId; }
    public Integer getShipperId() { return shipperId; }
    public void setShipperId(Integer shipperId) { this.shipperId = shipperId; }
    public Integer getVoucherId() { return voucherId; }
    public void setVoucherId(Integer voucherId) { this.voucherId = voucherId; }
    public boolean isBuyAll() { return buyAll; }
    public void setBuyAll(boolean buyAll) { this.buyAll = buyAll; }
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
    
}