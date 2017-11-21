package com.sweetjandy.remindr.repositories;


import com.sweetjandy.remindr.models.Remindr;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RemindrsRepository extends CrudRepository<Remindr, Long>{
    Remindr findByTitle(String title);
}
