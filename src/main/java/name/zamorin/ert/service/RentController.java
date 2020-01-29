package name.zamorin.ert.service;

import name.zamorin.ert.model.CarEvent;
import name.zamorin.ert.model.Rent;
import name.zamorin.ert.repo.CarModelRepository;
import name.zamorin.ert.repo.CustomerRepository;
import name.zamorin.ert.repo.OfficeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

@Controller
public class RentController {
    private final CarModelRepository carModelRepo;
    private final CustomerRepository customerRepo;
    private final OfficeRepository officeRepo;

    private final String restUrl = "http://localhost:8080/api/rents";

    @Autowired
    public RentController(CarModelRepository carModelRepo,
                          CustomerRepository customerRepo, OfficeRepository officeRepo) {
        this.carModelRepo = carModelRepo;
        this.customerRepo = customerRepo;
        this.officeRepo = officeRepo;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("rents", new RestTemplate().exchange(restUrl, HttpMethod.GET,
                null, new ParameterizedTypeReference<List<Rent>>() {
                }).getBody());
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
    public String addRent(@Valid Rent rent, BindingResult result, Model model) throws ParseException {
        new RestTemplate().exchange(restUrl, HttpMethod.POST, new HttpEntity<Rent>(rent, null),
                new ParameterizedTypeReference<Rent>() {
                });

        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deleteRent(@PathVariable("id") long id, Model model) {
        new RestTemplate().exchange(restUrl + "/" + id, HttpMethod.DELETE, null, String.class);
        return "redirect:/";
    }
}
