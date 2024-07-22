package com.example.devoir_java2.MODEL;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

   @Column()
    private String name;

   @Column()
    private String email;

   @Column()
    private String telephone;

   @Column()
    private String password;

    public User(String name, String email, String telephone, String password, Role role) {
        this.name = name;
        this.email = email;
        this.telephone = telephone;
        this.password = password;
        this.role = role;
    }

    public User() {

    }

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
   private List<Trajet> trajets;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER)
    private List<Reservation> reservations;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
