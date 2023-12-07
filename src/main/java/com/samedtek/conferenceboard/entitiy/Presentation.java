package com.samedtek.conferenceboard.entitiy;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "presentation")
@Getter
@Setter
public class Presentation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotEmpty(message = "Presentation Title can not be blank.")
    @Column(name = "title")
    private String title;

    @Min(value = 5, message = "Min presentation duration greater than 5 minutes.")
    @Max(value = 180, message = "Max presentation duration less than 180 minutes.")
    @Column(name = "duration")
    private Integer duration;

    @Column(name = "start_time")
    private String startTime;

    @Column(name = "end_time")
    private String endTime;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "auditorium_id", referencedColumnName = "id")
    private Auditorium auditorium;

}
