package com.autoservice.service.impl;

import com.autoservice.entity.Car;
import com.autoservice.repository.CarRepository;
import com.autoservice.service.CarService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    public CarServiceImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    public Car saveCar(Car car) {
        return carRepository.save(car);
    }

    @Override
    public Car getCarByVin(String vin) {
        return carRepository.findById(vin)
                .orElseThrow(() -> new IllegalArgumentException("Car with VIN " + vin + " not found."));
    }

    @Override
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }
}