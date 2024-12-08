package ute.bookstore.entity;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "shops")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id") 
    private User user;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String phone;
    
    @Column
    private String logo;
    
    @OneToOne(cascade = CascadeType.ALL)  
    @JoinColumn(name = "address_id")
    private Address address;
    
    private Double rating;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @ManyToMany
    @JoinTable(
        name = "shop_books",
        joinColumns = @JoinColumn(name = "shop_id"),
        inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private List<Book> books;
    
    @OneToMany(mappedBy = "shop")
    private List<Order> orders;
    
    @OneToMany(mappedBy = "shop")
    private List<Promotion> promotions;
}