package com.jafwatt.controller;

import com.jafwatt.service.CustomerService;
import com.jafwatt.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

@RestController
@RequestMapping("/customer")
@Consumes("application/json")
@Produces("application/json")
public class CustomerController {

    private CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<Customer> create(@RequestBody Customer customer) {
        ResponseEntity<Customer> responseEntity;
        try {
            Customer entity = customerService.create(customer);
            responseEntity = new ResponseEntity<>(entity, HttpStatus.OK);
        } catch (Exception e) {
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }
}
