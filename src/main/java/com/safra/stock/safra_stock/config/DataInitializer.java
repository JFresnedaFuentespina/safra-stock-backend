package com.safra.stock.safra_stock.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.safra.stock.safra_stock.entities.Role;
import com.safra.stock.safra_stock.repositories.RoleRepository;

@Configuration
public class DataInitializer {
    @Bean
    public ApplicationRunner initRoles(RoleRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                Role admin = new Role();
                admin.setName("ROLE_ADMIN");
                Role user = new Role();
                user.setName("ROLE_USER");
                repository.save(admin);
                repository.save(user);
                System.out.println(">>Roles creados en la bbdd!");
            } else {
                System.out.println(">>Roles ya existentes!");
            }
        };
    }
}
