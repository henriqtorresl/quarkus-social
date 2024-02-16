package io.github.henriqtorresl.quarkussocial.domain.repository;

import io.github.henriqtorresl.quarkussocial.domain.model.Follower;
import io.github.henriqtorresl.quarkussocial.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class FollowerRepository implements PanacheRepository<Follower> {

    public boolean follows(User follower, User user) {
        var params = Parameters.with("follower", follower).and("user", user).map();

//        -> Tambem poderia fazer usando o map do proprio java:
//        Map<String, Object> params = new HashMap<>();
//        params.put("follower", follower);
//        params.put("user", user);

        // os parametros tem que estar exatamente nesse formato: "=:parametro"
        PanacheQuery<Follower> query = find("follower =:follower and user =:user", params);
        Optional<Follower> result = query.firstResultOptional();  // retorna o indice 1 da lista da query dentro de um Opcional (a classe Opcional diz que pode ser que venha ou nao)

        return result.isPresent();
    }

    public List<Follower> findByUser(Long userId) {
        return find("user.id", userId).list();
    }

    public void deleteByFollowerAndUser(Long followerId, Long userId) {
        var params = Parameters
                .with("userId", userId)
                .and("followerId", followerId)
                .map();

        delete("follower.id =:followerId and user.id =:userId", params);
    }

}