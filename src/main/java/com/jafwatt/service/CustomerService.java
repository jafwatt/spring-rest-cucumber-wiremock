package com.jafwatt.service;

import com.jafwatt.model.CustomerRepository;
import com.jafwatt.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Service
public class CustomerService {

    private CustomerRepository customerRepository;

    private RestTemplate restTemplate;

    @Value("${third-party-web-service.url}")
    private String thirdPartyWebService;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, RestTemplate restTemplate) {
        this.customerRepository = customerRepository;
        this.restTemplate = restTemplate;
    }

    public Customer create(final Customer customer) {
        customer.setCreated(new Date());
        Customer savedCustomer = customerRepository.save(customer);
        restTemplate.getForObject(thirdPartyWebService, String.class);
        return savedCustomer;
    }
}
