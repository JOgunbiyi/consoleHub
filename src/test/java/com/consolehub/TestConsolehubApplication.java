package com.consolehub;

import org.springframework.boot.SpringApplication;

public class TestConsolehubApplication {

	public static void main(String[] args) {
		SpringApplication.from(ConsolehubApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
