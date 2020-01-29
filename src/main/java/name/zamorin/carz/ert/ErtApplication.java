package name.zamorin.carz.ert;

import name.zamorin.carz.model.*;
import name.zamorin.carz.repo.CarModelRepository;
import name.zamorin.carz.repo.CustomerRepository;
import name.zamorin.carz.repo.OfficeRepository;
import name.zamorin.carz.repo.RentRepository;
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
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

@SpringBootApplication
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = "name.zamorin.carz.repo")
@EnableTransactionManagement
@ComponentScan(basePackages = {"name.zamorin.carz"})
@EntityScan(basePackages = "name.zamorin.carz.model")
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
        CarModel cm1 = carModelRepo.save(CarModel.builder().name("Honda Accord").build());
        CarModel cm2 = carModelRepo.save(CarModel.builder().name("Mazda CX-5").build());

        Customer cs1 = customerRepo.save(Customer.builder().name("Булочкин Вася").build());
        Customer cs2 = customerRepo.save(Customer.builder().name("Батончиков Петя").build());

        Office of1 = officeRepo.save(Office.builder().name("Perm 1").build());
        Office of2 = officeRepo.save(Office.builder().name("Perm 2").build());

        Instant inst = Instant.now().minus(300, ChronoUnit.DAYS);
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("uuuu-MM-dd")
                        .withZone( ZoneId.systemDefault() );

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
