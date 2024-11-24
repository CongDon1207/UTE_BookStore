package ute.bookstore.entity;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String phone;
    
    @OneToOne
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