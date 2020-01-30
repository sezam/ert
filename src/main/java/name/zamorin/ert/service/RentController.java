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
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class RentController {
    private final CarModelRepository carModelRepo;
    private final CustomerRepository customerRepo;
    private final OfficeRepository officeRepo;
    private final RestTemplate restTemplate;

    private final String restUrl = "http://localhost:8080/api/rents";

    @Autowired
    public RentController(CarModelRepository carModelRepo, CustomerRepository customerRepo,
                          OfficeRepository officeRepo) {
        this.carModelRepo = carModelRepo;
        this.customerRepo = customerRepo;
        this.officeRepo = officeRepo;
        this.restTemplate = new RestTemplate();
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Rent> rents = restTemplate.exchange(restUrl, HttpMethod.GET,
                null, new ParameterizedTypeReference<List<Rent>>() {
                }).getBody();
        prepareModel(model, rents);
        return "index";
    }

    // сравнение дат
    private Boolean compareDates(String left, String right) {
        LocalDate ld = LocalDate.parse(left);
        LocalDate rd = LocalDate.parse(right);

        return rd.isAfter(ld);
    }

    private void prepareModel(Model model, List<Rent> rents) {
        model.addAttribute("rents", rents);
        model.addAttribute("models", rents.stream().collect(Collectors.groupingBy(Rent::getCarModel)).keySet());
        model.addAttribute("customers", rents.stream().collect(Collectors.groupingBy(Rent::getCustomer)).keySet());
        model.addAttribute("offices", rents.stream().collect(Collectors.groupingBy(Rent::getOffice)).keySet());
        model.addAttribute("carNumbers", rents.stream().collect(Collectors.groupingBy(Rent::getCarNumber)).keySet());
        model.addAttribute("events", rents.stream().collect(Collectors.groupingBy(Rent::getCarEvent)).keySet());
        model.addAttribute("leftDate", "");
        model.addAttribute("rightDate", "");
    }

    @PostMapping("/")
    public String filter(@RequestBody MultiValueMap<String, String> formData, Model model) {
        List<Rent> rents = restTemplate.exchange(restUrl, HttpMethod.GET,
                null, new ParameterizedTypeReference<List<Rent>>() {
                }).getBody();

        // Фильтруем по модели
        String v = formData.getFirst("carModel");
        if (v != null && !v.isEmpty()) {
            String finalV1 = v;
            rents = rents.stream()
                    .filter(rent -> rent.getCarModel().getId() == Long.valueOf(finalV1))
                    .collect(Collectors.toList());
        }

        // Фильтр по номеру
        v = formData.getFirst("carNumber");
        if (v != null && !v.isEmpty()) {
            String finalV = v;
            rents = rents.stream()
                    .filter(rent -> rent.getCarNumber().equalsIgnoreCase(finalV))
                    .collect(Collectors.toList());
        }

        // Фильтр по арендатору
        v = formData.getFirst("customer");
        if (v != null && !v.isEmpty()) {
            String finalV = v;
            rents = rents.stream()
                    .filter(rent -> rent.getCustomer().getId() == Long.valueOf(finalV))
                    .collect(Collectors.toList());
        }

        // Фильтр по точке
        v = formData.getFirst("office");
        if (v != null && !v.isEmpty()) {
            String finalV = v;
            rents = rents.stream()
                    .filter(rent -> rent.getOffice().getId() == Long.valueOf(finalV))
                    .collect(Collectors.toList());
        }

        // Фильтр по событию
        v = formData.getFirst("carEvent");
        if (v != null && !v.isEmpty()) {
            String finalV = v;
            rents = rents.stream()
                    .filter(rent -> rent.getCarEvent().toString().equalsIgnoreCase(finalV))
                    .collect(Collectors.toList());
        }

        // Фильтр по дате слева
        v = formData.getFirst("leftDate");
        if (v != null && !v.isEmpty()) {
            String finalV = v;
            rents = rents.stream()
                    .filter(rent -> compareDates(finalV, rent.getEventDate()))
                    .collect(Collectors.toList());
        }
        // Фильтр по дате справа
        v = formData.getFirst("rightDate");
        if (v != null && !v.isEmpty()) {
            String finalV = v;
            rents = rents.stream()
                    .filter(rent -> compareDates(rent.getEventDate(), finalV))
                    .collect(Collectors.toList());
        }

        prepareModel(model, rents);
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

    @GetMapping("/stats")
    public String showStats(Model model) {
        return "stats";
    }

    @PostMapping("/addrent")
    public String addRent(@Valid Rent rent, BindingResult result, Model model) throws ParseException {
        restTemplate.exchange(restUrl, HttpMethod.POST, new HttpEntity<Rent>(rent, null), String.class);

        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deleteRent(@PathVariable("id") long id, Model model) {
        restTemplate.exchange(restUrl + "/" + id, HttpMethod.DELETE, null, String.class);
        return "redirect:/";
    }
}
