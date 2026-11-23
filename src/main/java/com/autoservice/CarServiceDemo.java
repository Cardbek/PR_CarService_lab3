package com.autoservice;

import com.autoservice.dto.RepairOrderDto;
import com.autoservice.dto.ServiceJobDto;
import com.autoservice.entity.Car;
import com.autoservice.entity.Client;
import com.autoservice.entity.Mechanic;
import com.autoservice.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CarServiceDemo implements CommandLineRunner {

    private final ClientService clientService;
    private final CarService carService;
    private final MechanicService mechanicService;
    private final RepairOrderService repairOrderService;
    private final ServiceJobService serviceJobService;

    public CarServiceDemo(ClientService clientService, CarService carService,
                          MechanicService mechanicService, RepairOrderService repairOrderService,
                          ServiceJobService serviceJobService) {
        this.clientService = clientService;
        this.carService = carService;
        this.mechanicService = mechanicService;
        this.repairOrderService = repairOrderService;
        this.serviceJobService = serviceJobService;
    }

    @Override
    public void run(String... args) {
        System.out.println("\n=== STARTING SPRING BOOT CAR SERVICE ===");

        // 1. Ініціалізація базових даних
        Client client = new Client("Ivan Petrenko", "+380991234567");
        client = clientService.saveClient(client);

        Car car = new Car("VIN123456789", "Toyota", "Camry", 2020);
        car.setOwner(client);
        carService.saveCar(car);

        Mechanic mechanic = new Mechanic("Alex");
        mechanic = mechanicService.saveMechanic(mechanic);

        System.out.println("Client, car, and mechanic saved to PostgreSQL.");

        // 2. Успішний сценарій
        System.out.println("\n=== SUCCESSFUL SCENARIO ===");
        RepairOrderDto order = repairOrderService.createOrder(car.getVin());
        System.out.println("Created order with ID: " + order.getId());

        ServiceJobDto job1 = new ServiceJobDto("Oil change", new BigDecimal("1500"), new BigDecimal("500"));
        ServiceJobDto job2 = new ServiceJobDto("Filter replacement", new BigDecimal("800"), new BigDecimal("300"));

        // Пакетне додавання робіт
        repairOrderService.addJobsBatch(order.getId(), List.of(job1, job2));
        System.out.println("Added 2 service jobs via batch.");

        repairOrderService.diagnoseOrder(order.getId());
        repairOrderService.approveOrder(order.getId());
        repairOrderService.startWork(order.getId(), mechanic.getId());
        System.out.println("Order in progress. Mechanic assigned.");

        // Отримуємо актуальне замовлення, щоб дістати ID робіт і завершити їх
        RepairOrderDto activeOrder = repairOrderService.getOrderById(order.getId());
        for (ServiceJobDto jobDto : activeOrder.getJobs()) {
            serviceJobService.markJobAsDone(jobDto.getId());
        }

        repairOrderService.completeOrder(order.getId());
        System.out.println("Order successfully completed!");

        BigDecimal basePrice = repairOrderService.calculateBasePrice(order.getId());
        BigDecimal commercialPrice = repairOrderService.calculateCommercialPrice(order.getId());
        System.out.println("Base Total Cost: " + basePrice + " UAH.");
        System.out.println("Commercial Price (with discounts): " + commercialPrice + " UAH.");

        // 3. Негативний сценарій (Rollback при пакетному додаванні)
        System.out.println("\n=== NEGATIVE SCENARIO (TRANSACTION ROLLBACK) ===");
        RepairOrderDto badOrder = repairOrderService.createOrder(car.getVin());

        ServiceJobDto validJob = new ServiceJobDto("Diagnostics", new BigDecimal("0"), new BigDecimal("500"));
        ServiceJobDto invalidJob = new ServiceJobDto("Invalid Job", new BigDecimal("-100"), new BigDecimal("500")); // Від'ємна ціна!

        try {
            repairOrderService.addJobsBatch(badOrder.getId(), List.of(validJob, invalidJob));
        } catch (Exception e) {
            System.out.println("Expected error caught during batch insert: " + e.getMessage());
        }

        RepairOrderDto checkBadOrder = repairOrderService.getOrderById(badOrder.getId());
        System.out.println("Jobs in bad order after rollback: " + checkBadOrder.getJobs().size() + " (Should be 0 due to rollback)");

        System.out.println("\n=== APP INITIALIZATION FINISHED ===\n");
    }
}