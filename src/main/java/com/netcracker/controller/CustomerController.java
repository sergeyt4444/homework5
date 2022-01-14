package com.netcracker.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.Customer;
import com.netcracker.model.CustomerLastNameAndDiscountOnly;
import com.netcracker.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest")
public class CustomerController {

    @Autowired
    CustomerService customerService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/customers")
    public List<Customer> getAllCustomers() {
        return customerService.findAll();
    }

    @GetMapping("/customers/distinct_addresses")
    public List<String> getAllDistinctAddresses() {
        return customerService.findDistinctAddresses();
    }

    @GetMapping("/customers/nizhniy")
    public List<CustomerLastNameAndDiscountOnly> getAllNizhniyCustomers() {
        return customerService.findByAddress("Нижегородский район");
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable(value = "id")Integer id) throws ResourceNotFoundException {
        Customer customer = customerService.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Customer not found for id: " + id)
        );
        return ResponseEntity.ok(customer);
    }

    @DeleteMapping("/customers/{id}")
    public Map<String, Boolean> deleteCustomer(@PathVariable (value = "id")Integer id) throws ResourceNotFoundException {
        Customer customer = customerService.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Customer not found for id: " + id)
        );
        customerService.delete(customer);

        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", true);
        return response;
    }

    @PatchMapping(path = "/customers/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Integer id, @RequestBody JsonPatch patch)
            throws ResourceNotFoundException{
        try {
            Customer customer = customerService.findById(id).orElseThrow(
                    () -> new ResourceNotFoundException("Customer not found for id: " + id));
            Customer customerPatched = applyPatchToCustomer(patch, customer);
            customerService.addCustomer(customerPatched);
            return ResponseEntity.ok(customerPatched);
        } catch (JsonPatchException | JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Customer applyPatchToCustomer(
            JsonPatch patch, Customer targetCustomer) throws JsonPatchException, JsonProcessingException {
        JsonNode patched = patch.apply(objectMapper.convertValue(targetCustomer, JsonNode.class));
        return objectMapper.treeToValue(patched, Customer.class);
    }

    @PostMapping("/customers")
    public ResponseEntity<Customer> postCustomer(@RequestBody Customer customer) {
        Customer customerRes = this.customerService.addCustomer(customer);

        return new ResponseEntity<>(customerRes, HttpStatus.OK);
    }

    @PutMapping("/customers/{id}")
    Customer replaceCustomer(@RequestBody Customer customer, @PathVariable Integer id) {
        return customerService.findById(id)
                .map(customerRes -> {
                    customerRes.setAddress(customer.getAddress());
                    customerRes.setDiscount(customer.getDiscount());
                    return customerService.addCustomer(customerRes);
                })
                .orElseGet(() -> {
                    return customerService.addCustomer(customer);
                });
    }

}
