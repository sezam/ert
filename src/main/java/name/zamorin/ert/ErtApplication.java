package name.zamorin.ert;

import name.zamorin.ert.model.*;
import name.zamorin.ert.repo.CarModelRepository;
import name.zamorin.ert.repo.CustomerRepository;
import name.zamorin.ert.repo.OfficeRepository;
import name.zamorin.ert.repo.RentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@SpringBootApplication
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = "name.zamorin.ert.repo")
@EnableTransactionManagement
@ComponentScan(basePackages = {"name.zamorin.ert"})
@EntityScan(basePackages = "name.zamorin.ert.model")
public class ErtApplication {
    @Autowired
    private RentRepository rentRepo;
    @Autowired
    private CarModelRepository carModelRepo;
    @Autowired
    private CustomerRepository customerRepo;
    @Autowired
    private OfficeRepository officeRepo;

    public static void main(String[] args) {
        SpringApplication.run(ErtApplication.class, args);
    }


    private void initDB() {
        CarModel cm1 = carModelRepo.save(CarModel.builder().name("Honda CRV").build());
        CarModel cm2 = carModelRepo.save(CarModel.builder().name("Mazda CX-5").build());
        carModelRepo.save(CarModel.builder().name("Toyota LC-200").build());

        Customer cs1 = customerRepo.save(Customer.builder().name("Булочкин Вася").build());
        Customer cs2 = customerRepo.save(Customer.builder().name("Батончиков Петя").build());

        Office of1 = officeRepo.save(Office.builder().name("Perm 1").build());
        Office of2 = officeRepo.save(Office.builder().name("Perm 2").build());

        Instant inst = Instant.now().minus(300, ChronoUnit.DAYS);
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("uuuu-MM-dd")
                        .withZone(ZoneId.systemDefault());

        for (int i = 0; i < 20; i++) {
            CarModel car = (Math.random() * 50 > 25 ? cm1 : cm2);
            String cn = car.getName().substring(1, 3) + Math.round(99 + Math.random() * 900);
            Customer cs = (Math.random() * 50 > 25 ? cs1 : cs2);
            inst = inst.plus(Math.round(Math.random() * 3), ChronoUnit.DAYS);

            rentRepo.save(Rent.builder()
                    .carModel(car)
                    .carNumber(cn)
                    .customer(cs)
                    .office(Math.random() * 50 > 25 ? of1 : of2)
                    .carEvent(CarEvent.Rent)
                    .eventDate(formatter.format(inst))
                    .build());

            inst = inst.plus(Duration.ofDays(Math.round(Math.random() * 14)));
            rentRepo.save(Rent.builder()
                    .carModel(car)
                    .carNumber(cn)
                    .customer(cs)
                    .office(Math.random() * 50 > 25 ? of1 : of2)
                    .carEvent(CarEvent.Return)
                    .eventDate(formatter.format(inst))
                    .build());

        }
    }

    @Bean
    CommandLineRunner commandLineRunner() {
        return args -> {
            initDB();
        };
    }
}
