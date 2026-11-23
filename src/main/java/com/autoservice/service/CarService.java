package com.autoservice.service;

import com.autoservice.entity.Car;

import java.util.List;

public interface CarService {
    Car saveCar(Car car);

    Car getCarByVin(String vin);

    List<Car> getAllCars();
}