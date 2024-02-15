package io.github.henriqtorresl.quarkussocial.domain.repository;

import io.github.henriqtorresl.quarkussocial.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped          // Essa annotation cria um instância de UserRepository para ficar de escopo de aplicação, é como se fosse um Singleton
public class UserRepository implements PanacheRepository<User> {

}