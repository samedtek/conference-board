package com.samedtek.conferenceboard.entitiy;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "auditorium")
@Getter
@Setter
public class Auditorium {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotEmpty(message = "Auditorium name can not be blank.")
    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "auditorium")
    private List<Presentation> presentations;

}

