package com.example.testjava8;



import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Comparator;
import java.util.stream.Stream;

public class Lamda {
    public static void main(String[] args) {
        Student[] sArr = {
                new Student("철수", 79)
                , new Student("영희", 95)
                , new Student("영수", 80)
                , new Student("민희", 85)
                , new Student("유리", 40)
                , new Student("민수", 75)};

        try (Stream<Student> filter_Arr = Stream.of(sArr).sorted(Comparator.comparing((Student s) -> s.getName()))) {
            filter_Arr.forEach(System.out::println);
        }

        LocalDate todate = LocalDate.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM");
        System.out.println(todate.minusMonths(1).format(format));

    }
}


class Student {
    private String name;
    private int jum;

    public Student(String name, int jum) {
        this.name = name;
        this.jum = jum;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getJum() {
        return jum;
    }
    public void setJum(int jum) {
        this.jum = jum;
    }

    @Override
    public String toString() {
        return "Student [name=" + name + ", jum=" + jum + "]";
    }
}
