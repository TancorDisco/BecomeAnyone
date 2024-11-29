package ru.sweetbun.becomeanyone.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sweetbun.becomeanyone.entity.Token;

@Repository
public interface TokenRepository extends CrudRepository<Token, String> {
}
