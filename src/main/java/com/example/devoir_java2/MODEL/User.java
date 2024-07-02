package com.example.devoir_java2.MODEL;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@Table(name = "users")
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

   @Column()
    private String name;

   @Email
   @Column()
    private String email;

   @Column()
    private String telephone;

   @Column()
    private String password;

   @OneToMany(mappedBy = "user")
   private List<Trajet> trajets;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
}
