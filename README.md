# BookHaven - Nền tảng cửa hàng sách trực tuyến

[![Phiên bản Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen.svg)](https://spring.io/projects/spring-boot)

Nền tảng cửa hàng sách trực tuyến đầy đủ tính năng được xây dựng bằng Spring Boot cung cấp các giải pháp hoàn chỉnh cho những người yêu sách, người bán và quản trị viên.

## ✨ Tính năng chính

### 👨💻 Bảng điều khiển quản trị
- Hệ thống phê duyệt yêu cầu gói hàng của cửa hàng
- Quản lý tài khoản: Tạm dừng/Xóa
- Tạo và phân phối phiếu giảm giá
- Quản lý danh mục (Danh mục/Nhà xuất bản)
- Quản lý shipper
- Thống kê hoa hồng đơn hàng
- Giám sát nhật ký hệ thống
- Hệ thống thông báo tự động

### 👤 Tính năng dành cho khách hàng
- Duyệt sản phẩm bằng bộ lọc nâng cao (Cửa hàng/Tác giả/Danh mục)
- Trò chuyện thời gian thực với các cửa hàng
- Quản lý giỏ hàng
- Quản lý đơn hàng (Mua hàng/Hủy/Lịch sử)
- Đánh giá và xếp hạng sản phẩm
- Quản lý sổ địa chỉ
- Theo dõi lịch sử tìm kiếm
- Nâng cấp tài khoản lên Người bán

### 🏪 Quản lý cửa hàng
- Đăng ký/gia hạn/hủy gói hàng
- Quản lý sản phẩm (hoạt động CRUD)
- Hệ thống xử lý đơn hàng
- Xử lý phản hồi của khách hàng
- Thống kê và báo cáo bán hàng
- Quản lý tác giả
- Quản lý phiếu giảm giá và khuyến mại

### 🔒 Hệ thống tài khoản
- Xác thực dựa trên JWT
- Kiểm soát truy cập dựa trên vai trò (Spring Security)
- Xác minh email
- Theo dõi lịch sử đăng nhập
- Thông báo phát hiện bất thường
- Chức năng đặt lại mật khẩu
- Quản lý hồ sơ người dùng

### ⚡ Tính năng thời gian thực
- Hệ thống trò chuyện dựa trên WebSocket
- ​​Hệ thống thông báo tức thì
- Cập nhật đơn hàng trực tiếp

## 🛠 Công nghệ Stack
- **Backend**:
- Java 17 + Spring Boot 3.4.3
- Spring Security + Xác thực JWT
- Spring Data JPA + Hibernate
- MySQL 8.0 + Di chuyển Flyway
- Lombok + API xác thực
- Spring WebSocket
- ​​Spring Mail

- **API**:
- Thiết kế API RESTful
- Tài liệu OpenAPI 3.0

- **Bảo mật**:
- Sẵn sàng cho HTTPS
- Bảo vệ CSRF
- Quản lý phiên
- Mã hóa mật khẩu

## 🚀 Cài đặt

1. **Điều kiện tiên quyết**:
- Java 17 JDK
- MySQL 8.0+
- Maven 3.6+

2. **Thiết lập cơ sở dữ liệu**:
Truy cập src/main/resource/db/migration để tải cơ sở dữ liệu

## 🚀 Chạy
Truy cập http://localhost:8081/swagger-ui/index.html để xem các api
Truy cập http://localhost:8081/customer_chat.html để mở giao diện chat customer
Truy cập http://localhost:8081/shop_chat.html để mở giao diện chat shop

