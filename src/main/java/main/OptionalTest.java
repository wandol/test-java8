package main;

import java.util.Optional;

public class OptionalTest {
    static Insurance insurance;
    static Person person;

    public static void main(String[] args) {
        Optional<Insurance> optionalInsurance = Optional.ofNullable(insurance);
        Optional<String> name = optionalInsurance.map(Insurance::getName);

//        System.out.println(insurance.getName());
        System.out.println(name);

        Optional<Person> optperson = Optional.ofNullable(person);
        Optional<String> name2 = optperson.map(Person::getCar)
                .map(Car::getInsurance)
                .map(Insurance::getName);
        System.out.println(name2);
    }
}

class Person{
    private Car car;

    public Car getCar() {
        return car;
    }
}

class Car {
    private Insurance insurance;

    public Insurance getInsurance() {
        return insurance;
    }
}

class Insurance {
    private String name;

    public String getName() {
        return name;
    }
}