package com.adwaitkulkarni58.twitter;

import com.apple.eawt.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.*;

@SpringBootApplication
@ComponentScan(basePackages = "com.adwaitkulkarni58.twitter")
public class TwitterApplication {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SpringApplication.run(Application.class, args);
	}

}
