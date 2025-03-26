package com.PetSitter;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.ZoneId;
import java.util.TimeZone;

@SpringBootApplication
@EnableJpaAuditing // created_at, updated_at 자동 업데이트
public class PetSitterApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetSitterApplication.class, args);

		System.out.println("System TimeZone: " + TimeZone.getDefault().getID());
		System.out.println("Java 17 ZoneId: " + ZoneId.systemDefault());
	}

	@Bean
	JPAQueryFactory jpaQueryFactory(EntityManager em) {
		return new JPAQueryFactory(em);
	}

}
