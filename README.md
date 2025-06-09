# 📚 UTEBookStore

UTEBookStore là dự án website bán sách trực tuyến được phát triển bằng Spring Boot. Ứng dụng cung cấp đầy đủ chức năng cho người mua, người bán và quản trị viên.

## ✨ Tính năng

### 👥 Người dùng
- Đăng ký tài khoản với xác thực OTP qua email
- Đăng nhập, quản lý thông tin cá nhân và đổi mật khẩu
- Quản lý địa chỉ giao hàng
- Duyệt và tìm kiếm sách theo danh mục
- Thêm sản phẩm vào giỏ, cập nhật số lượng và thanh toán
- Theo dõi trạng thái đơn hàng và xem lịch sử mua
- Đánh giá, bình luận sách và nhận thông báo
- Chat trực tiếp với cửa hàng qua WebSocket

### 🏪 Người bán
- Bảng điều khiển thống kê doanh thu, đơn hàng và đánh giá
- Quản lý thông tin cửa hàng và logo (lưu trên Cloudinary)
- Thêm, sửa, xóa và tìm kiếm sản phẩm
- Tạo khuyến mãi giảm giá hoặc voucher cho sách
- Quản lý đơn hàng của cửa hàng và trò chuyện với khách

### 👑 Quản trị viên
- Thống kê tổng quan người dùng, cửa hàng, sách và khuyến mãi
- Quản lý người dùng (tạo, sửa, khóa, phân quyền, xuất Excel)
- Quản lý cửa hàng và phê duyệt hoạt động
- Quản lý danh mục sách và theo dõi đơn hàng toàn hệ thống

## 🛠️ Công nghệ sử dụng
- **Backend:** Spring Boot 3, Spring Data JPA
- **Frontend:** Thymeleaf, Bootstrap
- **Cơ sở dữ liệu:** MySQL (hỗ trợ SQL Server)
- **Lưu trữ ảnh:** Cloudinary
- **Giao tiếp thời gian thực:** WebSocket (STOMP)
- **Thư điện tử:** JavaMailSender
- **Xuất Excel:** Apache POI

## 💻 Yêu cầu hệ thống
- JDK 17 trở lên
- Maven 3.8+ (có sẵn `mvnw`)
- MySQL 8.0

## 🚀 Cài đặt
1. Clone mã nguồn:
   ```bash
   git clone https://github.com/your-username/UTEBookStore.git
   ```
2. Tạo cơ sở dữ liệu `ute-bookstore` và chỉnh sửa `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/ute-bookstore
   spring.datasource.username=YOUR_DB_USER
   spring.datasource.password=YOUR_DB_PASSWORD
   spring.mail.username=YOUR_EMAIL
   spring.mail.password=YOUR_MAIL_PASSWORD
   server.port=9090
   ```
3. Biên dịch và chạy:
   ```bash
   ./mvnw spring-boot:run
   ```
4. Truy cập ứng dụng tại `http://localhost:9090`

## 👥 Tác giả
- Nguyễn Công Đôn
- Nguyễn Tấn Hùng
- Mai Hồng Hải
- Nguyễn Ngọc Hiếu Hảo

Liên hệ: **laptrinhwebnhom11@gmail.com**

---
© 2024 UTEBookStore. All rights reserved.
