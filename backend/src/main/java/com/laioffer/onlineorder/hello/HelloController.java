package com.laioffer.onlineorder.hello;




import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HelloController {


    @GetMapping("/hello")
    public Person sayHello(@RequestParam(required = false, defaultValue = "Guest") String name) {
        return new Person(
                name,
                "laioffer",
                new Address("123 Happy Street", "San Francisco", "California", null),
                new Book("Clean Code", "John Smith")
        );
    }
}
