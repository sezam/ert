package name.zamorin.carz.ert;

import name.zamorin.carz.repo.CarModelRepository;
import name.zamorin.carz.repo.CustomerRepository;
import name.zamorin.carz.repo.RentRepository;
import name.zamorin.carz.model.CarModel;
import name.zamorin.carz.model.Customer;
import name.zamorin.carz.model.Rent;
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

import java.sql.Date;

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

	public static void main(String[] args) {
		SpringApplication.run(ErtApplication.class, args);
	}


	private void initDB(){
		CarModel cm1 = carModelRepo.save(CarModel.builder().name("Honda Accord").build());
		CarModel cm2 = carModelRepo.save(CarModel.builder().name("Mazda CX-5").build());
		CarModel cm3 = carModelRepo.save(CarModel.builder().name("Toyota LC-200").build());

		Customer cs1 = customerRepo.save(Customer.builder().name("Булочкин Вася").build());
		Customer cs2 = customerRepo.save(Customer.builder().name("Батончиков Петя").build());

		rentRepo.save(Rent.builder()
				.carModel(cm1)
				.carNumber("qw123e")
				.customer(cs1)
				.startDate("2019-01-01")
				.endDate("2019-01-11")
				.build());

		rentRepo.save(Rent.builder()
				.carModel(cm2)
				.carNumber("as456d")
				.customer(cs1)
				.startDate("2019-02-05")
				.endDate("2019-02-17")
				.build());
	}

	@Bean
	CommandLineRunner commandLineRunner() {
		return args -> {
			initDB();
		};
	}
}
