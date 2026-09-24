package com.dopamineshop.catalog.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DummyJsonSeeder {

    private final SeederService seederService;

    /**
     * Загрузка выполняется только при явном включении профиля seed.
     */
    @Bean
    @Profile("seed")
    public CommandLineRunner runSeeder() {
        return args -> {
            log.info("🌱 Starting DummyJSON seeding...");
            seederService.seedProducts();
            log.info("✅ Seeding completed!");
        };
    }
}
