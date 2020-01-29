package name.zamorin.carz.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name="carModel_id")
    private CarModel carModel;

    @NotNull
    @ManyToOne
    @JoinColumn(name="customer_id")
    private Customer customer;

    @NotNull
    @ManyToOne
    @JoinColumn(name="office_id")
    private Office office;

    @NotBlank
    private String carNumber;

    @NotBlank
    private String eventDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CarEvent carEvent;

}
