package ute.bookstore.controller.seller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ute.bookstore.entity.Promotion;
import ute.bookstore.entity.Shop;
import ute.bookstore.entity.Book;
import ute.bookstore.enums.DiscountType;
import ute.bookstore.service.IBookService;
import ute.bookstore.service.IPromotionService;
import ute.bookstore.service.IShopService;

@Controller
@RequestMapping("/seller/promotions")
public class SellerPromotionController {
	@Autowired
	private IBookService bookService;

	@Autowired
	private IPromotionService promotionService;
	
	@Autowired
	private IShopService shopService;
	
	private static final String DEFAULT_EMAIL = "vendor@gmail.com";

	@GetMapping("/discount")
	public String getDiscountPage(Model model) {
	    Shop shop = shopService.getShopByUserEmail(DEFAULT_EMAIL);
	    List<Book> books = bookService.getAllBooks();
	    List<Promotion> discounts = promotionService.findShopDiscounts(shop);

	    model.addAttribute("books", books);
	    model.addAttribute("discounts", discounts);
	    return "seller/product-discount";
	}

	@PostMapping("/discount/add")
	public String addDiscount(@RequestParam Long bookId, 
	                         @RequestParam DiscountType discountType,
	                         @RequestParam Double discount, 
	                         @RequestParam LocalDateTime startDate, 
	                         @RequestParam LocalDateTime endDate) {

	    // Lấy shop hiện tại
	    Shop shop = shopService.getShopByUserEmail(DEFAULT_EMAIL);
	    Book book = bookService.getBookById(bookId);

	    Promotion promotion = new Promotion();
	    promotion.setBook(book);
	    promotion.setDiscountType(discountType);
	    promotion.setDiscount(discount);
	    promotion.setStartDate(startDate);
	    promotion.setEndDate(endDate);
	    promotion.setIsActive(true);
	    promotion.setShop(shop); // Thêm shop vào promotion

	    promotionService.savePromotion(promotion);
	    return "redirect:/seller/promotions/discount";
	}

	@PostMapping("/discount/{id}/edit")
	public String updateDiscount(@PathVariable Long id,
	                           @RequestParam Long bookId,
	                           @ModelAttribute Promotion promotion) {
	    // Lấy shop và book
	    Shop shop = shopService.getShopByUserEmail(DEFAULT_EMAIL);
	    Book book = bookService.getBookById(bookId);
	    
	    // Lấy promotion cũ 
	    Promotion existingPromotion = promotionService.getPromotionById(id);
	    
	    // Cập nhật thông tin
	    existingPromotion.setBook(book);
	    existingPromotion.setDiscountType(DiscountType.valueOf(promotion.getDiscountType())); // Convert String to enum
	    existingPromotion.setDiscount(promotion.getDiscount());
	    existingPromotion.setStartDate(promotion.getStartDate());
	    existingPromotion.setEndDate(promotion.getEndDate());
	    existingPromotion.setShop(shop);
	    existingPromotion.setIsActive(true);

	    promotionService.updatePromotion(id, existingPromotion);
	    return "redirect:/seller/promotions/discount";
	}

	@PostMapping("/discount/{id}/delete")
	public String deleteDiscount(@PathVariable Long id) {
		promotionService.deletePromotion(id);
		return "redirect:/seller/promotions/discount";
	}


	@GetMapping("/voucher")
	public String getVoucherPage(Model model) {
	    Shop shop = shopService.getShopByUserEmail(DEFAULT_EMAIL); 
	    List<Promotion> vouchers = promotionService.getShopVouchers(shop);
	    model.addAttribute("vouchers", vouchers);
	    return "seller/product-voucher";
	}

	@PostMapping("/voucher/add")
	public String addVoucher(@RequestParam String code, 
	                        @RequestParam Double discount,
	                        @RequestParam Double minOrderAmount, 
	                        @RequestParam Integer maxUses, 
	                        @RequestParam LocalDateTime startDate,
	                        @RequestParam LocalDateTime endDate) {

	    // Lấy shop hiện tại
	    Shop shop = shopService.getShopByUserEmail(DEFAULT_EMAIL);
	    
	    Promotion voucher = new Promotion();
	    voucher.setCode(code);
	    voucher.setDiscount(discount);
	    voucher.setMinOrderAmount(minOrderAmount);
	    voucher.setMaxUses(maxUses);
	    voucher.setStartDate(startDate);
	    voucher.setEndDate(endDate);
	    voucher.setIsActive(true);
	    voucher.setShop(shop); // Thêm dòng này
	    
	    promotionService.savePromotion(voucher);
	    return "redirect:/seller/promotions/voucher";
	}

	@PostMapping("/voucher/{id}/edit") 
	public String updateVoucher(@PathVariable Long id, @ModelAttribute Promotion voucher) {
	    // Lấy shop hiện tại
	    Shop shop = shopService.getShopByUserEmail(DEFAULT_EMAIL);
	    
	    // Set các thông tin cần thiết
	    voucher.setId(id);
	    voucher.setShop(shop);
	    voucher.setIsActive(true);  // Giữ trạng thái active
	    
	    promotionService.updatePromotion(id, voucher);
	    return "redirect:/seller/promotions/voucher";
	}

	@PostMapping("/voucher/{id}/delete")
	public String deleteVoucher(@PathVariable Long id) {
		promotionService.deletePromotion(id);
		return "redirect:/seller/promotions/voucher";
	}
}
