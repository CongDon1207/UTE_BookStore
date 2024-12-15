package ute.bookstore.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
	
	private String email;     // Email hoặc tên đăng nhập
    private String password;  // Mật khẩu
}
