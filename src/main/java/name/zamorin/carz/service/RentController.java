package name.zamorin.carz.service;

import name.zamorin.carz.model.CarEvent;
import name.zamorin.carz.model.Rent;
import name.zamorin.carz.repo.CarModelRepository;
import name.zamorin.carz.repo.CustomerRepository;
import name.zamorin.carz.repo.OfficeRepository;
import name.zamorin.carz.repo.RentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.Arrays;

@Controller
public class RentController {
    private final RentRepository rentRepo;
    private final CarModelRepository carModelRepo;
    private final CustomerRepository customerRepo;
    private final OfficeRepository officeRepo;

    @Autowired
    public RentController(RentRepository rentRepo, CarModelRepository carModelRepo, CustomerRepository customerRepo, OfficeRepository officeRepo) {
        this.rentRepo = rentRepo;
        this.carModelRepo = carModelRepo;
        this.customerRepo = customerRepo;
        this.officeRepo = officeRepo;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("rents", rentRepo.findAll());
        return "index";
    }

    @GetMapping("/newrent")
    public String showRentForm(Rent rent, Model model) {
        model.addAttribute("models", carModelRepo.findAll());
        model.addAttribute("customers", customerRepo.findAll());
        model.addAttribute("offices", officeRepo.findAll());
        model.addAttribute("events", Arrays.asList(CarEvent.values()));
        return "add-rent";
    }

    @PostMapping("/addrent")
    public String addRent(@RequestBody MultiValueMap<String, String> formData,
                          BindingResult result, Model model) throws ParseException {

        Rent rent = Rent.builder()
                .carModel(carModelRepo.findById(Long.valueOf(formData.getFirst("carModel")))
                        .orElseThrow(() -> new IllegalArgumentException("Invalid carModel Id")))
                .customer(customerRepo.findById(Long.valueOf(formData.getFirst("customer")))
                        .orElseThrow(() -> new IllegalArgumentException("Invalid Customer Id")))
                .office(officeRepo.findById(Long.valueOf(formData.getFirst("office")))
                        .orElseThrow(() -> new IllegalArgumentException("Invalid Customer Id")))

                .carNumber(formData.getFirst("carNumber"))
                .eventDate(formData.getFirst("eventDate"))
                .carEvent(CarEvent.valueOf(formData.getFirst("carEvent")))
                .build();
        rentRepo.save(rent);

        model.addAttribute("rents", rentRepo.findAll());
        return "index";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Rent rent = rentRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid rent Id:" + id));
        model.addAttribute("rent", rent);
        model.addAttribute("models", carModelRepo.findAll());
        model.addAttribute("customers", customerRepo.findAll());
        return "update-rent";
    }

    @PostMapping("/update/{id}")
    public String updateRent(@PathVariable("id") long id, @Valid Rent rent, BindingResult result, Model model) {
/*
        if (result.hasErrors()) {
            rent.setId(id);
            return "update-rent";
        }

        rentRepo.save(rent);
*/
        model.addAttribute("rents", rentRepo.findAll());
        return "index";
    }

    @GetMapping("/delete/{id}")
    public String deleteRent(@PathVariable("id") long id, Model model) {
        Rent rent = rentRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid rent Id:" + id));
        rentRepo.delete(rent);
        model.addAttribute("rents", rentRepo.findAll());
        return "index";
    }
}
