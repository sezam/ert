package name.zamorin.ert.service;

import name.zamorin.ert.model.Rent;
import name.zamorin.ert.repo.RentRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final RentRepository rentRepo;

    @Autowired
    public ApiController(RentRepository rentRepo) {
        this.rentRepo = rentRepo;
    }

    // Список всех
    @GetMapping("/rents")
    public List getRents() {
        return rentRepo.findAll();
    }

    // Элемент по id
    @GetMapping("/rents/{id}")
    public Rent getRentById(@PathVariable(value = "id") Long id) {
        return rentRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid rent Id: " + id));
    }

    // Создать элемент
    @PostMapping("/rents")
    public Rent createRent(@Valid @RequestBody Rent rent) {
        return rentRepo.save(rent);
    }

    // Обновить запись по id
    @PutMapping("/rents/{id}")
    public Rent updateNote(@PathVariable(value = "id") Long id, @Valid @RequestBody Rent rentDetail) {
        Rent rent = rentRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid rent Id: " + id));
        BeanUtils.copyProperties(rent, rentDetail, "id");

        return rentRepo.save(rent);
    }

    // Удалить запись по id
    @DeleteMapping("/rents/{id}")
    public ResponseEntity deleteBook(@PathVariable(value = "id") Long id) {
        Rent rent = rentRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid rent Id: " + id));
        rentRepo.delete(rent);

        return ResponseEntity.ok().build();
    }
}
