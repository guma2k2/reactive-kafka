package com.vinsguru.payment.application.service;

import com.vinsguru.common.events.payment.PaymentStatus;
import com.vinsguru.common.util.DuplicateEventValidator;
import com.vinsguru.payment.application.entity.Customer;
import com.vinsguru.payment.application.entity.CustomerPayment;
import com.vinsguru.payment.application.mapper.EntityDtoMapper;
import com.vinsguru.payment.application.repository.CustomerRepository;
import com.vinsguru.payment.application.repository.PaymentRepository;
import com.vinsguru.payment.common.dto.PaymentDto;
import com.vinsguru.payment.common.dto.PaymentProcessRequest;
import com.vinsguru.payment.common.exception.CustomerNotFoundException;
import com.vinsguru.payment.common.exception.InsufficientBalanceException;
import com.vinsguru.payment.common.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private static final Mono<Customer> CUSTOMER_NOT_FOUND = Mono.error(new CustomerNotFoundException());
    private static final Mono<Customer> INSUFFICIENT_BALANCE = Mono.error(new InsufficientBalanceException());


    public PaymentServiceImpl(PaymentRepository paymentRepository, CustomerRepository customerRepository) {
        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public Mono<PaymentDto> process(PaymentProcessRequest request) {
        return DuplicateEventValidator.validate(
                paymentRepository.existsByOrderId(request.orderId()),
                customerRepository.findById(request.customerId())
        ).switchIfEmpty(CUSTOMER_NOT_FOUND)
                .filter(customer -> customer.getBalance() >= request.amount())
                .switchIfEmpty(INSUFFICIENT_BALANCE)
                .flatMap(customer -> deductPayment(customer, request))
                .doOnNext(paymentDto -> log.info("payment processed for {}", paymentDto.orderId()));
    }

    private Mono<PaymentDto> deductPayment(Customer customer, PaymentProcessRequest request) {
        CustomerPayment customerPayment = EntityDtoMapper.toCustomerPayment(request);
        customer.setBalance(customer.getBalance() - request.amount());
        customerPayment.setStatus(PaymentStatus.DEDUCTED);
        return customerRepository.save(customer)
                .then(paymentRepository.save(customerPayment))
                .map(EntityDtoMapper::toDto);
    }

    @Override
    public Mono<PaymentDto> refund(UUID orderId) {
        return paymentRepository.findByOrderIdAndStatus(orderId, PaymentStatus.DEDUCTED)
                .zipWhen(customerPayment -> customerRepository.findById(customerPayment.getCustomerId()))
                .flatMap(t -> refundPayment(t.getT1(), t.getT2()))
                .doOnNext(paymentDto -> log.info("refunded amount {} for {}", paymentDto.amount(), paymentDto.orderId()));
    }

    private Mono<PaymentDto> refundPayment(CustomerPayment customerPayment, Customer customer) {
        customer.setBalance(customer.getBalance() + customerPayment.getAmount());
        customerPayment.setStatus(PaymentStatus.REFUNDED);
        return customerRepository.save(customer)
                .then(paymentRepository.save(customerPayment))
                .map(EntityDtoMapper::toDto);
    }
}
