package com.autoservice.service.impl;

import com.autoservice.entity.Car;
import com.autoservice.repository.CarRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

    @Mock private CarRepository carRepository;
    @InjectMocks private CarServiceImpl carService;

    @Test
    void saveCar_ValidCar_ReturnsSavedCar() {
        Car car = new Car("VIN123", "BMW", "X5", 2020);
        when(carRepository.save(car)).thenReturn(car);

        Car saved = carService.saveCar(car);

        assertNotNull(saved);
        assertEquals("VIN123", saved.getVin());
        verify(carRepository).save(car);
    }

    @Test
    void getCarByVin_ExistingVin_ReturnsCar() {
        Car car = new Car("VIN123", "BMW", "X5", 2020);
        when(carRepository.findById("VIN123")).thenReturn(Optional.of(car));

        Car found = carService.getCarByVin("VIN123");

        assertEquals("BMW", found.getBrand());
    }

    @Test
    void getCarByVin_NonExistingVin_ThrowsIllegalArgumentException() {
        when(carRepository.findById("BAD_VIN")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> carService.getCarByVin("BAD_VIN"));
    }

    @Test
    void getAllCars_ReturnsListOfCars() {
        when(carRepository.findAll()).thenReturn(List.of(new Car(), new Car()));

        List<Car> cars = carService.getAllCars();

        assertEquals(2, cars.size());
        verify(carRepository).findAll();
    }
}